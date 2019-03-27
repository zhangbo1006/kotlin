/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.script.KotlinScriptDefinition
import org.jetbrains.kotlin.script.KotlinScriptDefinitionFromAnnotatedTemplate
import org.jetbrains.kotlin.scripting.shared.definitions.KotlinScriptDefinitionAdapterFromNewAPI
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull
import java.io.File
import java.net.URLClassLoader
import kotlin.script.dependencies.Environment
import kotlin.script.experimental.api.KotlinType
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.host.configurationDependencies
import kotlin.script.experimental.host.createCompilationConfigurationFromTemplate
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration

interface ScriptDefinitionContributor {
    val id: String

    fun getDefinitions(): List<KotlinScriptDefinition>
    fun isReady() = true

    companion object {
        val EP_NAME: ExtensionPointName<ScriptDefinitionContributor> =
            ExtensionPointName.create<ScriptDefinitionContributor>("org.jetbrains.kotlin.scriptDefinitionContributor")

        inline fun <reified T> find(project: Project) =
            Extensions.getArea(project).getExtensionPoint(EP_NAME).extensions.filterIsInstance<T>().firstOrNull()
    }

}

private val LOG = Logger.getInstance("ScriptTemplatesProviders")
fun loadDefinitionsFromTemplates(
    templateClassNames: List<String>,
    templateClasspath: List<File>,
    environment: Environment = emptyMap(),
    // TODO: need to provide a way to specify this in compiler/repl .. etc
    /*
     * Allows to specify additional jars needed for DependenciesResolver (and not script template).
     * Script template dependencies naturally become (part of) dependencies of the script which is not always desired for resolver dependencies.
     * i.e. gradle resolver may depend on some jars that 'built.gradle.kts' files should not depend on.
     */
    additionalResolverClasspath: List<File> = emptyList()
): List<KotlinScriptDefinition> {
    val classpath = templateClasspath + additionalResolverClasspath
    LOG.info("[kts] loading script definitions $templateClassNames using cp: ${classpath.joinToString(
        File.pathSeparator
    )}")
    val baseLoader = ScriptDefinitionContributor::class.java.classLoader
    val loader = if (classpath.isEmpty()) baseLoader else URLClassLoader(
        classpath.map { it.toURI().toURL() }.toTypedArray(),
        baseLoader
    )

    return templateClassNames.mapNotNull { templateClassName ->
        try {
            // TODO: drop class loading here - it should be handled downstream
            // as a compatibility measure, the asm based reading of annotations should be implemented to filter classes before classloading
            val template = loader.loadClass(templateClassName).kotlin
            when {
                template.annotations.firstIsInstanceOrNull<org.jetbrains.kotlin.script.ScriptTemplateDefinition>() != null ||
                        template.annotations.firstIsInstanceOrNull<kotlin.script.templates.ScriptTemplateDefinition>() != null -> {
                    KotlinScriptDefinitionFromAnnotatedTemplate(
                        template,
                        environment,
                        templateClasspath
                    )
                }
                template.annotations.firstIsInstanceOrNull<kotlin.script.experimental.annotations.KotlinScript>() != null -> {
                    val hostConfiguration =
                        ScriptingHostConfiguration(defaultJvmScriptingHostConfiguration) {
                            configurationDependencies(JvmDependency(classpath))
                        }
                    KotlinScriptDefinitionAdapterFromNewAPI(
                        createCompilationConfigurationFromTemplate(
                            KotlinType(
                                template
                            ), hostConfiguration, KotlinScriptDefinition::class
                        ),
                        hostConfiguration
                    )
                }
                else -> {
                    LOG.warn("[kts] cannot find a valid script definition annotation on the class $template")
                    null
                }
            }
        } catch (e: ClassNotFoundException) {
            // Assuming that direct ClassNotFoundException is the result of versions mismatch and missing subsystems, e.g. gradle
            // so, it only results in warning, while other errors are severe misconfigurations, resulting it user-visible error
            LOG.warn("[kts] cannot load script definition class $templateClassName")
            null
        } catch (e: Throwable) {
            LOG.error("[kts] cannot load script definition class $templateClassName", e)
            null
        }
    }
}
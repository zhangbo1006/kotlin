/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script.definitions

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.config.CompilerSettings
import org.jetbrains.kotlin.idea.compiler.configuration.KotlinCompilerSettings
import org.jetbrains.kotlin.idea.compiler.configuration.KotlinCompilerSettingsListener
import org.jetbrains.kotlin.idea.core.script.ScriptDefinitionContributor
import org.jetbrains.kotlin.idea.core.script.loadDefinitionsFromTemplates
import org.jetbrains.kotlin.script.KotlinScriptDefinition
import java.io.File

class ScriptTemplatesFromCompilerSettingsProvider(
    private val project: Project,
    private val compilerSettings: KotlinCompilerSettings
) : ScriptDefinitionContributor {

    init {
        project.messageBus.connect().subscribe(KotlinCompilerSettingsListener.TOPIC, object : KotlinCompilerSettingsListener {
            override fun <T> settingsChanged(newSettings: T) {
                if (newSettings !is CompilerSettings) return

                ScriptDefinitionsManager.getInstance(project).reloadDefinitionsBy(this@ScriptTemplatesFromCompilerSettingsProvider)
            }
        })
    }

    override fun getDefinitions(): List<KotlinScriptDefinition> {
        val kotlinSettings = compilerSettings.settings
        return if (kotlinSettings.scriptTemplates.isBlank()) emptyList()
        else loadDefinitionsFromTemplates(
            templateClassNames = kotlinSettings.scriptTemplates.split(',', ' '),
            templateClasspath = kotlinSettings.scriptTemplatesClasspath.split(File.pathSeparator).map(::File),
            environment = mapOf(
                "projectRoot" to (project.basePath ?: project.baseDir.canonicalPath)?.let(::File)
            )
        )
    }

    override val id: String = "KotlinCompilerScriptTemplatesSettings"
}


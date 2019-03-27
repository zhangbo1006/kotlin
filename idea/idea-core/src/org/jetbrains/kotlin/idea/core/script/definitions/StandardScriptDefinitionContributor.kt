/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script.definitions

import com.intellij.execution.console.IdeConsoleRootType
import com.intellij.ide.scratch.ScratchFileService
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.JavaSdk
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.ex.PathUtilEx
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.idea.caches.project.SdkInfo
import org.jetbrains.kotlin.idea.caches.project.getScriptRelatedModuleInfo
import org.jetbrains.kotlin.idea.core.script.ScriptDefinitionContributor
import org.jetbrains.kotlin.script.KotlinScriptDefinition
import org.jetbrains.kotlin.utils.PathUtil
import java.io.File
import kotlin.script.dependencies.Environment
import kotlin.script.dependencies.ScriptContents
import kotlin.script.experimental.dependencies.DependenciesResolver
import kotlin.script.experimental.dependencies.ScriptDependencies
import kotlin.script.experimental.dependencies.asSuccess
import kotlin.script.experimental.jvm.util.scriptCompilationClasspathFromContextOrStdlib
import kotlin.script.templates.standard.ScriptTemplateWithArgs

class StandardScriptDefinitionContributor(project: Project) :
    ScriptDefinitionContributor {
    private val standardIdeScriptDefinition = StandardIdeScriptDefinition(project)

    override fun getDefinitions() = listOf(standardIdeScriptDefinition)

    override val id: String = "StandardKotlinScript"
}

class StandardIdeScriptDefinition internal constructor(project: Project) : KotlinScriptDefinition(
    ScriptTemplateWithArgs::class) {
    override val dependencyResolver = BundledKotlinScriptDependenciesResolver(project)
}

class BundledKotlinScriptDependenciesResolver(private val project: Project) :
    DependenciesResolver {
    override fun resolve(
        scriptContents: ScriptContents,
        environment: Environment
    ): DependenciesResolver.ResolveResult {
        val virtualFile = scriptContents.file?.let { VfsUtil.findFileByIoFile(it, true) }

        val javaHome = getScriptSDK(project, virtualFile)

        var classpath = with(PathUtil.kotlinPathsForIdeaPlugin) {
            listOf(reflectPath, stdlibPath, scriptRuntimePath)
        }
        if (ScratchFileService.getInstance().getRootType(virtualFile) is IdeConsoleRootType) {
            classpath = scriptCompilationClasspathFromContextOrStdlib(wholeClasspath = true) + classpath
        }

        return ScriptDependencies(javaHome = javaHome?.let(::File), classpath = classpath).asSuccess()
    }

    private fun getScriptSDK(project: Project, virtualFile: VirtualFile?): String? {
        if (virtualFile != null) {
            val dependentModuleSourceInfo = getScriptRelatedModuleInfo(project, virtualFile)
            val sdk = dependentModuleSourceInfo?.dependencies()?.filterIsInstance<SdkInfo>()?.singleOrNull()?.sdk
            if (sdk != null) {
                return sdk.homePath
            }
        }

        val jdk = ProjectRootManager.getInstance(project).projectSdk
            ?: ProjectJdkTable.getInstance().allJdks.firstOrNull { sdk -> sdk.sdkType is JavaSdk }
            ?: PathUtilEx.getAnyJdk(project)
        return jdk?.homePath
    }
}
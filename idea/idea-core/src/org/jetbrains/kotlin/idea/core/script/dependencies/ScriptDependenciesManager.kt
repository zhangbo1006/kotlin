/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script.dependencies

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.io.URLUtil
import org.jetbrains.annotations.TestOnly
import org.jetbrains.kotlin.idea.core.script.dependencies.loaders.SyncScriptDependenciesLoader
import org.jetbrains.kotlin.script.ScriptDependenciesProvider
import java.io.File
import kotlin.script.experimental.dependencies.ScriptDependencies


// NOTE: this service exists exclusively because ScriptDependencyManager
// cannot be registered as implementing two services (state would be duplicated)
class IdeScriptDependenciesProvider(
    private val scriptDependenciesManager: ScriptDependenciesManager
) : ScriptDependenciesProvider {
    override fun getScriptDependencies(file: VirtualFile): ScriptDependencies? {
        return scriptDependenciesManager.getScriptDependencies(file)
    }
}

class ScriptDependenciesManager internal constructor(
    private val cacheUpdater: ScriptDependenciesUpdater,
    private val cache: ScriptDependenciesCache
) {
    fun getScriptClasspath(file: VirtualFile): List<VirtualFile> = toVfsRoots(cacheUpdater.getCurrentDependencies(file).classpath)
    fun getScriptDependencies(file: VirtualFile): ScriptDependencies = cacheUpdater.getCurrentDependencies(file)

    fun getAllScriptsClasspathScope() = cache.allScriptsClasspathScope
    fun getAllLibrarySourcesScope() = cache.allLibrarySourcesScope
    fun getAllLibrarySources() = cache.allLibrarySources
    fun getAllScriptsClasspath() = cache.allScriptsClasspath

    companion object {
        @JvmStatic
        fun getInstance(project: Project): ScriptDependenciesManager =
            ServiceManager.getService(project, ScriptDependenciesManager::class.java)

        fun toVfsRoots(roots: Iterable<File>): List<VirtualFile> {
            return roots.mapNotNull { it.classpathEntryToVfs() }
        }

        private fun File.classpathEntryToVfs(): VirtualFile? {
            val res = when {
                !exists() -> null
                isDirectory -> StandardFileSystems.local()?.findFileByPath(this.canonicalPath)
                isFile -> StandardFileSystems.jar()?.findFileByPath(this.canonicalPath + URLUtil.JAR_SEPARATOR)
                else -> null
            }
            // TODO: report this somewhere, but do not throw: assert(res != null, { "Invalid classpath entry '$this': exists: ${exists()}, is directory: $isDirectory, is file: $isFile" })
            return res
        }

        internal val log = Logger.getInstance(ScriptDependenciesManager::class.java)

        @TestOnly
        fun updateScriptDependenciesSynchronously(virtualFile: VirtualFile, project: Project) {
            val loader = SyncScriptDependenciesLoader(project)
            loader.updateDependencies(virtualFile)
            loader.notifyRootsChanged()
        }
    }
}

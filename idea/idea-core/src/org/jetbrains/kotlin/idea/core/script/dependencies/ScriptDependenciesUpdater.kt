/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script.dependencies

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.util.Alarm
import org.jetbrains.annotations.TestOnly
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.core.script.definitions.ScriptDefinitionsManager
import org.jetbrains.kotlin.idea.core.script.dependencies.loaders.AsyncScriptDependenciesLoader
import org.jetbrains.kotlin.idea.core.script.dependencies.loaders.FromFileAttributeScriptDependenciesLoader
import org.jetbrains.kotlin.idea.core.script.dependencies.loaders.OutsiderFileDependenciesLoader
import org.jetbrains.kotlin.idea.core.script.dependencies.loaders.SyncScriptDependenciesLoader
import org.jetbrains.kotlin.idea.util.ProjectRootsUtil
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.NotNullableUserDataProperty
import org.jetbrains.kotlin.script.KotlinScriptDefinition
import org.jetbrains.kotlin.script.LegacyResolverWrapper
import org.jetbrains.kotlin.scripting.shared.definitions.findScriptDefinition
import kotlin.script.experimental.dependencies.AsyncDependenciesResolver
import kotlin.script.experimental.dependencies.ScriptDependencies

class ScriptDependenciesUpdater(
    private val project: Project,
    private val cache: ScriptDependenciesCache
) {
    private val scriptsQueue = Alarm(Alarm.ThreadToUse.SWING_THREAD, project)
    private val scriptChangesListenerDelay = 1400

    private val loaders = arrayListOf(
        FromFileAttributeScriptDependenciesLoader(project),
        OutsiderFileDependenciesLoader(project),
        AsyncScriptDependenciesLoader(project),
        SyncScriptDependenciesLoader(project)
    )

    init {
        listenForChangesInScripts()
    }

    fun getCurrentDependencies(file: VirtualFile): ScriptDependencies {
        cache[file]?.let { return it }

        updateDependencies(file)
        makeRootsChangeIfNeeded()

        return cache[file] ?: ScriptDependencies.Empty
    }

    fun updateDependenciesIfNeeded(files: List<VirtualFile>): Boolean {
        if (!ScriptDefinitionsManager.getInstance(project).isReady()) return false

        var areDependenciesUpdateStarted = false
        for (file in files) {
            if (!areDependenciesCached(file)) {
                areDependenciesUpdateStarted = true
                updateDependencies(file)
            }
        }

        if (areDependenciesUpdateStarted) {
            makeRootsChangeIfNeeded()
        }

        return areDependenciesUpdateStarted
    }

    private fun updateDependencies(file: VirtualFile) {
        loaders.filter { it.isApplicable(file) }.forEach { it.updateDependencies(file) }
    }

    private fun makeRootsChangeIfNeeded() {
        loaders.firstOrNull {
            it.notifyRootsChanged()
        }
    }

    private fun listenForChangesInScripts() {
        project.messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {
            override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
                runScriptDependenciesUpdateIfNeeded(file)
            }

            override fun selectionChanged(event: FileEditorManagerEvent) {
                event.newFile?.let { runScriptDependenciesUpdateIfNeeded(it) }
            }

            private fun runScriptDependenciesUpdateIfNeeded(file: VirtualFile) {
                if (!shouldStartUpdate(file)) return

                updateDependencies(file)
                makeRootsChangeIfNeeded()
            }
        })

        EditorFactory.getInstance().eventMulticaster.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {

                val document = event.document
                val file = FileDocumentManager.getInstance().getFile(document)?.takeIf { it.isInLocalFileSystem } ?: return
                if (!file.isValid) {
                    cache.delete(file)
                    return
                }

                if (!shouldStartUpdate(file)) return

                // only update dependencies for scripts that were touched recently
                if (cache[file] == null) {
                    return
                }

                scriptsQueue.cancelAllRequests()

                scriptsQueue.addRequest(
                    {
                        FileDocumentManager.getInstance().saveDocument(document)

                        updateDependencies(file)
                        makeRootsChangeIfNeeded()
                    },
                    scriptChangesListenerDelay,
                    true
                )
            }
        }, project.messageBus.connect())
    }

    private fun shouldStartUpdate(file: VirtualFile): Boolean {
        if (project.isDisposed || !file.isValid || file.fileType != KotlinFileType.INSTANCE) {
            return false
        }

        if (
            ApplicationManager.getApplication().isUnitTestMode &&
            ApplicationManager.getApplication().isScriptDependenciesUpdaterDisabled == true
        ) {
            return false
        }

        val ktFile = PsiManager.getInstance(project).findFile(file) as? KtFile ?: return false
        return ProjectRootsUtil.isInProjectSource(ktFile, includeScriptsOutsideSourceRoots = true)
    }

    private fun areDependenciesCached(file: VirtualFile): Boolean {
        return cache[file] != null || file.scriptDependencies != null
    }

    fun isAsyncDependencyResolver(scriptDef: KotlinScriptDefinition): Boolean {
        val dependencyResolver = scriptDef.dependencyResolver
        return dependencyResolver is AsyncDependenciesResolver || dependencyResolver is LegacyResolverWrapper
    }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): ScriptDependenciesUpdater =
            ServiceManager.getService(project, ScriptDependenciesUpdater::class.java)

        fun areDependenciesCached(file: KtFile): Boolean {
            return getInstance(file.project).areDependenciesCached(file.virtualFile)
        }

        fun isAsyncDependencyResolver(file: KtFile): Boolean {
            val scriptDefinition = file.virtualFile.findScriptDefinition(file.project) ?: return false
            return getInstance(file.project).isAsyncDependencyResolver(scriptDefinition)
        }
    }
}

@set: TestOnly
var Application.isScriptDependenciesUpdaterDisabled by NotNullableUserDataProperty(
    Key.create("SCRIPT_DEPENDENCIES_UPDATER_DISABLED"),
    false
)
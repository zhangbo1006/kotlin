/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script.definitions

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorNotifications
import com.intellij.util.containers.SLRUMap
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.core.script.ScriptDefinitionContributor
import org.jetbrains.kotlin.idea.core.script.ScriptDependenciesCache
import org.jetbrains.kotlin.idea.core.script.settings.KotlinScriptingSettings
import org.jetbrains.kotlin.script.KotlinScriptDefinition
import org.jetbrains.kotlin.script.ScriptDefinitionProvider
import org.jetbrains.kotlin.script.ScriptTemplatesProvider
import org.jetbrains.kotlin.scripting.shared.definitions.LazyScriptDefinitionProvider
import org.jetbrains.kotlin.utils.addToStdlib.flattenTo
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write

class ScriptDefinitionsManager(private val project: Project) : LazyScriptDefinitionProvider() {
    private var definitionsByContributor = mutableMapOf<ScriptDefinitionContributor, List<KotlinScriptDefinition>>()
    private var definitions: List<KotlinScriptDefinition>? = null

    private val failedContributorsHashes = HashSet<Int>()

    private val scriptDefinitionsCacheLock = ReentrantReadWriteLock()
    private val scriptDefinitionsCache = SLRUMap<String, KotlinScriptDefinition>(10, 10)

    override fun findScriptDefinition(fileName: String): KotlinScriptDefinition? {
        if (nonScriptFileName(fileName)) return null
        if (!isReady()) return null

        val cached = synchronized(scriptDefinitionsCacheLock) { scriptDefinitionsCache.get(fileName) }
        if (cached != null) return cached

        val definition = super.findScriptDefinition(fileName) ?: return null

        synchronized(scriptDefinitionsCacheLock) {
            scriptDefinitionsCache.put(fileName, definition)
        }

        return definition
    }

    fun reloadDefinitionsBy(contributor: ScriptDefinitionContributor) = lock.write {
        if (definitions == null) return // not loaded yet

        if (contributor !in definitionsByContributor) error("Unknown contributor: ${contributor.id}")

        definitionsByContributor[contributor] = contributor.safeGetDefinitions()

        definitions = definitionsByContributor.values.flattenTo(mutableListOf())

        updateDefinitions()
    }

    fun getDefinitionsBy(contributor: ScriptDefinitionContributor): List<KotlinScriptDefinition> = lock.write {
        if (definitions == null) return emptyList() // not loaded yet

        if (contributor !in definitionsByContributor) error("Unknown contributor: ${contributor.id}")

        return definitionsByContributor[contributor] ?: emptyList()
    }

    override val currentDefinitions: Sequence<KotlinScriptDefinition>
        get() =
            (definitions ?: kotlin.run {
                reloadScriptDefinitions()
                definitions!!
            }).asSequence().filter { KotlinScriptingSettings.getInstance(project).isScriptDefinitionEnabled(it) }

    private fun getContributors(): List<ScriptDefinitionContributor> {
        @Suppress("DEPRECATION")
        val fromDeprecatedEP = Extensions.getArea(project).getExtensionPoint(ScriptTemplatesProvider.EP_NAME).extensions.toList()
            .map(::ScriptTemplatesProviderAdapter)
        val fromNewEp = Extensions.getArea(project).getExtensionPoint(ScriptDefinitionContributor.EP_NAME).extensions.toList()
        return fromNewEp.dropLast(1) + fromDeprecatedEP + fromNewEp.last()
    }

    fun reloadScriptDefinitions() = lock.write {
        for (contributor in getContributors()) {
            val definitions = contributor.safeGetDefinitions()
            definitionsByContributor[contributor] = definitions
        }

        definitions = definitionsByContributor.values.flattenTo(mutableListOf())

        updateDefinitions()
    }

    fun reorderScriptDefinitions() = lock.write {
        updateDefinitions()
    }

    fun getAllDefinitions(): List<KotlinScriptDefinition> {
        return definitions ?: kotlin.run {
            reloadScriptDefinitions()
            definitions!!
        }
    }

    fun isReady(): Boolean {
        return definitionsByContributor.keys.all { contributor ->
            contributor.isReady()
        }
    }

    override fun getDefaultScriptDefinition(): KotlinScriptDefinition {
        val standardScriptDefinitionContributor = ScriptDefinitionContributor.find<StandardScriptDefinitionContributor>(project)
            ?: error("StandardScriptDefinitionContributor should be registered is plugin.xml")
        return standardScriptDefinitionContributor.getDefinitions().last()
    }

    private fun updateDefinitions() {
        assert(lock.isWriteLocked) { "updateDefinitions should only be called under the write lock" }

        definitions = definitions?.sortedBy {
            KotlinScriptingSettings.getInstance(project).getScriptDefinitionOrder(it)
        }

        val fileTypeManager = FileTypeManager.getInstance()

        val newExtensions = getKnownFilenameExtensions().filter {
            fileTypeManager.getFileTypeByExtension(it) != KotlinFileType.INSTANCE
        }.toList()

        if (newExtensions.any()) {
            // Register new file extensions
            ApplicationManager.getApplication().invokeLater {
                runWriteAction {
                    newExtensions.forEach {
                        fileTypeManager.associateExtension(KotlinFileType.INSTANCE, it)
                    }
                }
            }
        }

        clearCache()
        scriptDefinitionsCache.clear()

        // TODO: clear by script type/definition
        ServiceManager.getService(project, ScriptDependenciesCache::class.java).clear()

        ApplicationManager.getApplication().invokeLater {
            if (!project.isDisposed) {
                EditorNotifications.getInstance(project).updateAllNotifications()
            }
        }
    }

    private fun ScriptDefinitionContributor.safeGetDefinitions(): List<KotlinScriptDefinition> {
        if (!failedContributorsHashes.contains(this@safeGetDefinitions.hashCode())) try {
            return getDefinitions()
        } catch (t: Throwable) {
            // reporting failed loading only once
            LOG.error("[kts] cannot load script definitions using $this", t)
            failedContributorsHashes.add(this@safeGetDefinitions.hashCode())
        }
        return emptyList()
    }

    companion object {
        private val LOG = Logger.getInstance("org.jetbrains.kotlin.idea.core.script.definitions.ScriptDefinitionsManager")

        fun getInstance(project: Project): ScriptDefinitionsManager =
            ServiceManager.getService(project, ScriptDefinitionProvider::class.java) as ScriptDefinitionsManager
    }
}



/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script.dependencies.loaders

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.idea.core.script.dependencies.scriptDependencies

class FromFileAttributeScriptDependenciesLoader(project: Project) : ScriptDependenciesLoader(project) {

    override fun isApplicable(file: VirtualFile): Boolean {
        return file.scriptDependencies != null && cache[file] == null
    }

    override fun loadDependencies(file: VirtualFile) {
        val deserializedDependencies = file.scriptDependencies ?: return
        saveDependenciesToCache(file, deserializedDependencies)
    }

    override fun shouldShowNotification(): Boolean = false
}
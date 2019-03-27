/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.script.KotlinScriptDefinition

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
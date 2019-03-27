/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script.dependencies

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SimpleModificationTracker

class ScriptDependenciesModificationTracker(): SimpleModificationTracker() {

    companion object {
        @JvmStatic
        fun getInstance(project: Project): ScriptDependenciesModificationTracker =
                ServiceManager.getService(project, ScriptDependenciesModificationTracker::class.java)!!
    }
}
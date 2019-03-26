/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.configuration

import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.project.ModuleData

object KotlinMPPTestSourceRootPatcher {
    @Suppress("UNUSED_PARAMETER")
    fun patchTestDataSources(ideModule: DataNode<ModuleData>) {
        // Not needed for IDEA 183 and older versions
    }
}
/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.utils

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

private fun findSupertypeByName(fromClass: Class<*>, supertypeFqName: String): Boolean {
    if (fromClass.canonicalName == supertypeFqName) {
        return true
    }
    if (fromClass.superclass != null && findSupertypeByName(fromClass.superclass, supertypeFqName)) {
        return true
    }
    if (fromClass.interfaces.any { iface -> findSupertypeByName(iface, supertypeFqName) }) {
        return true
    }
    return false
}

fun checkOtherProjectAppliesKotlinPluginFromSameClasses(thisProject: Project, otherProject: Project): Boolean {
    val otherProjectKotlinExtension = otherProject.extensions.findByName("kotlin")
        ?: return false

    if (otherProjectKotlinExtension is KotlinProjectExtension)
        return true

    // Check that the other project extension's class is really the KotlinProjectExtension class loaded by a different class loader:
    if (findSupertypeByName(otherProjectKotlinExtension.javaClass, KotlinProjectExtension::class.java.canonicalName)) {
        SingleWarningPerBuild.show(thisProject.rootProject, MULTIPLE_KOTLIN_PLUGINS_LOADED_WARNING)
        SingleWarningPerBuild.show(thisProject.rootProject, "$MULTIPLE_KOTLIN_PLUGINS_SPECIFIC_PROJECT_WARNING '${thisProject.path}'")
        SingleWarningPerBuild.show(thisProject.rootProject, "$MULTIPLE_KOTLIN_PLUGINS_SPECIFIC_PROJECT_WARNING '${otherProject.path}'")
    }

    return false
}

const val MULTIPLE_KOTLIN_PLUGINS_LOADED_WARNING: String =
    "The Kotlin Gradle plugin was loaded multiple times in different subprojects. It is not supported and may break the build. \n" +
            "This might happen in subprojects that apply the Kotlin plugins with the Gradle 'plugins { ... }' DSL " +
            "if they specify explicit versions independently, even if the versions are equal.\n" +
            "Please add the Kotlin plugin to the common parent project or the " +
            "root project, then remove the version in the subprojects.\n" +
            "If the parent project does not need the plugin, add 'apply false' to the plugin line.\n" +
            "See: https://docs.gradle.org/current/userguide/plugins.html#sec:subprojects_plugins_dsl\n" +
            "The detected conflicting subprojects are reported below, marked with (*)."

const val MULTIPLE_KOTLIN_PLUGINS_SPECIFIC_PROJECT_WARNING: String =
    "(*) Check the Kotlin plugin applied in project"
package org.jetbrains.kotlin.gradle.targets.js.nodejs

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExtension.Companion.NODE_JS

open class NodeJsPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        this.extensions.create(NODE_JS, NodeJsExtension::class.java, this)
        tasks.create(NodeJsSetupTask.NAME, NodeJsSetupTask::class.java)
    }

    companion object {
        fun ensureAppliedInHierarchy(myProject: Project): Project {
            var project : Project? = myProject
            while (project != null) {
                if (myProject.plugins.hasPlugin(NodeJsPlugin::class.java)) return project
                project = project.parent
            }

            myProject.pluginManager.apply(NodeJsPlugin::class.java)
            return myProject
        }
    }
}

description = "Maven artifact for Kotlin plugin"

plugins {
    base
}

repositories {
    maven("https://jetbrains.bintray.com/markdown")
}

val fatJar by configurations.creating
val compileDependencies by configurations.creating

dependencies {
    listOf<String>().forEach {
        fatJar(project(it)) { isTransitive = false }
    }
}

val jar = tasks.register("jar", Jar::class.java) {
    from {
        fatJar.filter { it.extension == "jar" }.map(::zipTree)
    }
}

val listIdeaPluginProjects = tasks.register("listIdeaPluginProjects") {
    doLast {
        rootProject.allprojects
            .filter { project -> project.tasks.any { it.name == "ideaPlugin" } }
            .joinToString(prefix = "\"", postfix = "\"") { it.path }
            .also { 
                println(it)
            }
    }
}
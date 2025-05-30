rootProject.name = "SplitPlugin"
include("shared")
include("frontend")
include("backend")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
    }
}
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
    id("intellij-platform-remdev")
}

dependencies {
    implementation(project(":shared"))
    intellijPlatform {
        create(IntelliJPlatformType.IntellijIdeaUltimate, libs.versions.ij.platform, useInstaller = false)
        pluginModule(project(":shared"))
    }
}
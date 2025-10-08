import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
    id("intellij-platform-remdev")
}

dependencies {
    intellijPlatform {
        create(IntelliJPlatformType.IntellijIdeaUltimate.code, libs.versions.ij.platform, useInstaller = false)
        pluginModule(implementation(project(":shared")))
    }
}
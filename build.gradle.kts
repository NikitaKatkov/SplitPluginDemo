import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.tasks.aware.SplitModeAware.SplitModeTarget

plugins {
    id("intellij-platform-main")
}

group = "com.example"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":shared"))
    implementation(project(":frontend"))
    implementation(project(":backend"))

    intellijPlatform {
        create(IntelliJPlatformType.IntellijIdeaUltimate, libs.versions.ij.platform, useInstaller = false)
        pluginModule(project(":shared"))
        pluginModule(project(":frontend"))
        pluginModule(project(":backend"))
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
    }
}

intellijPlatform {
    splitMode = true
    splitModeTarget = SplitModeTarget.BOTH
}

tasks {
    @Suppress("unused")
    val runIdeaInRemoteDevMode by intellijPlatformTesting.runIde.registering {
        type = IntelliJPlatformType.IntellijIdeaUltimate
    }
}
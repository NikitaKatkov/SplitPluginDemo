import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.tasks.aware.SplitModeAware.SplitModeTarget

plugins {
    id("intellij-platform-main")
}

group = "com.example"
version = "1.0-SNAPSHOT"


dependencies {
    intellijPlatform {
        create("IU", "2025.1", useInstaller = false)
        pluginModule(implementation(project(":shared")))
        pluginModule(implementation(project(":frontend")))
        pluginModule(implementation(project(":backend")))
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
    }
}

intellijPlatform {
    splitMode = true
    splitModeTarget = SplitModeTarget.BOTH

    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }

        changeNotes = """
            Initial version
        """.trimIndent()
    }
}

tasks {
    @Suppress("unused") 
    val runIdeaInRemoteDevMode by intellijPlatformTesting.runIde.registering {
        type = IntelliJPlatformType.IntellijIdeaUltimate

        splitMode = true
        splitModeTarget = SplitModeTarget.BOTH
    }
}
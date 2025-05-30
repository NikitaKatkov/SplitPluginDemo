import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.tasks.aware.SplitModeAware.SplitModeTarget

plugins {
    application
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.10"
    id("org.jetbrains.intellij.platform") version "2.6.0"
    id("rpc") version "2.1.10-0.2"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    intellijPlatform {
        snapshots()
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create("IU", "2025.1", useInstaller = false)
        pluginModule(implementation(project(":frontend")))
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

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    prepareJarSearchableOptions {
        enabled = false
    }

    val runIdeaInRemoteDevMode by intellijPlatformTesting.runIde.registering {
        type = IntelliJPlatformType.IntellijIdeaUltimate

        splitMode = true
        splitModeTarget = SplitModeTarget.BOTH

        task {
            setJvmArgs(
                mutableListOf(
                    "-Didea.kotlin.plugin.use.k2=true",
                )
            )
        }
    }
}
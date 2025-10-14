import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

version = ""

plugins {
    id("intellij-platform-remdev")
}

dependencies {
    intellijPlatform {
        create(IntelliJPlatformType.IntellijIdeaUltimate, libs.versions.ij.platform) {
            useInstaller = false
        }
        (implementation(project(":shared")) {
            exclude("org.jetbrains.kotlin", "kotlin-stdlib")
        })
        bundledModule("intellij.platform.rpc.backend")
    }
}

tasks {
    jar {
        archiveFileName.set("SplitPlugin.backend.jar")
    }
}
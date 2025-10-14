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
        pluginModule(implementation(project(":shared")))
        bundledModule("intellij.platform.frontend")
    }
}

tasks {
    jar {
        archiveFileName.set("SplitPlugin.frontend.jar")
    }
}
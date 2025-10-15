import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

version = "1.0-SNAPSHOT"
plugins {
    id("intellij-platform-remdev")
}
dependencies {
    intellijPlatform {
        create(IntelliJPlatformType.IntellijIdeaUltimate, libs.versions.ij.platform) {
            useInstaller = false
        }
    }
}

tasks {
    jar {
        archiveFileName.set("SplitPlugin.shared.jar")
    }
}
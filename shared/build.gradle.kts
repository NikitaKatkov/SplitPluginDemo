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
    }
}

import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.ProductMode

plugins {
    id("intellij-platform-remdev")
}

dependencies {
    intellijPlatform {
        create(IntelliJPlatformType.IntellijIdeaUltimate, libs.versions.ij.platform) {
            useInstaller = false
            productMode = ProductMode.FRONTEND
        }
        pluginModule(implementation(project(":shared")))
    }
}
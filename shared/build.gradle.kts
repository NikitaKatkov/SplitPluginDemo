plugins {
    application
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

dependencies {
    intellijPlatform {
        create("IU", "252.18003-EAP-CANDIDATE-SNAPSHOT", useInstaller = false)
    }
}
plugins {
    id("intellij-platform-module")
}

dependencies {
    intellijPlatform {
        create("IU", "2025.1", useInstaller = false)
    }
}
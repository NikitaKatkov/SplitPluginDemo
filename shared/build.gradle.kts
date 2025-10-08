plugins {
    id("intellij-platform-remdev")
}

dependencies {
    intellijPlatform {
        create("IU", "2025.1", useInstaller = false)
    }
}
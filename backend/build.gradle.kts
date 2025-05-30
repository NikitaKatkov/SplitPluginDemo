plugins {
    application
    id("rpc") version "2.1.10-0.2"
    id("org.jetbrains.intellij.platform") version "2.6.0"
}

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

        pluginModule(implementation(project(":shared")))
    }
}

tasks {
    prepareJarSearchableOptions {
        enabled = false
    }
}
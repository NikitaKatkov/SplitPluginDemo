plugins {
    application
    id("rpc") version "2.1.10-0.2"
    id("org.jetbrains.intellij.platform") version "2.6.0"
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
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
    }
    implementation(kotlin("stdlib-jdk8"))

    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.7.3")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.7.3")
}

tasks {
    prepareJarSearchableOptions {
        enabled = false
    }
    buildSearchableOptions {
        enabled = false
    }
}

kotlin {
    jvmToolchain(21)
}
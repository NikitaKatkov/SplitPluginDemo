plugins {
    application
    id("rpc") version "2.1.10-0.2"
    id("org.jetbrains.intellij.platform") version "2.6.0"
    kotlin("jvm")
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
        bundledModule("intellij.platform.rpc.backend")
    }
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    prepareJarSearchableOptions {
        enabled = false
    }
}
kotlin {
    jvmToolchain(21)
}
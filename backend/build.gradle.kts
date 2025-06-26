plugins {
    application
    alias(libs.plugins.ij.rpc.plugin)
    alias(libs.plugins.ij.gradle.plugin)
    alias(libs.plugins.ij.kotlin.jvm.plugin)
    alias(libs.plugins.ij.kotlin.serialization.plugin)
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
        bundledPlugin("com.jetbrains.codeWithMe")
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
plugins {
    application
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("rpc")
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:${SharedCompileDependencies.KOTLIN_SERIALIZATION_LIBRARY}")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:${SharedCompileDependencies.KOTLIN_SERIALIZATION_LIBRARY}")
}

intellijPlatform {
    buildSearchableOptions = false
}

kotlin {
    jvmToolchain(SharedCompileDependencies.JAVA_LANGUAGE_VERSION)
}
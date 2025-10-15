plugins {
    id("org.jetbrains.intellij.platform.module")
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
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:${SharedCompileDependencies.KOTLIN_SERIALIZATION_LIBRARY}")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:${SharedCompileDependencies.KOTLIN_SERIALIZATION_LIBRARY}")
}

intellijPlatform {
    buildSearchableOptions = false
}

kotlin {
    jvmToolchain(SharedCompileDependencies.JAVA_LANGUAGE_VERSION)
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "${SharedCompileDependencies.JAVA_LANGUAGE_VERSION}"
        targetCompatibility = "${SharedCompileDependencies.JAVA_LANGUAGE_VERSION}"
    }
}
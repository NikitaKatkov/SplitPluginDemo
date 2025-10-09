import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
    application
    id("org.jetbrains.intellij.platform")
    id("java")
}

repositories {
    mavenCentral()
    intellijPlatform {
        snapshots()
        defaultRepositories()
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "${SharedCompileDependencies.JAVA_LANGUAGE_VERSION}"
        targetCompatibility = "${SharedCompileDependencies.JAVA_LANGUAGE_VERSION}"
    }

    prepareJarSearchableOptions {
        enabled = false
    }
    buildSearchableOptions {
        enabled = false
    }
}
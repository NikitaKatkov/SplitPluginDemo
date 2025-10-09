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
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    prepareJarSearchableOptions {
        enabled = false
    }
    buildSearchableOptions {
        enabled = false
    }
}
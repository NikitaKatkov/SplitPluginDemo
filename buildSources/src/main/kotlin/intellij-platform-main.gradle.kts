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
}

intellijPlatform {
    buildSearchableOptions = false
}

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
}

dependencies {
    implementation("org.jetbrains.intellij.platform:intellij-platform-gradle-plugin:2.6.0")
    implementation("rpc:rpc.gradle.plugin:2.1.10-0.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
    implementation("org.jetbrains.kotlin:kotlin-serialization:2.1.10")
}
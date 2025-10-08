plugins {
    id("intellij-platform-remdev")
}


dependencies {
    intellijPlatform {
        create("IU", "252.23892-EAP-CANDIDATE-SNAPSHOT", useInstaller = false)

        pluginModule(implementation(project(":shared")))
        bundledModule("intellij.platform.rpc.backend")
        bundledPlugin("com.jetbrains.codeWithMe")
    }
}


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

fun patchXmlRawExample() {
    val targetPlatformVersion = TODO("get as input paramter or as a property from libs.toml file")

    val moduleXmlFileName = "SplitPlugin.frontend.main.xml"
    val moduleXmlFile = File(resourcesDir.get().asFile, moduleXmlFileName)

    if (!moduleXmlFile.exists()) {
        throw GradleException("Backend module should have xml in resources folder with name $moduleXmlFileName")
    }

    val saxBuilder = SAXBuilder()
    val document = saxBuilder.build(moduleXmlFile)
    val root = document.rootElement

    if (root.getChild("dependencies") != null) {
        root.addContent(Element("dependencies"))
    }
    val dependencies = root.getChild("dependencies")!!

    if (targetPlatformVersion == "251" || targetPlatformVersion == "243") {
        addDependency(dependencies, "com.intellij.platform.experimental.frontend", DependencyKind.PLUGIN)
    } else {
        // this module dependency was introduced in 252
        addDependency(dependencies, "intellij.platform.frontend", DependencyKind.MODULE)
    }


    val format = Format.getPrettyFormat().apply {
        omitDeclaration = true
    }

    val outputter = XMLOutputter(format)
    moduleXmlFile.writeText(outputter.outputString(document))
}

private fun addDependency(
    dependenciesElement: Element,
    dependencyName: String,
    dependencyKind: DependencyKind) {
    val dependencyKindName = when (dependencyKind) {
        DependencyKind.MODULE -> "module"
        DependencyKind.PLUGIN -> "plugin"
    }

    val alreadyExists = dependenciesElement.getChildren(dependencyKindName).any { module ->
        module.getAttributeValue("name") == dependencyName
    }

    if (alreadyExists) {
        return
    }

    val newModule = Element(dependencyKindName).apply {
        setAttribute("name", dependencyName)
    }

    dependenciesElement.addContent(newModule)
}

private enum class DependencyKind {
    MODULE,
    PLUGIN
}
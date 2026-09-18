import java.util.Properties

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "BuildABrowser Droided"
include(":app")
include(":PainterAndroid")
include(":EmbeddingAndroid")
project(":PainterAndroid").projectDir = file("Painter/Android")
project(":EmbeddingAndroid").projectDir = file("Embedding/Android")

val babPath: String? = providers.gradleProperty("bab.dir").orNull
    ?: providers.environmentVariable("BAB_DIR").orNull
    ?: file("local.properties").takeIf { it.exists() }?.let { propFile ->
        Properties().apply { propFile.inputStream().use { load(it) } }.getProperty("bab.dir")
    }

val babDir = babPath?.let { file(it) }

if (babDir != null && babDir.isDirectory) {
    includeBuild(babDir) {
        dependencySubstitution {
            substitute(module("com.github.BuildABrowser.BuildABrowser:PainterCore")).using(project(":PainterCore"))
            substitute(module("com.github.BuildABrowser.BuildABrowser:Renderer")).using(project(":Renderer"))
            substitute(module("com.github.BuildABrowser.BuildABrowser:Common")).using(project(":Common"))
        }
    }
}
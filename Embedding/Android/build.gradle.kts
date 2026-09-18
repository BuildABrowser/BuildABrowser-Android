plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
}

android {
    namespace = "net.buildabrowser.babbrowser.embedding.android"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("noAssertions") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks.add("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

@Suppress("UnstableApiUsage")
val withResourcesScope = configurations.dependencyScope("withResourcesScope")

@Suppress("UnstableApiUsage")
val withResources = configurations.resolvable("withResources") {
    extendsFrom(withResourcesScope.get())
}

dependencies {
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.ui)
    "withResourcesScope"(libs.renderer)
    api(libs.renderer)
    implementation(project(":Painter:Android"))
    implementation(libs.okhttp)
    implementation(libs.slf4j.api)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}

androidComponents {
    onVariants { variant ->
        val extractAssets = tasks.register<ExtractAssetsTask>("extractAssets_${variant.name}") {
            description = "Extract assets from the Java dependencies for Android"
            zipFiles.from(withResources)
        }
        variant.sources.assets?.addGeneratedSourceDirectory(extractAssets, ExtractAssetsTask::outputDirectory)
    }
}

abstract class ExtractAssetsTask : DefaultTask() {
    @get:InputFiles abstract val zipFiles: ConfigurableFileCollection
    @get:OutputDirectory abstract val outputDirectory: DirectoryProperty

    @get:Inject abstract val fs: FileSystemOperations
    @get:Inject abstract val archives: ArchiveOperations

    @TaskAction
    fun extract() = fs.copy {
        zipFiles.forEach { from(archives.zipTree(it)) { include("ua/**") } }
        into(outputDirectory)
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "net.buildabrowser.BuildABrowser"
            artifactId = "EmbeddingAndroid"
            version = "0.1.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
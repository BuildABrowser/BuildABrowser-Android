plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "net.buildabrowser.droided"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "net.buildabrowser.droided"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
}

val withResources by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    withResources(libs.renderer)

    implementation(libs.renderer)
    implementation(libs.okhttp)
    implementation(libs.slf4j.api)
    implementation("com.github.taucher2003:t2003-logger-impl:1.0.2")
    implementation("com.github.taucher2003:t2003-logger-binder:1.0.2")

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

androidComponents {
    onVariants { variant ->
        val extractAssets = tasks.register<ExtractAssetsTask>("extractAssets_${variant.name}") {
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
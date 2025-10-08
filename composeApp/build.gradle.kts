import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.googleServices)
}

kotlin {
    androidTarget {
        compilerOptions.jvmTarget.set(JvmTarget.fromTarget(Constants.javaVersion.toString()))
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            // implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:34.2.0"))
            implementation("com.google.firebase:firebase-messaging")
            implementation(libs.worker)
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.content.negotiation)
            with(compose) {
                implementation(runtime)
                implementation(foundation)
                implementation(material3)
                implementation(ui)
                implementation(components.resources)
                implementation(components.uiToolingPreview)
            }
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.decompose)
            implementation(libs.decompose.compose)
            implementation(libs.media.player)
            implementation(libs.media.frame.kit)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = Constants.PACKAGE_NAME
    compileSdk = Constants.VERSION_SDK_COMPILE

    defaultConfig {
        applicationId = Constants.PACKAGE_NAME
        minSdk = Constants.VERSION_SDK_MIN
        targetSdk = Constants.VERSION_SDK_COMPILE
        versionCode = Constants.VERSION_CODE
        versionName = Constants.VERSION_NAME
    }
    packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = Constants.javaVersion
        targetCompatibility = Constants.javaVersion
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}


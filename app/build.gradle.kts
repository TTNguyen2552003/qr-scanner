plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "app.kotlin.qrscanner"
    compileSdk = 34

    defaultConfig {
        applicationId = "app.kotlin.qrscanner"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val zxingCoreVersion = "3.5.3"
    implementation("com.google.zxing:core:$zxingCoreVersion")

    val workVersion = "2.9.0"
    implementation("androidx.work:work-runtime-ktx:$workVersion")

    val lifecycleVersion = "2.8.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

    val barcodeScanningVersion = "17.2.0"
    implementation("com.google.mlkit:barcode-scanning:$barcodeScanningVersion")

    val coilVersion = "2.6.0"
    implementation("io.coil-kt:coil-compose:$coilVersion")

    val animatedVectorCompose = "1.0.0"
    implementation("androidx.compose.animation:animation-graphics:$animatedVectorCompose")

    val accompanistPermissionVersion = "0.35.1-alpha"
    implementation("com.google.accompanist:accompanist-permissions:$accompanistPermissionVersion")

    val splashScreenVersion = "1.0.0"
    implementation("androidx.core:core-splashscreen:$splashScreenVersion")

    val cameraxVersion = "1.4.0-beta01"
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation("androidx.camera:camera-extensions:${cameraxVersion}")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
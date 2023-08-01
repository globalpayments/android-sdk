import java.io.DataInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "1.7.20"
}

val configProperties = Properties()
if (project.file("local.properties").exists()) {
    configProperties.load(DataInputStream(project.file("local.properties").inputStream()))
} else {
    configProperties.load(DataInputStream(project.file("configuration.properties").inputStream()))
}
val appId: String = configProperties.getProperty("appId")
val appKey: String = configProperties.getProperty("appKey")
val serverUrl: String = configProperties.getProperty("serverUrl")
val preferDecoupledFlow: String = configProperties.getProperty("preferDecoupledFlow")
val authTimeout: String = configProperties.getProperty("authTimeout")

android {
    namespace = "com.globalpatments.android.sdk.merchant3ds"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.globalpatments.android.sdk.merchant3ds"
        minSdk = 21
        targetSdk = 33
        versionCode = 2
        versionName = "1.1.27"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "APP_ID", "\"$appId\"")
        buildConfigField("String", "APP_KEY", "\"$appKey\"")
        buildConfigField("String", "SERVER_URL", "\"$serverUrl\"")
        buildConfigField("Boolean", "PREFER_DECOUPLED_FLOW", preferDecoupledFlow)
        buildConfigField("int", "AUTH_TIMEOUT", authTimeout)
    }

    buildFeatures.buildConfig = true


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val accompanist = "0.26.5-rc"

    implementation(files("libs/3ds-sdk.aar"))

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.6.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.05.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.compose.material:material")
    implementation("com.google.accompanist:accompanist-navigation-animation:$accompanist")
    implementation("com.google.accompanist:accompanist-webview:$accompanist")
    implementation("androidx.webkit:webkit:1.7.0")
    implementation("io.coil-kt:coil-compose:2.2.2")


    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    implementation("com.google.android.gms:play-services-base:18.2.0")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("org.bouncycastle:bcprov-ext-jdk18on:1.71")
    implementation("org.bitbucket.b_c:jose4j:0.7.12")

    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}

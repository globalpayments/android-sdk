plugins {
    id("globalpayments.android.application")
    id("globalpayments.android.application.compose")
    id("globalpayments.sampleapp.config")
    id("kotlinx-serialization")
    id("kotlin-parcelize")
}

android {
    namespace = "com.globalpayments.android.sdk.sample"

    defaultConfig {
        applicationId = "com.globalpayments.android.sdk.sample"
        versionCode = 1
        versionName = libs.versions.globalPayments.android.get()

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures.buildConfig = true

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/*"
        }
    }
}

dependencies {
    implementation(files("libs/3ds-sdk.aar"))
    implementation(project(":globalpayments-android-sdk"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.webkit)
    implementation(libs.material)

    //Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.accompanist.webview)
    implementation(libs.google.pay.button)
    implementation(libs.play.services.base)
    implementation(libs.play.services.wallet)

    implementation(libs.slf4j.simple)
    implementation(libs.bouncyCastle)
    implementation(libs.jose4j)
}

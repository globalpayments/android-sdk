plugins {
    id("globalpayments.android.application")
    id("globalpayments.android.application.compose")
    id("globalpayments.android.hilt")
    id("globalpayments.merchant3ds.config")
    kotlin("kapt")
    id("kotlinx-serialization")
}
android {
    namespace = "com.globalpayments.android.sdk.merchant3ds"

    defaultConfig {
        applicationId = "com.globalpayments.android.sdk.merchant3ds"
        versionCode = 2
        versionName = libs.versions.globalPayments.android.get()

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures.buildConfig = true

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(files("libs/3ds-sdk.aar"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.core)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.webkit)

    implementation(libs.accompanist.webview)

    implementation(libs.coil.compose)

    implementation(libs.retrofit)
    implementation(libs.retrofit.okhttp.interceptor)
    implementation(libs.retrofit.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.play.services.base)
    implementation(libs.slf4j.simple)
    implementation(libs.bouncyCastle)
    implementation(libs.jose4j)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
}

kapt {
    correctErrorTypes = true
}

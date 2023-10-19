plugins {
    id("globalpayments.android.library")
    id("globalpayments.maven.publish")
}

android {
    namespace = "com.globalpayments.android.sdk"

    defaultConfig {
        buildConfigField("String", "JAVA_VERSION", "\"${libs.versions.globalPayments.java.get()}\"")
    }

    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes.add("META-INF/*")
        }
    }
    publishing {
        singleVariant("release") {
            withJavadocJar()
            withSourcesJar()
        }
    }

    dependencies {
        implementation(libs.kotlin.stdlib)
        implementation(libs.androidx.appcompat)
        implementation(libs.androidx.browser)
        implementation(libs.androidx.fragment)
        implementation(libs.androidx.webkit)
        implementation(libs.material)

        api(libs.globalPayments.java)
    }
}

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.globalpayments.android.sdk.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}
gradlePlugin {
    plugins {
        register("androidApplicationCompose") {
            id = "globalpayments.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplication") {
            id = "globalpayments.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidHilt") {
            id = "globalpayments.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidLibrary") {
            id = "globalpayments.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("jvmLibrary") {
            id = "globalpayments.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("sampleAppConfig") {
            id = "globalpayments.sampleapp.config"
            implementationClass = "GlobalPaymentsSampleAppConfigConvention"
        }
        register("merchant3dsConfig") {
            id = "globalpayments.merchant3ds.config"
            implementationClass = "GlobalPaymentsMerchant3DSConfigConvention"
        }
        register("publishArtifacts") {
            id = "globalpayments.maven.publish"
            implementationClass = "PublishConventionPlugin"
        }
    }
}

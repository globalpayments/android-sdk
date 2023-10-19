buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

allprojects {
    tasks.withType(Javadoc::class.java).configureEach { excludes.add("**/*.kt") }
}
tasks.register("clean", Delete::class.java).configure {
    delete(rootProject.buildDir)
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}

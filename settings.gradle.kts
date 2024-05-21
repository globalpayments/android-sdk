pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}


rootProject.name = "GlobalPayments-Android-SDK"
include(":globalpayments-android-sdk")
include(":sample-app")
include(":merchant3ds")

//Temporary fix for https://issuetracker.google.com/issues/315023802
gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))
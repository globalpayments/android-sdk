package com.globalpayments.android.sdk

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import java.util.Properties

internal fun Project.configureMerchant3DSApp(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    val configProperties = Properties()
    if (file("local.properties").exists()) {
        configProperties.load(file("local.properties").inputStream())
    } else {
        configProperties.load(file("configuration.properties").inputStream())
    }
    commonExtension.defaultConfig {
        buildConfigField("String", "APP_ID", "\"${configProperties.getProperty("appId")}\"")
        buildConfigField("String", "APP_KEY", "\"${configProperties.getProperty("appKey")}\"")
        buildConfigField("String", "SERVER_URL", "\"${configProperties.getProperty("serverUrl")}\"")
        buildConfigField("Boolean", "PREFER_DECOUPLED_FLOW", configProperties.getProperty("preferDecoupledFlow"))
        buildConfigField("int", "AUTH_TIMEOUT", configProperties.getProperty("authTimeout"))
        buildConfigField("String", "API_KEY", "\"${configProperties.getProperty("apiKey")}\"")
    }
}

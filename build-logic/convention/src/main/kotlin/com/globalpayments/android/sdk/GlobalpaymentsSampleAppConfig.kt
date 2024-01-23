package com.globalpayments.android.sdk

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.konan.properties.Properties

internal fun Project.configureSampleApp(
    commonExtension: CommonExtension<*, *, *, *, *>
) {
    val configProperties = Properties()
    if (file("local.properties").exists()) {
        configProperties.load(file("local.properties").inputStream())
    } else {
        configProperties.load(file("configuration.properties").inputStream())
    }
    commonExtension.defaultConfig {
        buildConfigField("String", "MERCHANT_ID", "\"${configProperties.getProperty("merchantId")}\"")
        buildConfigField("String", "TRANSACTION_PROCESSING_ACCOUNT_NAME", "\"${configProperties.getProperty("transactionProcessingAccountName")}\"")
        buildConfigField("String", "TOKENIZATION_ACCOUNT_NAME", "\"${configProperties.getProperty("tokenizationAccountName")}\"")
        buildConfigField("String", "TRANSACTION_PROCESSING_ACCOUNT_ID", "\"${configProperties.getProperty("transactionProcessingAccountId")}\"")
        buildConfigField("String", "DATA_ACCOUNT_ID", "\"${configProperties.getProperty("dataAccountId")}\"")
        buildConfigField("String", "DISPUTE_MANAGEMENT_ACCOUNT_ID", "\"${configProperties.getProperty("disputeManagementAccountId")}\"")
        buildConfigField("String", "TOKENIZATION_ACCOUNT_ID", "\"${configProperties.getProperty("tokenizationAccountId")}\"")
        buildConfigField("String", "RISK_ASSESSMENT_ACCOUNT_ID", "\"${configProperties.getProperty("riskAssessmentAccountId")}\"")
        buildConfigField("String", "APP_ID", "\"${configProperties.getProperty("appId")}\"")
        buildConfigField("String", "APP_KEY", "\"${configProperties.getProperty("appKey")}\"")
        buildConfigField("String", "SERVICE_URL", "\"${configProperties.getProperty("serviceUrl")}\"")
        buildConfigField("String", "CHALLENGE_NOTIFICATION_URL", "\"${configProperties.getProperty("challengeNotificationUrl")}\"")
        buildConfigField("String", "STATUS_URL", "\"${configProperties.getProperty("statusUrl")}\"")
        buildConfigField("String", "METHOD_NOTIFICATION_URL", "\"${configProperties.getProperty("methodNotificationUrl")}\"")
        buildConfigField("String", "API_VERSION", "\"${configProperties.getProperty("apiVersion")}\"")
        buildConfigField("Integer", "TOKEN_SECONDS_TO_EXPIRE", configProperties.getProperty("tokenSecondsToExpire"))
        buildConfigField("String", "TOKEN_INTERVAL_TO_EXPIRE", "\"${configProperties.getProperty("tokenIntervalToExpire")}\"")
        buildConfigField("String", "COUNTRY", "\"${configProperties.getProperty("country")}\"")
        buildConfigField("String", "CHANNEL", "\"${configProperties.getProperty("channel")}\"")
        buildConfigField("String", "ENVIRONMENT", "\"${configProperties.getProperty("environment")}\"")
    }
}

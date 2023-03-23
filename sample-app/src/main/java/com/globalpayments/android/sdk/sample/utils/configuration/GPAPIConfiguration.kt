package com.globalpayments.android.sdk.sample.utils.configuration

import com.global.api.entities.enums.Channel
import com.global.api.entities.enums.Environment
import com.global.api.entities.enums.IntervalToExpire
import com.globalpayments.android.sdk.sample.BuildConfig

data class GPAPIConfiguration(
    val merchantId: String? = null,
    val transactionProcessingAccountName: String? = null,
    val tokenizationAccountName: String? = null,
    val transactionProcessingAccountId: String? = null,
    val dataAccountId :String? = null,
    val disputeManagementAccountId :String? = null,
    val tokenizationAccountId :String? = null,
    val riskAssessmentAccountId :String? = null,
    val appId: String? = null,
    val appKey: String? = null,
    val serviceUrl: String? = null,
    val challengeNotificationUrl: String? = null,
    val methodNotificationUrl: String? = null,
    val apiVersion: String? = null,
    val tokenSecondsToExpire: Int? = null,
    var channel: Channel? = null,
    val tokenIntervalToExpire: IntervalToExpire? = null,
    val environment: Environment? = null,
    val selectedCountry: String? = null,
) {
    companion object {
        @JvmStatic
        fun createDefaultConfig(): GPAPIConfiguration {
            return GPAPIConfiguration(
                BuildConfig.MERCHANT_ID,
                BuildConfig.TRANSACTION_PROCESSING_ACCOUNT_NAME,
                BuildConfig.TOKENIZATION_ACCOUNT_NAME,
                BuildConfig.TRANSACTION_PROCESSING_ACCOUNT_ID,
                BuildConfig.DATA_ACCOUNT_ID,
                BuildConfig.DISPUTE_MANAGEMENT_ACCOUNT_ID,
                BuildConfig.TOKENIZATION_ACCOUNT_ID,
                BuildConfig.RISK_ASSESSMENT_ACCOUNT_ID,
                BuildConfig.APP_ID,
                BuildConfig.APP_KEY,
                BuildConfig.SERVICE_URL,
                BuildConfig.CHALLENGE_NOTIFICATION_URL,
                BuildConfig.METHOD_NOTIFICATION_URL,
                BuildConfig.API_VERSION,
                BuildConfig.TOKEN_SECONDS_TO_EXPIRE,
                try {
                    Channel.valueOf(BuildConfig.CHANNEL)
                } catch (e: Exception) {
                    Channel.CardNotPresent
                },
                try {
                    IntervalToExpire.valueOf(BuildConfig.TOKEN_INTERVAL_TO_EXPIRE)
                } catch (e: Exception) {
                    IntervalToExpire.WEEK
                },
                try {
                    Environment.valueOf(BuildConfig.ENVIRONMENT)
                } catch (e: Exception) {
                    Environment.TEST
                },
                BuildConfig.COUNTRY
            )
        }
    }
}

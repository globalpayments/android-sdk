package com.globalpayments.android.sdk.sample.gpapi.screens.config

import com.global.api.entities.enums.Channel
import com.global.api.entities.enums.Environment
import com.global.api.entities.enums.IntervalToExpire

data class ConfigScreenModel(
    val appId: String = "",
    val appKey: String = "",
    val merchantId: String = "",
    val transactionProcessingAccountName: String = "",
    val transactionProcessingAccountId: String = "",
    val dataAccountId: String = "",
    val disputeManagementAccountId: String = "",
    val riskAssessmentAccountId: String = "",
    val tokenizationAccountId: String = "",
    val tokenizationAccountName: String = "",
    val serviceUrl: String = "",
    val challengeNotificationUrl: String = "",
    val methodNotificationUrl: String = "",
    val apiVersion: String = "",
    val tokenSecondsToExpire: String = "",
    val tokenIntervalToExpire: IntervalToExpire = IntervalToExpire.DAY,
    val country: String = "",
    val channel: Channel = Channel.CardNotPresent,
    val environment: Environment = Environment.TEST,
)

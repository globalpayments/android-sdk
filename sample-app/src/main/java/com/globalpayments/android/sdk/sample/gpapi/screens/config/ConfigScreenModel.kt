package com.globalpayments.android.sdk.sample.gpapi.screens.config

import com.global.api.entities.enums.Channel
import com.global.api.entities.enums.Environment
import com.global.api.entities.enums.IntervalToExpire
import com.global.api.entities.enums.Secure3dVersion
import com.global.api.entities.enums.ShaHashType

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
    val statusUrl: String = "",
    val challengeNotificationUrl: String = "",
    val methodNotificationUrl: String = "",
    val apiVersion: String = "",
    val tokenSecondsToExpire: String = "",
    val tokenIntervalToExpire: IntervalToExpire = IntervalToExpire.DAY,
    val country: String = "",
    val channel: Channel = Channel.CardNotPresent,
    val environment: Environment = Environment.TEST,
    val apiKey: String = "",
    val accountId: String = "",
    val rebatePassword: String = "",
    val refundPassword: String = "",
    val sharedSecret: String = "",
    val merchantContactUrl: String = "",
    val secure3DVersion: Secure3dVersion = Secure3dVersion.ANY,
    val shaHashType: ShaHashType = ShaHashType.SHA1,
)

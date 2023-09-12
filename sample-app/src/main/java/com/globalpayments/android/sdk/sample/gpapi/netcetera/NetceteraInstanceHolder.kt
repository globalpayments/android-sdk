package com.globalpayments.android.sdk.sample.gpapi.netcetera

import android.app.Activity
import android.content.Context
import android.content.res.AssetManager
import com.netcetera.threeds.sdk.ThreeDS2ServiceInstance
import com.netcetera.threeds.sdk.api.ThreeDS2Service
import com.netcetera.threeds.sdk.api.configparameters.ConfigParameters
import com.netcetera.threeds.sdk.api.configparameters.builder.ConfigurationBuilder
import com.netcetera.threeds.sdk.api.configparameters.builder.SchemeConfiguration
import com.netcetera.threeds.sdk.api.transaction.Transaction
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver
import com.netcetera.threeds.sdk.api.transaction.challenge.events.CompletionEvent
import com.netcetera.threeds.sdk.api.transaction.challenge.events.ProtocolErrorEvent
import com.netcetera.threeds.sdk.api.transaction.challenge.events.RuntimeErrorEvent
import com.netcetera.threeds.sdk.api.ui.logic.UiCustomization
import com.netcetera.threeds.sdk.api.utils.DsRidValues
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NetceteraInstanceHolder {

    private var isServiceInitialized: Boolean = false
    private val threeDS2Service: ThreeDS2Service by lazy { ThreeDS2ServiceInstance.get() }

    suspend fun init3DSService(context: Context) = withContext(Dispatchers.IO) {
        if (isServiceInitialized) return@withContext
        threeDS2Service.initialize(
            context,
            getThreeDS2ConfigParams(context),
            Locale.getDefault().language,
            getThreeDS2UICustomization()
        )
        isServiceInitialized = true
    }

    suspend fun createTransaction(cardBrand: String, messageVersion: String): Transaction = withContext(Dispatchers.IO) {
        threeDS2Service.createTransaction(cardBrand.asDsRidValue(), messageVersion)
    }

    suspend fun Transaction.startChallenge(
        activity: Activity,
        params: ChallengeParameters,
    ) = suspendCoroutine {

        doChallenge(activity, params, object : ChallengeStatusReceiver {
            override fun completed(p0: CompletionEvent?) {
                it.resume(Unit)
            }

            override fun cancelled() {
                it.resumeWithException(CancellationException("User canceled"))
            }

            override fun timedout() {
                it.resumeWithException(CancellationException("Timeout exception"))
            }

            override fun protocolError(p0: ProtocolErrorEvent?) {
                it.resumeWithException(Exception(p0?.errorMessage?.errorDetails ?: "Protocol Error"))
            }

            override fun runtimeError(p0: RuntimeErrorEvent?) {
                it.resumeWithException(Exception(p0?.errorMessage ?: "Protocol Error"))
            }
        }, 5)
    }

    private fun getThreeDS2ConfigParams(context: Context): ConfigParameters {
        val assetManager = context.assets
        return ConfigurationBuilder().license(assetManager.readLicense()).configureScheme(
            SchemeConfiguration.visaSchemeConfiguration()
                .encryptionPublicKeyFromAssetCertificate(assetManager, "acs.pem")
                .rootPublicKeyFromAssetCertificate(assetManager, "acs.pem").build()
        ).build()
    }

    private fun getThreeDS2UICustomization(): UiCustomization {
        return UiCustomization()
    }

    private fun AssetManager.readLicense() =
        this.open("license").bufferedReader().use(BufferedReader::readText)

    private fun String.asDsRidValue(): String? = when (this.lowercase()) {
        "visa" -> DsRidValues.VISA
        "mastercard" -> DsRidValues.MASTERCARD
        "amex" -> DsRidValues.AMEX
        "dinners" -> DsRidValues.DINERS
        "union" -> DsRidValues.UNION
        "jcb" -> DsRidValues.JCB
        "cb" -> DsRidValues.CB
        else -> null
    }
}

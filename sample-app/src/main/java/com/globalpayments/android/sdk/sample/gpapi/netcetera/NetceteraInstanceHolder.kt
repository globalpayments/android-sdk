package com.globalpayments.android.sdk.sample.gpapi.netcetera

import android.app.Activity
import android.content.Context
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
import com.netcetera.threeds.sdk.api.ui.logic.UiCustomization.UiCustomizationType
import com.netcetera.threeds.sdk.api.utils.DsRidValues
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NetceteraInstanceHolder {

    private val initMutex = Mutex()
    private var isServiceInitialized: Boolean = false
    private val threeDS2Service: ThreeDS2Service by lazy { ThreeDS2ServiceInstance.get() }

    suspend fun init3DSService(context: Context, apiKey: String) = withContext(Dispatchers.IO) {
        initMutex.withLock {
            if (isServiceInitialized) return@withContext
            threeDS2Service.initialize(
                context,
                getThreeDS2ConfigParams(context, apiKey),
                Locale.getDefault().language,
                getThreeDS2UICustomizationMap()
            )
            isServiceInitialized = true
        }
    }

    suspend fun createTransaction(cardBrand: String, messageVersion: String): Transaction = withContext(Dispatchers.IO) {
        threeDS2Service.createTransaction(cardBrand.asDsRidValue(), messageVersion)
    }

    suspend fun createTransactionFromCardNumber(cardNumber: String, messageVersion: String): Transaction = withContext(Dispatchers.IO) {
        val dsRid = getDsRidValuesForCardFromCardNumber(cardNumber)
        val transaction = threeDS2Service.createTransaction(dsRid, messageVersion)
        return@withContext transaction
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

    private fun getThreeDS2ConfigParams(context: Context, apiKey: String): ConfigParameters {
        val assetManager = context.assets
        return ConfigurationBuilder()
            .apiKey(apiKey)
            .configureScheme(
                SchemeConfiguration.visaSchemeConfiguration()
                    .encryptionPublicKeyFromAssetCertificate(assetManager, "acs.pem")
                    .rootPublicKeyFromAssetCertificate(assetManager, "acs.pem").build()
            ).build()
    }

    private fun getThreeDS2UICustomizationMap(): Map<UiCustomizationType, UiCustomization> {
        return hashMapOf<UiCustomizationType, UiCustomization>().apply {
            put(UiCustomizationType.DEFAULT, getThreeDS2UICustomization())
            put(UiCustomizationType.DARK, getThreeDS2UICustomization())
        }
    }

    private fun getThreeDS2UICustomization(): UiCustomization {
        return UiCustomization()
    }

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

    private fun getDsRidValuesForCardFromCardNumber(cardNumber: String): String? {
        val visaPattern = "^4[0-9]{12}(?:[0-9]{3})?$"
        val masterCardPattern = "^(5[1-5][0-9]{14}|2[2-7][0-9]{14})$"
        val amexPattern = "^3[47][0-9]{13}$"
        val dinersPattern = "^3(?:0[0-5]|[68][0-9])[0-9]{11}$"
        val unionPayPattern = "^(62[0-9]{14,17})$"
        val jcbPattern = "^(?:2131|1800|35\\d{3})\\d{11}$"
        val cbPattern = "^30[0-5][0-9]{11}$"

        return when {
            cardNumber.matches(visaPattern.toRegex()) -> DsRidValues.VISA
            cardNumber.matches(masterCardPattern.toRegex()) -> DsRidValues.MASTERCARD
            cardNumber.matches(amexPattern.toRegex()) -> DsRidValues.AMEX
            cardNumber.matches(dinersPattern.toRegex()) -> DsRidValues.DINERS
            cardNumber.matches(unionPayPattern.toRegex()) -> DsRidValues.UNION
            cardNumber.matches(jcbPattern.toRegex()) -> DsRidValues.JCB
            cardNumber.matches(cbPattern.toRegex()) -> DsRidValues.CB
            else -> null
        }
    }
}

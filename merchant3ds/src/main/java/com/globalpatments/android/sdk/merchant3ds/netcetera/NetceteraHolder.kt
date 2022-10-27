package com.globalpatments.android.sdk.merchant3ds.netcetera

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
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class NetceteraHolder @Inject constructor() {

    @Volatile
    private var isInitialized: Boolean = false
    private val threeDS2Service: ThreeDS2Service = ThreeDS2ServiceInstance.get()


    suspend fun init3DS(context: Context) = withContext(Dispatchers.IO) {
        if (isInitialized) return@withContext
        isInitialized = true
        threeDS2Service.initialize(
            context,
            getThreeDS2ConfigParams(context),
            Locale.getDefault().language,
            getThreeDS2UICustomization()
        )
    }

    fun createTransaction(cardBrand: String, messageVersion: String): Transaction {
        return threeDS2Service.createTransaction(getDsRidValuesForCard(cardBrand), messageVersion)
    }

    suspend fun Transaction.startChallenge(
        activity: Activity,
        acsReferenceNumber: String,
        payerAuthenticationRequest: String,
        acsTransactionId: String,
        providerServerTransRef: String
    ) = suspendCoroutine {
        val params = ChallengeParameters().apply {
            acsRefNumber = acsReferenceNumber
            acsSignedContent = payerAuthenticationRequest
            acsTransactionID = acsTransactionId
            set3DSServerTransactionID(providerServerTransRef)
        }
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
                it.resumeWithException(
                    Exception(
                        p0?.errorMessage?.errorDetails ?: "Protocol Error"
                    )
                )
            }

            override fun runtimeError(p0: RuntimeErrorEvent?) {
                it.resumeWithException(
                    Exception(
                        p0?.errorMessage ?: "Protocol Error"
                    )
                )
            }
        }, 5)
    }

    private fun getDsRidValuesForCard(cardBrand: String): String? {
        return when (cardBrand.lowercase()) {
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


    private fun getThreeDS2ConfigParams(context: Context): ConfigParameters {
        val assetManager = context.assets
        return ConfigurationBuilder().license(assetManager.readLicense()).configureScheme(
            SchemeConfiguration.visaSchemeConfiguration()
                .encryptionPublicKeyFromAssetCertificate(assetManager, "acs2021.pem")
                .rootPublicKeyFromAssetCertificate(assetManager, "acs2021.pem").build()
        ).build()
    }

    private fun getThreeDS2UICustomization(): UiCustomization {
        return UiCustomization()
    }

    private fun AssetManager.readLicense() =
        this.open("license").bufferedReader().use(BufferedReader::readText)
}
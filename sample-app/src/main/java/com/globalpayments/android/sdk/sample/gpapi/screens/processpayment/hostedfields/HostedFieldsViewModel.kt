package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.hostedfields

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.MobileData
import com.global.api.entities.StoredCredential
import com.global.api.entities.ThreeDSecure
import com.global.api.entities.Transaction
import com.global.api.entities.enums.AuthenticationSource
import com.global.api.entities.enums.SdkInterface
import com.global.api.entities.enums.SdkUiType
import com.global.api.entities.enums.Secure3dVersion
import com.global.api.paymentMethods.CreditCardData
import com.global.api.services.GpApiService
import com.global.api.services.Secure3dService
import com.global.api.utils.JsonDoc
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.netcetera.NetceteraInstanceHolder
import com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.unifiedpaymentsapi.NetceteraParams
import com.globalpayments.android.sdk.sample.gpapi.utils.formatAsPaymentAmount
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import com.globalpayments.android.sdk.sample.utils.AppPreferences
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfiguration
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfigurationUtils.buildDefaultGpApiConfig
import com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.math.BigDecimal

class HostedFieldsViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedAppPreferences = AppPreferences(application)
    val screenModel: MutableStateFlow<HostedFieldsScreenModel> = MutableStateFlow(HostedFieldsScreenModel())

    init {
        createAccessToken()
        viewModelScope.launch(Dispatchers.IO) {
            NetceteraInstanceHolder.init3DSService(application)
        }
    }

    private fun createAccessToken() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accessToken = GpApiService
                    .generateTransactionKey(buildDefaultGpApiConfig(sharedAppPreferences.gpAPIConfiguration ?: GPAPIConfiguration.fromBuildConfig()))
                    .accessToken
                screenModel.update { it.copy(accessToken = accessToken) }
            } catch (exception: Exception) {
                onError(exception)
            }
        }
    }

    fun onPaymentAmountChanged(value: String) {
        screenModel.update { it.copy(paymentAmount = value.formatAsPaymentAmount()) }
    }

    fun reset() {
        screenModel.update { HostedFieldsScreenModel() }
        createAccessToken()
    }

    fun onCardTokenReceived(token: String, cardBrand: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tokenizedCard = CreditCardData(token)
                val amount = BigDecimal(screenModel.value.paymentAmount)
                val cardEnrollmentCheck = checkCardEnrollment(tokenizedCard, amount)
                val isCardEnrolledFor3DS = cardEnrollmentCheck.enrolledStatus == ENROLLED

                if (!isCardEnrolledFor3DS) {
                    chargeMoney(tokenizedCard, amount)
                    return@launch
                }
                val transaction = NetceteraInstanceHolder.createTransaction(cardBrand, cardEnrollmentCheck.messageVersion)

                val netceteraAuthenticationParams = transaction.authenticationRequestParameters
                val authenticationResponse = initiateAuthentication(tokenizedCard, amount, cardEnrollmentCheck, netceteraAuthenticationParams)

                if (authenticationResponse.status != CHALLENGE_REQUIRED) {
                    val authenticationData = getAuthenticationData(authenticationResponse.serverTransactionId)
                    tokenizedCard.threeDSecure = authenticationData
                    chargeMoney(tokenizedCard, amount)
                    return@launch
                }

                val netceteraParams = NetceteraParams(transaction, authenticationResponse, tokenizedCard, amount)
                screenModel.update { it.copy(netceteraTransactionParams = netceteraParams) }

            } catch (exception: Exception) {
                onError(exception)
            }
        }
    }

    private suspend fun getAuthenticationData(serverTransactionId: String): ThreeDSecure = withContext(Dispatchers.IO) {
        Secure3dService
            .getAuthenticationData()
            .withServerTransactionId(serverTransactionId)
            .execute(Secure3dVersion.TWO, Constants.DEFAULT_GPAPI_CONFIG)
    }

    private suspend fun checkCardEnrollment(
        tokenizedCard: CreditCardData,
        amount: BigDecimal
    ): ThreeDSecure = withContext(Dispatchers.IO) {
        Secure3dService
            .checkEnrollment(tokenizedCard)
            .withCurrency(Currency)
            .withAmount(amount)
            .withAuthenticationSource(AuthenticationSource.MobileSDK)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    private suspend fun chargeMoney(
        tokenizedCard: CreditCardData,
        amount: BigDecimal,
    ) = withContext(Dispatchers.IO) {

        val response = tokenizedCard
            .charge(amount)
            .withCurrency(Currency)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)

        val resultToShow = response.mapNotNullFields()
        val sampleResponse = GPSampleResponseModel(
            transactionId = response.transactionId, response = listOf(
                "Time" to response.timestamp, "Status" to response.responseMessage
            )
        )
        val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, resultToShow)
        screenModel.update {
            it.copy(
                gpSampleResponse = sampleResponse,
                gpSnippetResponseModel = gpSnippetResponseModel,
                netceteraTransactionParams = null
            )
        }
    }


    private suspend fun initiateAuthentication(
        tokenizedCard: CreditCardData,
        amount: BigDecimal,
        secureEcom: ThreeDSecure,
        netceteraParams: AuthenticationRequestParameters
    ): ThreeDSecure = withContext(Dispatchers.IO) {
        Secure3dService
            .initiateAuthentication(tokenizedCard, secureEcom)
            .withAuthenticationSource(AuthenticationSource.MobileSDK)
            .withAmount(amount)
            .withCurrency(Currency)
            .withStoredCredential(StoredCredential())
            .withOrderCreateDate(DateTime.now())
            .withMobileData(MobileData().apply {
                applicationReference = netceteraParams.sdkAppID
                sdkTransReference = netceteraParams.sdkTransactionID
                referenceNumber = netceteraParams.sdkReferenceNumber
                sdkInterface = SdkInterface.Both
                encodedData = netceteraParams.deviceData
                maximumTimeout = 15
                ephemeralPublicKey = JsonDoc.parse(netceteraParams.sdkEphemeralPublicKey)
                setSdkUiTypes(*SdkUiType.entries.toTypedArray())
            })
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    fun doChallenge(
        activity: Activity,
        netceteraParams: NetceteraParams
    ) {
        viewModelScope.launch {
            try {
                val (transaction, threeDSecure, tokenizedCard, amount) = netceteraParams
                val challengeParams = ChallengeParameters().apply {
                    acsRefNumber = threeDSecure.acsReferenceNumber
                    acsSignedContent = threeDSecure.payerAuthenticationRequest
                    acsTransactionID = threeDSecure.acsTransactionId
                    set3DSServerTransactionID(threeDSecure.providerServerTransRef)
                }
                with(NetceteraInstanceHolder) { transaction.startChallenge(activity, challengeParams) }

                val authenticationData = getAuthenticationData(threeDSecure.serverTransactionId)
                tokenizedCard.threeDSecure = authenticationData
                chargeMoney(tokenizedCard, amount)

            } catch (exception: Exception) {
                onError(exception)
            }
        }
    }

    fun onError(exception: Exception) {
        screenModel.update {
            val gpSnippetResponseModel = GPSnippetResponseModel("AccessToken", listOf("Error" to (exception.message ?: "")), true)
            it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
        }
    }

    companion object {
        private const val Currency = "USD"
        private const val ENROLLED = "ENROLLED"
        private const val CHALLENGE_REQUIRED = "CHALLENGE_REQUIRED"
    }
}

package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.unifiedpaymentsapi

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.MobileData
import com.global.api.entities.StoredCredential
import com.global.api.entities.ThreeDSecure
import com.global.api.entities.Transaction
import com.global.api.entities.enums.AuthenticationSource
import com.global.api.entities.enums.SdkInterface
import com.global.api.entities.enums.SdkUiType
import com.global.api.entities.enums.Secure3dVersion
import com.global.api.entities.enums.StoredCredentialInitiator
import com.global.api.entities.enums.StoredCredentialReason
import com.global.api.entities.enums.StoredCredentialSequence
import com.global.api.entities.enums.StoredCredentialType
import com.global.api.paymentMethods.CreditCardData
import com.global.api.services.Secure3dService
import com.global.api.utils.CardUtils
import com.global.api.utils.JsonDoc
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.netcetera.NetceteraInstanceHolder
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date

class UnifiedPaymentAPIViewModel(context: Context) : ViewModel() {

    private val calendar: Calendar by lazy { Calendar.getInstance() }

    val screenModel: MutableStateFlow<UnifiedPaymentAPIModel> = MutableStateFlow(UnifiedPaymentAPIModel())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            NetceteraInstanceHolder.init3DSService(context)
        }
    }

    fun onAmountChanged(value: String) = screenModel.update { it.copy(amount = value) }
    fun onCardNumberChanged(value: String) = screenModel.update { it.copy(cardNumber = value) }
    fun onCvvChanged(value: String) = screenModel.update { it.copy(cardCVV = value) }
    fun updateCardExpirationDate(date: Date) = screenModel.update {
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        it.copy(cardYear = year.toString(), cardMonth = if (month < 10) "0$month" else month.toString())
    }

    fun onMakePaymentRecurring(value: Boolean) = screenModel.update { it.copy(makePaymentRecurring = value) }

    fun onCardHolderName(value: String) = screenModel.update { it.copy(cardHolderName = value) }

    fun makePayment() {
        viewModelScope.launch(Dispatchers.IO) {
            val model = screenModel.value
            makePayment(model)
        }
    }

    private suspend fun makePayment(model: UnifiedPaymentAPIModel) {
        val amount = model.amount.toBigDecimalOrNull() ?: return
        try {
            val cardToken = getCardToken()
            val tokenizedCard = CreditCardData(cardToken)

            val cardEnrollmentCheck = checkCardEnrollment(tokenizedCard, amount)
            val isCardEnrolledFor3DS = cardEnrollmentCheck.enrolledStatus == ENROLLED

            if (!isCardEnrolledFor3DS) {
                chargeMoney(tokenizedCard, amount)
                return
            }

            val cardBrand = CardUtils.mapCardType(model.cardNumber)
            val transaction = NetceteraInstanceHolder.createTransaction(cardBrand, cardEnrollmentCheck.messageVersion)

            val netceteraAuthenticationParams = transaction.authenticationRequestParameters
            val authenticationResponse = initiateAuthentication(tokenizedCard, amount, cardEnrollmentCheck, netceteraAuthenticationParams)

            if (authenticationResponse.status != CHALLENGE_REQUIRED) {
                val authenticationData = getAuthenticationData(authenticationResponse.serverTransactionId)
                tokenizedCard.threeDSecure = authenticationData
                chargeMoney(tokenizedCard, amount)
                return
            }

            val netceteraParams = NetceteraParams(transaction, authenticationResponse, tokenizedCard, amount)
            screenModel.update { it.copy(netceteraTransactionParams = netceteraParams) }

        } catch (exception: Exception) {
            onError(exception)
        }
    }

    private suspend fun makePaymentRecurring(chargeResponse: Transaction, tokenizedCard: CreditCardData) = withContext(Dispatchers.IO) {
        val storedCredential = StoredCredential().apply {
            initiator = StoredCredentialInitiator.CardHolder
            type = StoredCredentialType.Recurring
            sequence = StoredCredentialSequence.Subsequent
            reason = StoredCredentialReason.Incremental
        }
        tokenizedCard
            .charge(BigDecimal(screenModel.value.amount))
            .withCurrency(Currency)
            .withStoredCredential(storedCredential)
            .withCardBrandStorage(StoredCredentialInitiator.Merchant, chargeResponse.cardBrandTransactionId)
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

    private suspend fun chargeMoney(
        tokenizedCard: CreditCardData,
        amount: BigDecimal,
    ) = withContext(Dispatchers.IO) {

        var response = tokenizedCard
            .charge(amount)
            .withCurrency(Currency)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
        if (screenModel.value.makePaymentRecurring) {
            response = makePaymentRecurring(response, tokenizedCard)
        }

        val resultToShow = response.mapNotNullFields()
        val sampleResponse = GPSampleResponseModel(
            transactionId = response.transactionId, response = listOf(
                "Time" to response.timestamp, "Status" to response.responseMessage
            )
        )
        val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, resultToShow)
        screenModel.update {
            it.copy(
                gpSampleResponseModel = sampleResponse,
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

    private suspend fun getAuthenticationData(serverTransactionId: String): ThreeDSecure = withContext(Dispatchers.IO) {
        Secure3dService
            .getAuthenticationData()
            .withServerTransactionId(serverTransactionId)
            .execute(Secure3dVersion.TWO, Constants.DEFAULT_GPAPI_CONFIG)
    }

    private fun onError(exception: Exception) {
        screenModel.update {
            val gpSnippetResponseModel =
                GPSnippetResponseModel(Transaction::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
            it.copy(
                gpSnippetResponseModel = gpSnippetResponseModel,
                gpSampleResponseModel = null,
                netceteraTransactionParams = null
            )
        }
    }

    private suspend fun getCardToken(): String = withContext(Dispatchers.IO) {
        val model = screenModel.value
        val card = CreditCardData().apply {
            number = model.cardNumber
            expMonth = model.cardMonth.toInt()
            expYear = model.cardYear.toInt()
            cardHolderName = model.cardHolderName
            isCardPresent = true
        }
        card.tokenize(true, Constants.DEFAULT_GPAPI_CONFIG)
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

    fun resetScreen() {
        screenModel.update { UnifiedPaymentAPIModel() }
    }

    companion object {
        private const val Currency = "GBP"
        private const val ENROLLED = "ENROLLED"
        private const val CHALLENGE_REQUIRED = "CHALLENGE_REQUIRED"
    }
}

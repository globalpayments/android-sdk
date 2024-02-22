package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.unifiedpaymentsapi

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Transaction
import com.global.api.paymentMethods.CreditCardData
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.netcetera.NetceteraInstanceHolder
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import com.globalpayments.android.sdk.sample.repository.Secure3DSRepository
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date

class UnifiedPaymentAPIViewModel(context: Context) : ViewModel() {

    private val secure3DSRepository = Secure3DSRepository()
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
            val cardToken = secure3DSRepository.tokenizeCard(
                model.cardNumber,
                model.cardMonth.toInt(),
                model.cardYear.toInt(),
                model.cardHolderName
            )
            val tokenizedCard = CreditCardData(cardToken)

            val cardEnrollmentCheck = secure3DSRepository.checkCardEnrollment(cardToken, amount, Currency)

            if (!cardEnrollmentCheck.isCardEnrolled) {
                chargeMoney(cardToken, amount)
                return
            }

            val transaction = NetceteraInstanceHolder.createTransaction(cardEnrollmentCheck.cardBrand, cardEnrollmentCheck.messageVersion)

            val netceteraAuthenticationParams = transaction.authenticationRequestParameters
            val authenticationResponse = secure3DSRepository.initiateAuthentication(cardToken, amount, netceteraAuthenticationParams, Currency)
            if (!authenticationResponse.isChallengeRequired) {
                chargeMoney(cardToken, amount, authenticationResponse.serverTransactionId)
                return
            }

            val netceteraParams = NetceteraParams(transaction, authenticationResponse, cardToken, amount)
            screenModel.update { it.copy(netceteraTransactionParams = netceteraParams) }

        } catch (exception: Exception) {
            onError(exception)
        }
    }

    fun doChallenge(
        activity: Activity,
        netceteraParams: NetceteraParams
    ) {
        viewModelScope.launch {
            try {
                val (transaction, initAuthenticationResponse, tokenizedCard, amount) = netceteraParams
                val challengeParams = ChallengeParameters().apply {
                    acsRefNumber = initAuthenticationResponse.acsReferenceNumber
                    acsSignedContent = initAuthenticationResponse.payerAuthenticationRequest
                    acsTransactionID = initAuthenticationResponse.acsTransactionId
                    set3DSServerTransactionID(initAuthenticationResponse.providerServerTransRef)
                }
                with(NetceteraInstanceHolder) { transaction.startChallenge(activity, challengeParams) }
                chargeMoney(tokenizedCard, amount, initAuthenticationResponse.serverTransactionId)

            } catch (exception: Exception) {
                onError(exception)
            }
        }
    }

    private suspend fun chargeMoney(
        cardToken: String,
        amount: BigDecimal,
        serverTransactionId: String? = null
    ) = withContext(Dispatchers.IO) {

        var response = secure3DSRepository.chargeMoney(cardToken, amount, Currency, serverTransactionId)
        if (screenModel.value.makePaymentRecurring) {
            response = secure3DSRepository.makePaymentRecurring(
                cardToken,
                response.cardBrandTransactionId,
                amount,
                Currency
            )
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

    fun resetScreen() {
        screenModel.update { UnifiedPaymentAPIModel() }
    }

    companion object {
        private const val Currency = "GBP"
        private const val ENROLLED = "ENROLLED"
        private const val CHALLENGE_REQUIRED = "CHALLENGE_REQUIRED"
    }
}

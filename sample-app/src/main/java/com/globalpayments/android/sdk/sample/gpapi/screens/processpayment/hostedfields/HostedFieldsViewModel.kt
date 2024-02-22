package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.hostedfields

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Transaction
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.netcetera.NetceteraInstanceHolder
import com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.unifiedpaymentsapi.NetceteraParams
import com.globalpayments.android.sdk.sample.gpapi.utils.formatAsPaymentAmount
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import com.globalpayments.android.sdk.sample.repository.AccessTokenRepository
import com.globalpayments.android.sdk.sample.repository.Secure3DSRepository
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class HostedFieldsViewModel(application: Application) : AndroidViewModel(application) {

    private val accessTokenRepository = AccessTokenRepository(application)
    private val secure3DSRepository = Secure3DSRepository()

    val screenModel: MutableStateFlow<HostedFieldsScreenModel> = MutableStateFlow(HostedFieldsScreenModel())

    init {
        getAccessToken()
        viewModelScope.launch(Dispatchers.IO) {
            NetceteraInstanceHolder.init3DSService(application)
        }
    }

    private fun getAccessToken() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accessToken = accessTokenRepository.getAccessToken()
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
        getAccessToken()
    }

    fun onCardTokenReceived(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val amount = BigDecimal(screenModel.value.paymentAmount)
                val cardEnrollmentCheck = secure3DSRepository.checkCardEnrollment(token, amount, Currency)

                if (!cardEnrollmentCheck.isCardEnrolled) {
                    chargeMoney(token, amount)
                    return@launch
                }
                val cardBrand = cardEnrollmentCheck.cardBrand
                val transaction = NetceteraInstanceHolder.createTransaction(cardBrand, cardEnrollmentCheck.messageVersion)

                val netceteraAuthenticationParams = transaction.authenticationRequestParameters
                val authenticationResponse = secure3DSRepository.initiateAuthentication(token, amount, netceteraAuthenticationParams, Currency)

                if (!authenticationResponse.isChallengeRequired) {
                    chargeMoney(
                        token,
                        amount,
                        authenticationResponse.serverTransactionId,
                    )
                    return@launch
                }

                val netceteraParams = NetceteraParams(transaction, authenticationResponse, token, amount)
                screenModel.update { it.copy(netceteraTransactionParams = netceteraParams) }

            } catch (exception: Exception) {
                onError(exception)
            }
        }
    }

    private suspend fun chargeMoney(
        tokenizedCard: String,
        amount: BigDecimal,
        serverTransactionId: String? = null
    ) = withContext(Dispatchers.IO) {

        val response = secure3DSRepository.chargeMoney(tokenizedCard, amount, Currency, serverTransactionId)

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

    fun onError(exception: Exception) {
        screenModel.update {
            val gpSnippetResponseModel = GPSnippetResponseModel("AccessToken", listOf("Error" to (exception.message ?: "")), true)
            it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
        }
    }

    companion object {
        private const val Currency = "USD"
    }
}

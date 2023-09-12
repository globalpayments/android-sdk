package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.ebt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Transaction
import com.global.api.paymentMethods.EBTCardData
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date

class EbtViewModel : ViewModel() {

    private val calendar = Calendar.getInstance()
    val screenModel = MutableStateFlow(EbtScreenModel())

    fun onAmountChanged(value: String) = screenModel.update { it.copy(amount = value) }
    fun onCardNumberChanged(value: String) = screenModel.update { it.copy(cardNumber = value) }
    fun updateCardExpirationDate(date: Date) = screenModel.update {
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        it.copy(cardYear = year.toString(), cardMonth = month.toString())
    }

    fun onPinBlockChanged(value: String) = screenModel.update { it.copy(pinBlock = value) }
    fun onCardHolderName(value: String) = screenModel.update { it.copy(cardHolderName = value) }

    fun makePayment(paymentType: PaymentType) {
        val amount = screenModel.value.amount.toBigDecimalOrNull() ?: return
        viewModelScope.launch {
            val model = screenModel.value
            val ebtCard = EBTCardData().apply {
                number = model.cardNumber
                expMonth = model.cardMonth.toInt()
                expYear = model.cardYear.toInt()
                pinBlock = model.pinBlock
                cardHolderName = model.cardHolderName
                isCardPresent = true
            }
            try {
                val response = when (paymentType) {
                    PaymentType.Charge -> chargeCard(ebtCard, amount)
                    PaymentType.Refund -> refundAmount(ebtCard, amount)
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
                        transaction = response
                    )
                }
            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(Transaction::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    private suspend fun chargeCard(ebtCardData: EBTCardData, amount: BigDecimal) = withContext(Dispatchers.IO) {
        ebtCardData
            .charge(amount)
            .withCurrency(Currency)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    private suspend fun refundAmount(ebtCardData: EBTCardData, amount: BigDecimal) = withContext(Dispatchers.IO) {
        ebtCardData
            .refund(amount)
            .withCurrency(Currency)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    fun refund() {
        val transaction = screenModel.value.transaction ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = transaction
                    .refund()
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
                        gpSampleResponseModel = sampleResponse,
                        gpSnippetResponseModel = gpSnippetResponseModel,
                        transaction = null
                    )
                }
            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(Transaction::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    fun reverse() {
        val transaction = screenModel.value.transaction ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = transaction
                    .reverse()
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
                        gpSampleResponseModel = sampleResponse,
                        gpSnippetResponseModel = gpSnippetResponseModel,
                        transaction = null
                    )
                }
            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(Transaction::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    fun reset() {
        screenModel.update { EbtScreenModel() }
    }

    companion object {
        private const val Currency = "USD"
    }
}

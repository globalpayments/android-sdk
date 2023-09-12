package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.paypal

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Transaction
import com.global.api.entities.enums.AlternativePaymentType
import com.global.api.entities.enums.PaymentMethodType
import com.global.api.entities.reporting.SearchCriteria
import com.global.api.paymentMethods.AlternativePaymentMethod
import com.global.api.services.ReportingService
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date


class PayPalScreenViewModel(context: Context) : ViewModel() {

    private val paypalHost by lazy { context.getString(R.string.paypal_deep_link_host) }
    private val paypalScheme by lazy { context.getString(R.string.paypal_deep_link_scheme) }

    val screenModel = MutableStateFlow(PayPalScreenModel())

    fun onAmountChanged(value: String) = screenModel.update { it.copy(amount = value) }

    fun makePayment(paymentType: PaymentType) {
        screenModel.value.amount.toBigDecimalOrNull() ?: return
        screenModel.update { it.copy(gpSnippetResponseModel = GPSnippetResponseModel()) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val paymentMethod = AlternativePaymentMethod()
                    .setAlternativePaymentMethodType(AlternativePaymentType.PAYPAL)
                    .setReturnUrl("$paypalScheme://$paypalHost")
                    .setStatusUpdateUrl("$paypalScheme://$paypalHost")
                    .setCancelUrl("$paypalScheme://$paypalHost")
                    .setDescriptor(Descriptor)
                    .setCountry("GB")
                    .setAccountHolderName(AccountName)

                val result = when (paymentType) {
                    PaymentType.Charge -> charge(paymentMethod)
                    PaymentType.Authorize -> authorize(paymentMethod)
                }

                val resultToShow = result.mapNotNullFields()
                val sampleResponse = GPSampleResponseModel(
                    transactionId = result.transactionId,
                    response = listOf(
                        "Time" to result.timestamp,
                        "Status" to result.responseMessage
                    )
                )
                val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, resultToShow, false)
                screenModel.update {
                    it.copy(
                        pendingTransaction = result,
                        gpSnippetResponseModel = gpSnippetResponseModel,
                        transactionStatus = PayPalTransactionStatus.Pending,
                        gpSampleResponseModel = sampleResponse,
                    )
                }

            } catch (exception: Exception) {
                onError(exception)
            }
        }
    }

    private suspend fun charge(paymentMethod: AlternativePaymentMethod) = withContext(Dispatchers.IO) {
        val amount = screenModel.value.amount.toBigDecimal()
        paymentMethod
            .charge(amount)
            .withCurrency(Currency)
            .withDescription(ChargeDescription)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    private suspend fun authorize(paymentMethod: AlternativePaymentMethod) = withContext(Dispatchers.IO) {
        val amount = screenModel.value.amount.toBigDecimal()
        paymentMethod
            .authorize(amount)
            .withCurrency(Currency)
            .withDescription(ChargeDescription)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    fun checkTransaction() {
        val pendingTransaction = screenModel.value.pendingTransaction ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val startDate = Date()
                val responseFind =
                    ReportingService
                        .findTransactionsPaged(1, 1)
                        .withTransactionId(pendingTransaction.transactionId)
                        .where(SearchCriteria.StartDate, startDate)
                        .and(SearchCriteria.EndDate, startDate)
                        .execute(Constants.DEFAULT_GPAPI_CONFIG)

                val transactionSummary = responseFind.getResults()[0]
                if (transactionSummary.transactionStatus == "PENDING") {
                    val transaction = Transaction
                        .fromId(transactionSummary.transactionId, null, PaymentMethodType.APM)
                        .apply {
                            alternativePaymentResponse = transactionSummary.alternativePaymentResponse
                        }
                    val result = transaction
                        .confirm()
                        .execute(Constants.DEFAULT_GPAPI_CONFIG)
                    val responseModel = GPSampleResponseModel(
                        transactionId = result.transactionId,
                        response = listOf(
                            "Time Created" to result.timestamp,
                            "Status" to result.responseMessage
                        )
                    )
                    screenModel.update {
                        val status = when (result.responseMessage) {
                            "CAPTURED" -> PayPalTransactionStatus.Charged
                            "PREAUTHORIZED" -> PayPalTransactionStatus.Authorized
                            else -> PayPalTransactionStatus.Pending
                        }
                        val gpSnippetResponseModel =
                            GPSnippetResponseModel(Transaction::class.java.simpleName, result.mapNotNullFields())
                        it.copy(
                            gpSampleResponseModel = responseModel,
                            transactionStatus = status,
                            gpSnippetResponseModel = gpSnippetResponseModel,
                            pendingTransaction = if (status == PayPalTransactionStatus.Authorized || status == PayPalTransactionStatus.Charged) transaction else null
                        )
                    }
                } else if (transactionSummary.transactionStatus == "INITIATED") {
                    //wait a little then check again
                    delay(2_000L)
                    if (screenModel.value.retryRemaining > 0) {
                        screenModel.update { it.copy(retryRemaining = it.retryRemaining - 1) }
                        checkTransaction() //retry
                    } else {
                        onError(Exception("Status didn't change"))
                    }
                }
            } catch (exception: Exception) {
                onError(exception)
            }
        }
    }

    fun captureTransaction() {
        val pendingTransaction = screenModel.value.pendingTransaction ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: Transaction = pendingTransaction
                    .capture()
                    .execute(DEFAULT_GPAPI_CONFIG)

                val responseModel = GPSampleResponseModel(
                    transactionId = result.transactionId,
                    response = listOf(
                        "Time Created" to result.timestamp,
                        "Status" to result.responseMessage
                    )
                )
                val gpSnippetResponseModel =
                    GPSnippetResponseModel(Transaction::class.java.simpleName, result.mapNotNullFields(), false)
                screenModel.update {
                    it.copy(
                        gpSampleResponseModel = responseModel,
                        transactionStatus = PayPalTransactionStatus.Charged,
                        gpSnippetResponseModel = gpSnippetResponseModel,
                        pendingTransaction = null
                    )
                }

            } catch (exception: Exception) {
                onError(exception)
            }
        }
    }

    fun refundTransaction() {
        val transactionId = screenModel.value.pendingTransaction?.transactionId?.takeIf(String::isNotBlank) ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val startDate = Date()
                val response = ReportingService
                    .findTransactionsPaged(1, 1)
                    .withTransactionId(transactionId)
                    .where(SearchCriteria.StartDate, startDate)
                    .and(SearchCriteria.EndDate, startDate)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)

                val transactionSummary = response.getResults().first()

                val transaction = Transaction.fromId(transactionId, null, PaymentMethodType.APM)
                transaction.alternativePaymentResponse =
                    transactionSummary.alternativePaymentResponse

                val result = transaction
                    .refund()
                    .withCurrency(Currency)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)

                val resultToShow = result.mapNotNullFields()
                val responseToShow = GPSampleResponseModel(
                    transactionId = result.transactionId,
                    response = listOf(
                        "Time" to result.timestamp,
                        "Status" to result.responseMessage
                    )
                )
                val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, resultToShow, false)
                screenModel.update {
                    it.copy(
                        gpSampleResponseModel = responseToShow,
                        gpSnippetResponseModel = gpSnippetResponseModel,
                        transactionStatus = PayPalTransactionStatus.Done
                    )
                }
            } catch (exception: Exception) {
                onError(exception)
            }
        }
    }

    fun reverseTransaction() {
        val transactionId = screenModel.value.pendingTransaction?.transactionId?.takeIf(String::isNotBlank) ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val startDate = Date()
                val response = ReportingService
                    .findTransactionsPaged(1, 1)
                    .withTransactionId(transactionId)
                    .where(SearchCriteria.StartDate, startDate)
                    .and(SearchCriteria.EndDate, startDate)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)

                val transactionSummary = response.getResults().first()

                val transaction =
                    Transaction.fromId(transactionId, null, PaymentMethodType.APM)
                transaction.alternativePaymentResponse =
                    transactionSummary.alternativePaymentResponse

                val result = transaction
                    .reverse()
                    .withCurrency(Currency)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)

                val resultToShow = result.mapNotNullFields()
                val responseToShow = GPSampleResponseModel(
                    transactionId = result.transactionId,
                    response = listOf(
                        "Time" to result.timestamp,
                        "Status" to result.responseMessage
                    )
                )
                val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, resultToShow)
                screenModel.update {
                    it.copy(
                        gpSnippetResponseModel = gpSnippetResponseModel,
                        gpSampleResponseModel = responseToShow,
                        transactionStatus = PayPalTransactionStatus.Done
                    )
                }

            } catch (exception: Exception) {
                onError(exception)
            }
        }
    }

    private fun onError(exception: Exception) {
        val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
        screenModel.update {
            it.copy(gpSnippetResponseModel = gpSnippetResponseModel, gpSampleResponseModel = null)
        }
    }

    fun resetScreen() {
        screenModel.update { PayPalScreenModel() }
    }

    companion object {
        const val Descriptor = "Test Transaction"
        const val AccountName = "John Doe"
        const val ChargeDescription = "New APM"
        const val Currency = "EUR"
    }
}

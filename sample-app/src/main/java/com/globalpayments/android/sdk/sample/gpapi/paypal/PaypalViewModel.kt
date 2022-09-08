package com.globalpayments.android.sdk.sample.gpapi.paypal

import androidx.lifecycle.MutableLiveData
import com.global.api.entities.Transaction
import com.global.api.entities.enums.AlternativePaymentType
import com.global.api.entities.enums.PaymentMethodType
import com.global.api.entities.reporting.SearchCriteria
import com.global.api.paymentMethods.AlternativePaymentMethod
import com.global.api.services.ReportingService
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel
import com.globalpayments.android.sdk.task.TaskExecutor
import com.globalpayments.android.sdk.task.TaskResult
import java.math.BigDecimal
import java.util.*


class PaypalViewModel : BaseViewModel() {

    private var pendingTransaction: Transaction? = null
    var onUrlReceived = MutableLiveData<String>()
    var onTransactionSuccess = MutableLiveData<Transaction>()

    var successTransaction = MutableLiveData<String?>()

    var paymentType: PaymentType = PaymentType.Charge

    fun makePayment(scheme: String, host: String, amount: BigDecimal) {
        showProgress()
        TaskExecutor.executeAsync(
            taskToExecute = {
                val paymentMethod = AlternativePaymentMethod()
                    .setAlternativePaymentMethodType(AlternativePaymentType.PAYPAL)
                    .setReturnUrl("$scheme://$host")
                    .setStatusUpdateUrl("$scheme://$host")
                    .setCancelUrl("$scheme://$host")
                    .setDescriptor(Descriptor)
                    .setCountry("GB")
                    .setAccountHolderName(AccountName)
                if (paymentType == PaymentType.Charge) {
                    paymentMethod
                        .charge(amount)
                        .withCurrency(Currency)
                        .withDescription(ChargeDescription)
                        .execute(Constants.DEFAULT_GPAPI_CONFIG)
                } else {
                    paymentMethod
                        .authorize(amount)
                        .withCurrency(Currency)
                        .withDescription(ChargeDescription)
                        .execute(Constants.DEFAULT_GPAPI_CONFIG)
                }
            },
            onFinished = {
                when (it) {
                    is TaskResult.Error -> {
                        showError(it.exception)
                    }
                    is TaskResult.Success -> {
                        pendingTransaction = it.data
                        val url =
                            it.data.alternativePaymentResponse.redirectUrl ?: return@executeAsync
                        onUrlReceived.postValue(url)
                    }
                }

            }
        )
    }

    fun checkTransaction() {
        val pendingTransaction = pendingTransaction ?: return
        TaskExecutor.executeAsync(
            taskToExecute = {
                val startDate = Date()
                val responseFind =
                    ReportingService
                        .findTransactionsPaged(1, 1)
                        .withTransactionId(pendingTransaction.transactionId)
                        .where(SearchCriteria.StartDate, startDate)
                        .and(SearchCriteria.EndDate, startDate)
                        .execute(Constants.DEFAULT_GPAPI_CONFIG)

                val transactionSummary = responseFind.getResults()[0]
                val transaction = Transaction
                    .fromId(transactionSummary.transactionId, null, PaymentMethodType.APM)
                    .apply {
                        alternativePaymentResponse = transactionSummary.alternativePaymentResponse
                    }
                transaction
                    .confirm()
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)
            },
            onFinished = {
                when (it) {
                    is TaskResult.Error -> {
                        showError(it.exception)
                    }
                    is TaskResult.Success -> {
                        this.pendingTransaction = null
                        successTransaction.postValue(it.data.transactionId)
                        onTransactionSuccess.postValue(it.data)
                    }
                }
            }
        )
    }

    fun refundTransaction() {
        val transactionToRefund = successTransaction.value ?: return
        showProgress()
        TaskExecutor.executeAsync(
            taskToExecute = {
                val startDate = Date()
                val response = ReportingService
                    .findTransactionsPaged(1, 1)
                    .withTransactionId(transactionToRefund)
                    .where(SearchCriteria.StartDate, startDate)
                    .and(SearchCriteria.EndDate, startDate)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)

                val transactionSummary = response.getResults().first()

                val transaction =
                    Transaction.fromId(transactionToRefund, null, PaymentMethodType.APM)
                transaction.alternativePaymentResponse =
                    transactionSummary.alternativePaymentResponse

                transaction.refund().withCurrency(Currency).execute(Constants.DEFAULT_GPAPI_CONFIG)
            },
            onFinished = {
                when (it) {
                    is TaskResult.Error -> showError(it.exception)
                    is TaskResult.Success -> {
                        onTransactionSuccess.postValue(it.data)
                        successTransaction.postValue(null)
                    }
                }
            }
        )
    }

    fun reverseTransaction() {
        val transactionToReverse = successTransaction.value ?: return
        showProgress()
        TaskExecutor.executeAsync(
            taskToExecute = {
                val startDate = Date()
                val response = ReportingService
                    .findTransactionsPaged(1, 1)
                    .withTransactionId(transactionToReverse)
                    .where(SearchCriteria.StartDate, startDate)
                    .and(SearchCriteria.EndDate, startDate)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)

                val transactionSummary = response.getResults().first()

                val transaction =
                    Transaction.fromId(transactionToReverse, null, PaymentMethodType.APM)
                transaction.alternativePaymentResponse =
                    transactionSummary.alternativePaymentResponse

                transaction
                    .reverse()
                    .withCurrency(Currency)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)
            },
            onFinished = {
                when (it) {
                    is TaskResult.Error -> showError(it.exception)
                    is TaskResult.Success -> {
                        onTransactionSuccess.postValue(it.data)
                        successTransaction.postValue(null)
                    }
                }
            }
        )
    }

    companion object {
        const val Descriptor = "Test Transaction"
        const val AccountName = "John Doe"
        const val ChargeDescription = "New APM"
        const val Currency = "EUR"
    }

    enum class PaymentType {
        Charge, Authorize
    }
}
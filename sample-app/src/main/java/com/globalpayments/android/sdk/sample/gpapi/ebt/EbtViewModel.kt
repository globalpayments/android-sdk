package com.globalpayments.android.sdk.sample.gpapi.ebt

import androidx.lifecycle.MutableLiveData
import com.global.api.entities.Transaction
import com.global.api.paymentMethods.EBTCardData
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success.TransactionSuccessModel
import com.globalpayments.android.sdk.task.TaskExecutor
import com.globalpayments.android.sdk.task.TaskResult
import java.math.BigDecimal

class EbtViewModel : BaseViewModel() {

    val transactionToShow = MutableLiveData<TransactionSuccessModel>()
    val transactionToRefund = MutableLiveData<Transaction?>()

    fun chargeAmount(
        carNumber: String,
        expMont: Int,
        expYear: Int,
        pinBlock: String,
        cardHolderName: String,
        amount: String
    ) {

        showProgress()
        TaskExecutor.executeAsync(
            taskToExecute = {
                val ebtCard = EBTCardData().apply {
                    number = carNumber
                    expMonth = expMont
                    setExpYear(expYear)
                    setPinBlock(pinBlock)
                    setCardHolderName(cardHolderName)
                    isCardPresent = true
                }
                ebtCard
                    .charge(BigDecimal(amount))
                    .withCurrency(Currency)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)
            },
            onFinished = {
                hideProgress()
                when (it) {
                    is TaskResult.Error -> showError(it.exception.message)
                    is TaskResult.Success -> {
                        val transaction = it.data
                        if (transaction.responseCode == "DECLINED") {
                            showError(transaction.responseCode)
                            return@executeAsync
                        }
                        transactionToShow.postValue(transaction.toExternalModel())
                        transactionToRefund.postValue(transaction)
                    }
                }
            }
        )
    }

    fun refundAmount(
        carNumber: String,
        expMont: Int,
        expYear: Int,
        pinBlock: String,
        cardHolderName: String,
        amount: String
    ) {
        showProgress()
        TaskExecutor.executeAsync(
            taskToExecute = {
                val ebtCard = EBTCardData().apply {
                    number = carNumber
                    expMonth = expMont
                    setExpYear(expYear)
                    setPinBlock(pinBlock)
                    setCardHolderName(cardHolderName)
                    isCardPresent = true
                }
                ebtCard
                    .refund(BigDecimal(amount))
                    .withCurrency(Currency)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)
            },
            onFinished = {
                hideProgress()
                when (it) {
                    is TaskResult.Error -> showError(it.exception.message)
                    is TaskResult.Success -> {
                        val transaction = it.data
                        if (transaction.responseCode == "DECLINED") {
                            showError(transaction.responseCode)
                            return@executeAsync
                        }
                        transactionToShow.postValue(transaction.toExternalModel())
                        transactionToRefund.postValue(transaction)
                    }
                }
            }
        )
    }

    fun refundTransaction(amount: BigDecimal?) {
        val transaction = transactionToRefund.value ?: return
        showProgress()
        TaskExecutor.executeAsync(
            taskToExecute = {
                if (amount != null) {
                    transaction
                        .refund(amount)
                } else {
                    transaction
                        .refund()
                }
                    .withCurrency(Currency)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)
            },
            onFinished = {
                hideProgress()
                when (it) {
                    is TaskResult.Error -> showError(it.exception.message)
                    is TaskResult.Success -> {
                        transactionToShow.postValue(it.data.toExternalModel())
                        transactionToRefund.postValue(null)
                    }
                }
            }
        )
    }

    fun reverseTransaction() {
        val transaction = transactionToRefund.value ?: return
        showProgress()
        TaskExecutor.executeAsync(
            taskToExecute = {
                transaction
                    .reverse()
                    .withCurrency(Currency)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)
            },
            onFinished = {
                hideProgress()
                when (it) {
                    is TaskResult.Error -> showError(it.exception.message)
                    is TaskResult.Success -> {
                        transactionToShow.postValue(it.data.toExternalModel())
                        transactionToRefund.postValue(null)
                    }
                }
            }
        )
    }

    private fun Transaction.toExternalModel() = TransactionSuccessModel(
        id = transactionId,
        resultCode = responseCode,
        timeCreated = timestamp,
        status = responseMessage,
        amount = balanceAmount?.toString() ?: ""
    )

    companion object {
        private const val Currency = "USD"
    }
}
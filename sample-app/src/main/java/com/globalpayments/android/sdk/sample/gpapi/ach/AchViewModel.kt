package com.globalpayments.android.sdk.sample.gpapi.ach

import androidx.lifecycle.MutableLiveData
import com.global.api.entities.Address
import com.global.api.entities.Customer
import com.global.api.entities.enums.AccountType
import com.global.api.entities.enums.SecCode
import com.global.api.paymentMethods.eCheck
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success.TransactionSuccessModel
import com.globalpayments.android.sdk.task.TaskExecutor
import com.globalpayments.android.sdk.task.TaskResult
import java.math.BigDecimal


class AchViewModel : BaseViewModel() {

    val successTransaction = MutableLiveData<TransactionSuccessModel>()
    val errorTransaction = MutableLiveData<String>()

    private val eCheck = eCheck()

    fun onAccountTypeSelected(accountType: AccountType) {
        eCheck.accountType = accountType
    }

    fun onSecCodeSelected(secCode: SecCode) {
        eCheck.secCode = secCode
    }

    fun makePayment(
        amount: BigDecimal,
        accountHolderName: String,
        routingNumber: String,
        accountNumber: String,
        billingAddress: Address,
        customer: Customer,
        orderType: OrderType
    ) {
        showProgress()
        eCheck.apply {
            this.accountNumber = accountNumber
            this.routingNumber = routingNumber
            this.checkHolderName = accountHolderName
        }
        when (orderType) {
            OrderType.Charge -> charge(amount, billingAddress, customer)
            OrderType.Refund -> refund(amount, billingAddress, customer)
        }
    }

    private fun charge(
        amount: BigDecimal,
        billingAddress: Address,
        customer: Customer
    ) {
        TaskExecutor.executeAsync(
            taskToExecute = {
                eCheck
                    .charge(amount)
                    .withCurrency(Currency)
                    .withAddress(billingAddress)
                    .withCustomer(customer)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)
            },
            onFinished = {
                hideProgress()
                when (it) {
                    is TaskResult.Error -> errorTransaction.postValue(
                        it.exception.message ?: "Error"
                    )
                    is TaskResult.Success -> {
                        val transaction = it.data
                        successTransaction.postValue(
                            TransactionSuccessModel(
                                id = transaction.transactionId,
                                resultCode = transaction.responseCode,
                                timeCreated = transaction.timestamp,
                                status = transaction.responseMessage,
                                amount = transaction.balanceAmount?.toString() ?: ""
                            )
                        )
                    }
                }
            }
        )
    }

    private fun refund(
        amount: BigDecimal,
        billingAddress: Address,
        customer: Customer
    ) {
        TaskExecutor.executeAsync(
            taskToExecute = {
                eCheck
                    .refund(amount)
                    .withCurrency(Currency)
                    .withAddress(billingAddress)
                    .withCustomer(customer)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)
            },
            onFinished = {
                hideProgress()
                when (it) {
                    is TaskResult.Error -> errorTransaction.postValue(
                        it.exception.message ?: "Error"
                    )
                    is TaskResult.Success -> {
                        val transaction = it.data
                        successTransaction.postValue(
                            TransactionSuccessModel(
                                id = transaction.transactionId,
                                resultCode = transaction.responseCode,
                                timeCreated = transaction.timestamp,
                                status = transaction.responseMessage,
                                amount = transaction.balanceAmount?.toString() ?: ""
                            )
                        )
                    }
                }
            }
        )
    }

    companion object {
        private const val Currency = "USD"
    }

    enum class OrderType {
        Charge, Refund
    }
}
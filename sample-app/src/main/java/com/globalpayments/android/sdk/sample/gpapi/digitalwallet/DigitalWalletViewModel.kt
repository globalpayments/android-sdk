package com.globalpayments.android.sdk.sample.gpapi.digitalwallet

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.global.api.entities.Transaction
import com.global.api.entities.enums.MobilePaymentMethodType
import com.global.api.entities.enums.TransactionModifier
import com.global.api.paymentMethods.CreditCardData
import com.globalpayments.android.sdk.TaskExecutor
import com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success.TransactionSuccessModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import java.math.BigDecimal


class DigitalWalletViewModel(application: Application) : AndroidViewModel(application) {

    // A client for interacting with the Google Pay API.
    private val paymentsClient: PaymentsClient =
        DigitalWalletUtils.createPaymentsClient(application)

    // LiveData with the result of whether the user can pay using Google Pay
    private val _canUseGooglePay: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().also {
            fetchCanUseGooglePay()
        }
    }
    val canUseGooglePay: LiveData<Boolean> = _canUseGooglePay

    private val _paymentSuccess = MutableLiveData<TransactionSuccessModel>()
    val transactionSuccessModel: LiveData<TransactionSuccessModel> = _paymentSuccess

    private val _paymentError = MutableLiveData<String>()
    val paymentError: LiveData<String> = _paymentError

    private val _progressStatus = MutableLiveData<Boolean>()
    val progressStatus: LiveData<Boolean> = _progressStatus


    /**
     * Determine the user's ability to pay with a payment method supported by your app and display
     * a Google Pay payment button.
     *
     * @return a [LiveData] object that holds the future result of the call.
     * @see [](https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient.html.isReadyToPay)
    ) */
    private fun fetchCanUseGooglePay() {
        val isReadyToPayJson = DigitalWalletUtils.isReadyToPayRequest()
        if (isReadyToPayJson == null) _canUseGooglePay.value = false

        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                _canUseGooglePay.value = completedTask.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                Log.w("isReadyToPay failed", exception)
                _canUseGooglePay.value = false
            }
        }
    }

    /**
     * Creates a [Task] that starts the payment process with the transaction details included.
     *
     * @param priceCents the price to show on the payment sheet.
     * @return a [Task] with the payment information.
     * @see [](https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient#loadPaymentData(com.google.android.gms.wallet.PaymentDataRequest)
    ) */
    fun getLoadPaymentDataTask(priceCents: String): Task<PaymentData> {
        val paymentDataRequestJson = DigitalWalletUtils.getPaymentDataRequest(priceCents)
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        return paymentsClient.loadPaymentData(request)
    }

    fun makeTransactionWithGooglePay(token: String, amount: BigDecimal) {
        _progressStatus.postValue(true)

        TaskExecutor.executeAsync(object : TaskExecutor.Task<Transaction?> {
            @Throws(Exception::class)
            override fun executeAsync(): Transaction {
                return executeRequest(token, amount)
            }

            override fun onSuccess(transaction: Transaction?) {
                _progressStatus.postValue(false)
                transaction ?: return
                _paymentSuccess.postValue(
                    TransactionSuccessModel(
                        id = transaction.transactionId,
                        resultCode = transaction.responseCode,
                        timeCreated = transaction.timestamp,
                        status = transaction.responseMessage,
                        amount = transaction.balanceAmount?.toString() ?: ""
                    )
                )
            }

            override fun onError(exception: Exception) {
                _progressStatus.postValue(false)
                _paymentError.postValue(exception.message ?: "Error processing transaction")
            }
        })
    }

    private fun executeRequest(token: String, amount: BigDecimal): Transaction {
        val card = CreditCardData().apply {
            this.token = token
            this.mobileType = MobilePaymentMethodType.GOOGLEPAY
        }

        return card
            .charge(amount)
            .withCurrency(CURRENCY_CODE)
            .withModifier(TransactionModifier.EncryptedMobile)
            .execute(DEFAULT_GPAPI_CONFIG)
    }


}
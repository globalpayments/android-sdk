package com.globalpayments.android.sdk.sample.gpapi.digitalwallet

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.global.api.entities.enums.MobilePaymentMethodType
import com.global.api.entities.enums.TransactionModifier
import com.global.api.paymentMethods.CreditCardData
import com.global.api.services.GpApiService
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success.TransactionSuccessModel
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success.asInternalModel
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfiguration
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfigurationUtils.buildDefaultGpApiConfig
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private val _accessToken = MutableLiveData<String>()
    val accessToken: LiveData<String> = _accessToken


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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val card = CreditCardData().apply {
                    this.token = token
                    this.mobileType = MobilePaymentMethodType.GOOGLEPAY
                }

                val transaction = card
                    .charge(amount)
                    .withCurrency(CURRENCY_CODE)
                    .withModifier(TransactionModifier.EncryptedMobile)
                    .withMaskedDataResponse(false)
                    .execute(DEFAULT_GPAPI_CONFIG)

                _paymentSuccess.postValue(transaction.asInternalModel())
            } catch (exception: Exception) {
                _paymentError.postValue(exception.message ?: "Error processing transaction")
            } finally {
                _progressStatus.postValue(false)
            }
        }
    }

    fun getAccessToken() {
        viewModelScope.launch(Dispatchers.IO) {
            _progressStatus.postValue(true)
            try {
                val accessToken = GpApiService.generateTransactionKey(buildDefaultGpApiConfig(GPAPIConfiguration.createDefaultConfig())).accessToken
                this@DigitalWalletViewModel._accessToken.postValue(accessToken)
            } catch (exception: Exception) {
                _paymentError.postValue(exception.message ?: "Error processing transaction")
            } finally {
                _progressStatus.postValue(false)
            }
        }
    }

    fun captureTransaction(cardToken: String, amount: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _progressStatus.postValue(true)
            try {
                val card = CreditCardData().apply {
                    token = cardToken
                    mobileType = MobilePaymentMethodType.CLICK_TO_PAY
                    cardHolderName = "James Mason#"
                }

                val response =
                    card
                        .charge(BigDecimal(amount))
                        .withCurrency("EUR")
                        .withModifier(TransactionModifier.EncryptedMobile)
                        .withMaskedDataResponse(false)
                        .execute(Constants.DEFAULT_GPAPI_CONFIG)
                _paymentSuccess.postValue(response.asInternalModel())
            } catch (exception: Exception) {
                _paymentError.postValue(exception.message ?: "Error processing transaction")
            } finally {
                _progressStatus.postValue(false)
            }
        }
    }
}

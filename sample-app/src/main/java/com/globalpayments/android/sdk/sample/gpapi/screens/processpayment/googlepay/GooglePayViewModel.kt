package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.googlepay

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Transaction
import com.global.api.entities.enums.MobilePaymentMethodType
import com.global.api.entities.enums.TransactionModifier
import com.global.api.paymentMethods.CreditCardData
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class GooglePayViewModel(context: Context) : ViewModel() {

    private val googlePayClient: PaymentsClient = GooglePayUtils.createPaymentsClient(context)

    val screenModel = MutableStateFlow(GooglePayScreenModel())

    init {
        checkIfGooglePayIsAvailable()
    }

    fun onAmountChanged(value: String) = screenModel.update { it.copy(amount = value) }

    fun makePayment() {
        val amount = screenModel.value.amount
        val paymentDataRequest = GooglePayUtils.getPaymentDataRequest(amount)
        val request = PaymentDataRequest.fromJson(paymentDataRequest.toString())
        googlePayClient
            .loadPaymentData(request)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    handlePaymentSuccess(task.result)
                } else {
                    when (val exception = task.exception) {
                        is ResolvableApiException -> screenModel.update { it.copy(pendingIntent = exception.resolution) }
                        is ApiException -> handleError(exception.message)
                        else -> handleError("Unexpected non API" + " exception when trying to deliver the task result to an activity!")
                    }
                }
            }
    }

    fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson()
        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")
            val token = paymentMethodData
                .getJSONObject("tokenizationData")
                .getString("token")
            makeTransactionWithGooglePay(token)

        } catch (error: JSONException) {
            handleError(error.message)
        }
    }

    private fun makeTransactionWithGooglePay(token: String) {
        val amount = screenModel.value.amount.toBigDecimalOrNull() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val card = CreditCardData().apply {
                    this.token = token
                    this.mobileType = MobilePaymentMethodType.GOOGLEPAY
                }

                val response = card
                    .charge(amount)
                    .withCurrency("USD")
                    .withModifier(TransactionModifier.EncryptedMobile)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)

                val responseToShow = response.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, responseToShow)
                screenModel.update { it.copy(gpSnippetResponseModel = gpSnippetResponseModel) }

            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(Transaction::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    private fun handleError(message: String?) {
        screenModel.update {
            val gpSnippetResponseModel =
                GPSnippetResponseModel(Transaction::class.java.simpleName, listOf("Error" to (message ?: "")), true)
            it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
        }
    }

    private fun checkIfGooglePayIsAvailable() {
        val isReadyToPayJson = GooglePayUtils.isReadyToPayRequest()
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
        googlePayClient
            .isReadyToPay(request)
            .addOnCompleteListener { task ->
                val isGooglePayAvailable = try {
                    task.getResult(ApiException::class.java)
                } catch (exception: ApiException) {
                    Log.w("isReadyToPay failed", exception)
                    false
                }
                screenModel.update { it.copy(isGooglePayAvailable = isGooglePayAvailable) }
            }
    }
}

package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.ctp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Transaction
import com.global.api.entities.enums.MobilePaymentMethodType
import com.global.api.entities.enums.TransactionModifier
import com.global.api.paymentMethods.CreditCardData
import com.global.api.services.GpApiService
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import com.globalpayments.android.sdk.sample.utils.AppPreferences
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfiguration
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfigurationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class CtpViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedAppPreferences = AppPreferences(application)
    val screenModel = MutableStateFlow(CTPModel())

    fun onAmountChanged(value: String) = screenModel.update { it.copy(amount = value) }
    fun reset() = screenModel.update { CTPModel() }

    fun requestAccessToken() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accessToken = GpApiService
                    .generateTransactionKey(
                        GPAPIConfigurationUtils.buildDefaultGpApiConfig(
                            sharedAppPreferences.gpAPIConfiguration ?: GPAPIConfiguration.fromBuildConfig()
                        ).apply {
                            permissions = arrayOf("PMT_POST_Create_Single", "ACC_GET_Single")
                        }
                    ).accessToken
                screenModel.update { it.copy(accessToken = accessToken) }
            } catch (exception: Exception) {
                onErrorReceived(exception)
            }
        }
    }

    fun captureTransaction(cardToken: String, amount: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = captureCTP(cardToken, amount)
                val responseAsMap = response.mapNotNullFields()
                val responseToShow = GPSampleResponseModel(
                    transactionId = response.transactionId,
                    response = listOf(
                        "Time" to response.timestamp,
                        "Status" to response.responseMessage
                    )
                )
                val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, responseAsMap)
                screenModel.update {
                    it.copy(
                        gpSampleResponseModel = responseToShow,
                        gpSnippetResponseModel = gpSnippetResponseModel
                    )
                }
            } catch (exception: Exception) {
                onErrorReceived(exception)
            }
        }
    }

    fun onErrorReceived(exception: Exception) {
        val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
        screenModel.value = screenModel.value.copy(gpSnippetResponseModel = gpSnippetResponseModel)
        Log.e("CtpViewModel", exception.message ?: "Error")
    }

    private suspend fun captureCTP(cardToken: String, amount: String) = withContext(Dispatchers.IO) {
        val card = CreditCardData().apply {
            token = cardToken
            mobileType = MobilePaymentMethodType.CLICK_TO_PAY
            cardHolderName = "James Mason#"
        }
        return@withContext card
            .charge(BigDecimal(amount))
            .withCurrency("EUR")
            .withModifier(TransactionModifier.EncryptedMobile)
            .withMaskedDataResponse(true)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }
}

data class CTPModel(
    val amount: String = "",
    val accessToken: String = "",
    val gpSampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

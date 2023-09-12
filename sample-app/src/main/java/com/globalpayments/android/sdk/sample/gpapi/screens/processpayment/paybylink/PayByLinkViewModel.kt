package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.paybylink

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.PayByLinkData
import com.global.api.entities.enums.PayByLinkType
import com.global.api.entities.enums.PaymentMethodName
import com.global.api.entities.enums.PaymentMethodUsageMode
import com.global.api.entities.enums.Target
import com.global.api.entities.reporting.PayByLinkSummary
import com.global.api.services.PayByLinkService
import com.global.api.utils.GenerationUtils
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.util.Date

class PayByLinkViewModel : ViewModel() {

    val screenModel = MutableStateFlow(PayByLinkScreenModel())

    fun onAmountChanged(value: String) = screenModel.update { it.copy(amount = value) }
    fun onDescriptionChanged(value: String) = screenModel.update { it.copy(description = value) }
    fun onExpirationDateChanged(date: Date) = screenModel.update { it.copy(expirationDate = date) }
    fun onPaymentUsageModeChanged(value: PaymentMethodUsageMode) =
        screenModel.update { it.copy(usageMode = value, usageLimit = if (value == PaymentMethodUsageMode.SINGLE) "1" else it.usageLimit) }

    fun onUsageLimitChanged(value: String) = screenModel.update { it.copy(usageLimit = value) }
    fun resetScreen() = screenModel.update { PayByLinkScreenModel() }

    fun getPayLink() {
        screenModel.update { it.copy(gpSnippetResponseModel = GPSnippetResponseModel()) }
        viewModelScope.launch {
            try {
                val response = getPayLink(screenModel.value)
                screenModel.update { it.copy(payLinkId = response.payByLinkResponse.id) }
                delay(3_000)
                checkPayLinkStatus()
            } catch (exception: Exception) {
                onError(exception)
            }
        }
    }

    private suspend fun getPayLink(model: PayByLinkScreenModel) = withContext(Dispatchers.IO) {
        val payLinkData = PayByLinkData()
            .setType(PayByLinkType.PAYMENT)
            .setUsageMode(model.usageMode)
            .setAllowedPaymentMethods(arrayOf(PaymentMethodName.Card.getValue(Target.GP_API)))
            .setName(model.description)
            .setExpirationDate(DateTime(model.expirationDate?.time))
            .setUsageLimit(model.usageLimit.toIntOrNull())
            .setIsShippable(false)
            .setImages(emptyList())
            .setReturnUrl("https://www.example.com/returnUrl")
            .setCancelUrl("https://www.example.com/returnUrl")
            .setStatusUpdateUrl("https://www.example.com/returnUrl")

        PayByLinkService
            .create(payLinkData, model.amount.toBigDecimalOrNull())
            .withCurrency("GBP")
            .withClientTransactionId(GenerationUtils.generateRecurringKey())
            .withDescription(model.description)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    fun checkPayLinkStatus() {
        val payLinkId = screenModel.value.payLinkId.takeIf(String::isNotBlank) ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val summary = PayByLinkService.payByLinkDetail(payLinkId).execute(Constants.DEFAULT_GPAPI_CONFIG)
                val gpSampleResponseModel = GPSampleResponseModel(
                    summary.id, listOf(
                        "URL" to summary.url,
                        "Status" to summary.status.toString()
                    )
                )
                val gpSnippetResponseModel = GPSnippetResponseModel(PayByLinkSummary::class.java.simpleName, summary.mapNotNullFields())
                screenModel.update {
                    it.copy(
                        uriToOpen = summary.url,
                        gpSampleResponseModel = gpSampleResponseModel,
                        gpSnippetResponseModel = gpSnippetResponseModel
                    )
                }
            } catch (exception: Exception) {
                onError(exception)
            }
        }
    }

    private fun onError(exception: Exception) {
        screenModel.update {
            val gpSnippetResponseModel =
                GPSnippetResponseModel(PayByLinkSummary::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
            it.copy(
                gpSnippetResponseModel = gpSnippetResponseModel,
                gpSampleResponseModel = null
            )
        }
    }
}

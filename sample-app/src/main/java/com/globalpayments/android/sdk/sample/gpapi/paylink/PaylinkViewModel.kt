package com.globalpayments.android.sdk.sample.gpapi.paylink

import com.global.api.entities.PayLinkData
import com.global.api.entities.enums.PayLinkType
import com.global.api.entities.enums.PaymentMethodName
import com.global.api.entities.enums.PaymentMethodUsageMode
import com.global.api.entities.enums.Target
import com.global.api.entities.reporting.PayLinkSummary
import com.global.api.services.PayLinkService
import com.global.api.utils.GenerationUtils
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel
import com.globalpayments.android.sdk.sample.utils.SingleLiveEvent
import com.globalpayments.android.sdk.sample.utils.launchPrintingError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class PaylinkViewModel : BaseViewModel() {

    val paylinkSummary = SingleLiveEvent<PayLinkSummary?>()

    fun getPaylink(paylinkDataModel: PaylinkDataModel) = launchPrintingError {
        paylinkSummary.postValue(null)

        if (paylinkDataModel.usageMode == PaymentMethodUsageMode.MULTIPLE && paylinkDataModel.usageLimit < 2) {
            showError("Usage limit must be greater than 2")
            return@launchPrintingError
        }

        val paylinkData = PayLinkData()
            .setType(PayLinkType.PAYMENT)
            .setUsageMode(paylinkDataModel.usageMode)
            .setAllowedPaymentMethods(arrayOf(PaymentMethodName.Card.getValue(Target.GP_API)))
            .setName(paylinkDataModel.name)
            .setExpirationDate(paylinkDataModel.expirationDate)
            .setUsageLimit(paylinkDataModel.usageLimit)
            .setIsShippable(false)
            .setImages(emptyList())
            .setReturnUrl("https://www.example.com/returnUrl")
            .setCancelUrl("https://www.example.com/returnUrl")
            .setStatusUpdateUrl("https://www.example.com/returnUrl")

        val response = PayLinkService
            .create(paylinkData, paylinkDataModel.amount)
            .withCurrency("GBP")
            .withClientTransactionId(GenerationUtils.generateRecurringKey())
            .withDescription(paylinkDataModel.name)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)

        delay(3_000)

        getPaylinkSummary(response.payLinkResponse.id)
    }

    fun refreshPaylinkSummary() = launchPrintingError {
        val summary = paylinkSummary.value ?: return@launchPrintingError
        getPaylinkSummary(summary.id)
    }

    private suspend fun getPaylinkSummary(paylinkId: String) = withContext(Dispatchers.IO) {
        val summary = PayLinkService.payLinkDetail(paylinkId).execute(Constants.DEFAULT_GPAPI_CONFIG)
        paylinkSummary.postValue(summary)
    }
}

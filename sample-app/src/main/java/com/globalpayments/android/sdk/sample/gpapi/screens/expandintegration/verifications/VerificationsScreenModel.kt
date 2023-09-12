package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.verifications

import com.globalpayments.android.sdk.model.PaymentCardModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.utils.FingerprintMethodUsageMode

data class VerificationsScreenModel(
    val currentTab: Int = 0,
    val paymentCard: PaymentCardModel = PaymentCardModel.VISA_SUCCESSFUL,
    val cardNumber: String = PaymentCardModel.VISA_SUCCESSFUL.cardNumber,
    val expiryMonth: String = PaymentCardModel.VISA_SUCCESSFUL.expiryMonth,
    val expiryYear: String = PaymentCardModel.VISA_SUCCESSFUL.expiryYear,
    val cvn: String = PaymentCardModel.VISA_SUCCESSFUL.cvnCvv,
    val currency: String = "USD",
    val fingerPrintEnabled: Boolean = false,
    val fingerPrintUsageMethod: FingerprintMethodUsageMode? = null,
    val idempotencyKey: String = "",
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

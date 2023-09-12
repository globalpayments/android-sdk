package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.storedpayments

import com.global.api.entities.enums.PaymentMethodUsageMode
import com.globalpayments.android.sdk.model.PaymentCardModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.utils.FingerprintMethodUsageMode

data class StoredPaymentsScreenModel(
    val paymentOperation: PaymentOperation = PaymentOperation.TOKENIZE,
    val paymentCard: PaymentCardModel = PaymentCardModel.VISA_SUCCESSFUL,
    val tokenUsageMode: PaymentMethodUsageMode = PaymentMethodUsageMode.SINGLE,
    val fingerprintEnabled: Boolean = false,
    val fingerPrintUsageMethod: FingerprintMethodUsageMode? = null,
    val paymentMethodId: String = "",
    val cardNumber: String = PaymentCardModel.VISA_SUCCESSFUL.cardNumber,
    val expiryMonth: String = PaymentCardModel.VISA_SUCCESSFUL.expiryMonth,
    val expiryYear: String = PaymentCardModel.VISA_SUCCESSFUL.expiryYear,
    val cvn: String = PaymentCardModel.VISA_SUCCESSFUL.cvnCvv,
    val cardHolderName: String = "",
    val currency: String = "USD",
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

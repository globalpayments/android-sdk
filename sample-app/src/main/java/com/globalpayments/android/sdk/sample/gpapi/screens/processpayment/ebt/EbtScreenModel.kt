package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.ebt

import com.global.api.entities.Transaction
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

enum class PaymentType {
    Charge, Refund
}

data class EbtScreenModel(
    val amount: String = "",
    val cardNumber: String = "",
    val cardYear: String = "",
    val cardMonth: String = "",
    val pinBlock: String = "",
    val cardHolderName: String = "",
    val paymentType: PaymentType? = null,

    val transaction: Transaction? = null,

    val gpSampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

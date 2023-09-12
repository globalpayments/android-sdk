package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.paypal

import com.global.api.entities.Transaction
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel


enum class PaymentType {
    Charge, Authorize
}

enum class PayPalTransactionStatus {
    Pending,
    Authorized,
    Charged,
    Done
}

data class PayPalScreenModel(
    val amount: String = "",
    val paymentType: PaymentType = PaymentType.Charge,
    val transactionStatus: PayPalTransactionStatus = PayPalTransactionStatus.Pending,
    val pendingTransaction: Transaction? = null,
    val gpSampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel(),
    val retryRemaining: Int = 3,
)

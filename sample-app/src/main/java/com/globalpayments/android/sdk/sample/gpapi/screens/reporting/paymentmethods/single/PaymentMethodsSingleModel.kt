package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.paymentmethods.single

import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

data class PaymentMethodsSingleModel(
    val paymentMethodId: String = "",
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

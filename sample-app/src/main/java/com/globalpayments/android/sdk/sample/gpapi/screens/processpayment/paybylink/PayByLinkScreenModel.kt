package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.paybylink

import com.global.api.entities.enums.PaymentMethodUsageMode
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import java.util.Date

data class PayByLinkScreenModel(
    val amount: String = "",
    val usageMode: PaymentMethodUsageMode = PaymentMethodUsageMode.SINGLE,
    val usageLimit: String = "1",
    val description: String = "",
    val expirationDate: Date? = null,

    val gpSampleResponseModel: GPSampleResponseModel? = null,
    val payLinkId: String = "",
    val uriToOpen: String? = "",
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)



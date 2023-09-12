package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.hostedfields

import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.unifiedpaymentsapi.NetceteraParams

data class HostedFieldsScreenModel(
    val paymentAmount: String = "",
    val accessToken: String = "",

    val netceteraTransactionParams: NetceteraParams? = null,

    val gpSampleResponse: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

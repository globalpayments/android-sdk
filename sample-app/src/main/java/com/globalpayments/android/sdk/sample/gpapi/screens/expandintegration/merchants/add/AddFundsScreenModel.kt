package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.merchants.add

import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

data class AddFundsScreenModel(

    val amount: String = "10",
    val accountNumber: String = "FMA_a78b841dfbd14803b3a31e4e0c514c72",
    val merchantId: String = "MER_5096d6b88b0b49019c870392bd98ddac",

    val sampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.payers.create

import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

data class CreatePayerScreenModel(
    val id: String = "PYR_",
    val firstName: String = "James",
    val lastName: String = "Mason",

    val sampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

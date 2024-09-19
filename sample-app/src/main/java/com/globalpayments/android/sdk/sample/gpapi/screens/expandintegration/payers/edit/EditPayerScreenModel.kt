package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.payers.edit

import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

data class EditPayerScreenModel(
    val id: String = "PYR_",
    val firstName: String = "James",
    val lastName: String = "Mason",

    val sampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

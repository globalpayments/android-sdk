package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.account.single

import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

data class AccountsSingleModel(
    val accountId: String = "",
    val gpSampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

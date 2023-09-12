package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.deposits.single

import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

data class DepositsSingleModel(
    val depositId: String = "",
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

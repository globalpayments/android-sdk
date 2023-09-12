package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.actions.single

import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

data class ActionsSingleModel(
    val actionId: String = "",
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

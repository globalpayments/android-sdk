package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.disputes.single

import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

data class DisputesSingleModel(
    val disputeId: String = "",
    val isFromSettlements: Boolean = false,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

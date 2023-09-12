package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.transactions.single

import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

data class TransactionSingleModel(
    val transactionId: String = "",
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

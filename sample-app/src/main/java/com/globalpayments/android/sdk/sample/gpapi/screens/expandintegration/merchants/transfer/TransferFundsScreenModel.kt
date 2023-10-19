package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.merchants.transfer

import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

data class TransferFundsScreenModel(
    val senderMerchantId: String = "",
    val receiverMerchantId: String = "",
    val transferAmount: String = "",

    val sampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

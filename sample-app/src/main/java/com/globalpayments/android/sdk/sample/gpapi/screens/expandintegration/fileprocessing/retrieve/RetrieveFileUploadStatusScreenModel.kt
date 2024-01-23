package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.fileprocessing.retrieve

import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

data class RetrieveFileUploadStatusScreenModel(
    val fileId: String = "",
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

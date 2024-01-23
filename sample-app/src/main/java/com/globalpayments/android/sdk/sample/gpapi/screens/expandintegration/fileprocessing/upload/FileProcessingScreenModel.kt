package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.fileprocessing.upload

import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import java.io.File

data class FileProcessingScreenModel(
    val fileToUpload: File? = null,
    val sampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

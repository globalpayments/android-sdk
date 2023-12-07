package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.merchants.document

import com.global.api.entities.enums.DocumentCategory
import com.global.api.entities.enums.FileType
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

data class MerchantUploadDocumentScreenModel(

    val merchantId: String = "MER_5096d6b88b0b49019c870392bd98ddac",
    val document: Document? = null,
    val documentType: FileType = FileType.TIF,
    val documentCategory: DocumentCategory = DocumentCategory.IDENTITY_VERIFICATION,

    val sampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

data class Document(
    val name: String,
    val encodedContent: String
)

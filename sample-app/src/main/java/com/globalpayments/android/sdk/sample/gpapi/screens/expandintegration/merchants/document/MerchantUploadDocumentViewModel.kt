package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.merchants.document

import android.app.Application
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Transaction
import com.global.api.entities.User
import com.global.api.entities.enums.DocumentCategory
import com.global.api.entities.enums.FileType
import com.global.api.entities.enums.UserType
import com.global.api.entities.propay.DocumentUploadData
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class MerchantUploadDocumentViewModel(application: Application) : AndroidViewModel(application) {

    val screenModel: MutableStateFlow<MerchantUploadDocumentScreenModel> = MutableStateFlow(MerchantUploadDocumentScreenModel())

    fun onMerchantIdChanged(value: String) = screenModel.update { it.copy(merchantId = value) }
    fun onDocumentTypeChanged(value: FileType) = screenModel.update { it.copy(documentType = value) }
    fun onDocumentCategoryChanged(value: DocumentCategory) = screenModel.update { it.copy(documentCategory = value) }

    @OptIn(ExperimentalEncodingApi::class)
    fun loadDocument(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val encodedDocument = getApplication<Application>()
                .contentResolver
                .openInputStream(uri)
                ?.use { stream ->
                    Base64.encode(stream.readBytes())
                } ?: return@launch
            val documentName = DocumentFile.fromSingleUri(getApplication(), uri)?.name ?: return@launch
            screenModel.update { it.copy(document = Document(documentName, encodedDocument)) }
        }
    }

    fun uploadDocument() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val model = screenModel.value
                val response = uploadDocument(model)
                val resultToShow = response.mapNotNullFields()
                val sampleResponse = GPSampleResponseModel(
                    transactionId = response.userReference.userId, response = listOf(
                        "Response Code" to response.responseCode,
                        "Document" to response.document.name,
                        "Document Status" to response.document.status,
                        "Document Format" to response.document.format.name,
                        "Document Category" to response.document.category.name
                    )
                )
                val gpSnippetResponseModel = GPSnippetResponseModel(User::class.java.simpleName, resultToShow)
                screenModel.update {
                    it.copy(
                        sampleResponseModel = sampleResponse,
                        gpSnippetResponseModel = gpSnippetResponseModel
                    )
                }
            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(Transaction::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    private fun uploadDocument(screenModel: MerchantUploadDocumentScreenModel): User {
        val merchant = User.fromId(screenModel.merchantId, UserType.MERCHANT)
        val document = DocumentUploadData().apply {
            document = screenModel.document?.encodedContent
            docType = screenModel.documentType
            docCategory = screenModel.documentCategory
        }
        return merchant
            .uploadDocument(document)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    fun resetScreen() = screenModel.update { MerchantUploadDocumentScreenModel() }

}

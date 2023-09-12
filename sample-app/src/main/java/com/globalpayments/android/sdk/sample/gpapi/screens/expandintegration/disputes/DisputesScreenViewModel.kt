package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.disputes

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.DisputeDocument
import com.global.api.entities.Transaction
import com.global.api.entities.reporting.DisputeSummary
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import com.globalpayments.android.sdk.utils.Base64Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class DisputesScreenViewModel : ViewModel() {

    val screenModel: MutableStateFlow<DisputesScreenModel> = MutableStateFlow(DisputesScreenModel())

    fun onOperationTypeChanged(value: DisputeOperationType) {
        screenModel.update { it.copy(operationType = value) }
    }

    fun onDisputeIdChanged(value: String) {
        screenModel.update { it.copy(disputeId = value) }
    }

    fun onIdempotencyKeyChanged(value: String) {
        screenModel.update { it.copy(idempotencyKey = value) }
    }

    fun onFileSelected(fileUri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.Default) {
            val encodedFile = Base64Utils.getBase64EncodedContent(fileUri, context)
            val documentFile = DisputeDocumentModel(
                encodedFile,
                DisputeDocumentType.SALES_RECEIPT,
                fileUri.lastPathSegment ?: UUID.randomUUID().toString()
            )
            screenModel.update { it.copy(files = it.files + documentFile) }
        }
    }

    fun onFileTypeChanged(document: DisputeDocumentModel, type: DisputeDocumentType) {
        val files = screenModel.value.files
        val newFile = document.copy(fileType = type)
        val newList = files.map { if (it == document) newFile else it }
        screenModel.update { it.copy(files = newList) }
    }

    fun removeFile(document: DisputeDocumentModel) {
        screenModel.update { it.copy(files = it.files - document) }
    }

    fun onSubmitClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = when (screenModel.value.operationType) {
                    DisputeOperationType.ACCEPT -> acceptDispute(screenModel.value)
                    DisputeOperationType.CHALLENGE -> challengeDispute(screenModel.value)
                }
                val responseToShow = response.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, responseToShow)
                screenModel.update {
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
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

    private fun acceptDispute(model: DisputesScreenModel): Transaction {
        val disputeSummary = DisputeSummary().apply {
            caseId = model.disputeId
        }
        return disputeSummary
            .accept()
            .withIdempotencyKey(model.idempotencyKey)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    private fun challengeDispute(model: DisputesScreenModel): Transaction {
        val disputeSummary = DisputeSummary().apply {
            caseId = model.disputeId
        }

        val disputeDocuments = model
            .files
            .map {
                DisputeDocument().apply {
                    type = it.fileType.toString()
                    base64Content = it.fileEncoded
                }
            }
            .let { ArrayList(it) }

        return disputeSummary
            .challenge(disputeDocuments)
            .withIdempotencyKey(model.idempotencyKey)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }
}

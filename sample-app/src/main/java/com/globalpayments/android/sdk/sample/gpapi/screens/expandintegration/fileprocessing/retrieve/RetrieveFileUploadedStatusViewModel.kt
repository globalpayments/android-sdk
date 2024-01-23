package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.fileprocessing.retrieve

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.FileProcessor
import com.global.api.services.FileProcessingService
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class RetrieveFileUploadedStatusViewModel : ViewModel() {

    val screenModel: MutableStateFlow<RetrieveFileUploadStatusScreenModel> = MutableStateFlow(RetrieveFileUploadStatusScreenModel())

    fun fileIdChanged(value: String) = screenModel.update { it.copy(fileId = value) }

    fun getFileStatus() {
        val resourceId = screenModel.value.fileId.takeIf(String::isNotBlank) ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = getFileDetails(resourceId)
                val responseToShow = response.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(FileProcessor::class.java.simpleName, responseToShow)
                screenModel.update {
                    it.copy(
                        gpSnippetResponseModel = gpSnippetResponseModel
                    )
                }
            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(FileProcessor::class.java.simpleName, listOf("Error" to (exception.localizedMessage ?: "")), true)
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    private fun getFileDetails(resourceId: String): FileProcessor {
        val fileProcessingService = FileProcessingService()
        return fileProcessingService.getDetails(resourceId)
    }
}

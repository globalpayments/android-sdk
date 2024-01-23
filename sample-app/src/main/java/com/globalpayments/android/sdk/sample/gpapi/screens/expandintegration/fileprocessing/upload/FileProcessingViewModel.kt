package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.fileprocessing.upload

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.FileProcessor
import com.global.api.services.FileProcessingService
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class FileProcessingViewModel(application: Application) : AndroidViewModel(application) {

    val screenModel: MutableStateFlow<FileProcessingScreenModel> = MutableStateFlow(FileProcessingScreenModel())

    fun onFileSelected(fileUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val application = getApplication<Application>()
            val cacheDir = application.cacheDir
            val temporaryFile = File(cacheDir, "fileToUpload")
            temporaryFile.createNewFile()
            application
                .contentResolver
                .openInputStream(fileUri)
                ?.use { inputStream ->
                    temporaryFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            screenModel.update { it.copy(fileToUpload = temporaryFile) }
        }
    }

    fun onSubmitClicked() {
        val file = screenModel.value.fileToUpload ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fp = processFile(file)
                val responseToShow = fp.mapNotNullFields()
                val sampleResponse = GPSampleResponseModel(
                    transactionId = fp.resourceId, response = listOf(
                        "Response Code" to fp.responseCode,
                        "Message" to fp.responseMessage,
                        "FilesUploaded" to fp.filesUploaded.size.toString(),
                    )
                )
                val gpSnippetResponseModel = GPSnippetResponseModel(FileProcessor::class.java.simpleName, responseToShow)
                screenModel.update {
                    it.copy(
                        sampleResponseModel = sampleResponse,
                        gpSnippetResponseModel = gpSnippetResponseModel
                    )
                }
            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(FileProcessor::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    private suspend fun processFile(file: File): FileProcessor = withContext(Dispatchers.IO) {
        val fileProcessingService = FileProcessingService()
        val response = fileProcessingService.initiate()
        FileProcessingRequest().invoke(response.uploadUrl, file)
        FileProcessingService().getDetails(response.resourceId)
    }

    fun resetScreen() {
        screenModel.value = FileProcessingScreenModel()
    }

}

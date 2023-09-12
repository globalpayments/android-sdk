package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.batches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.BatchSummary
import com.global.api.services.BatchService
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BatchesScreenViewModel : ViewModel() {

    val screenModel: MutableStateFlow<BatchesScreenModel> = MutableStateFlow(BatchesScreenModel())

    fun onIdChanged(value: String) {
        screenModel.update { it.copy(id = value) }
    }

    fun onSubmitClicked() {
        screenModel.update { it.copy(model = GPSnippetResponseModel()) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val batchNumber = screenModel.value.id
                val response = closeBatch(batchNumber).mapNotNullFields()
                val model = GPSnippetResponseModel(BatchSummary::class.java.simpleName, response, false)
                screenModel.update { it.copy(model = model) }
            } catch (exception: Exception) {
                screenModel.update {
                    val model = GPSnippetResponseModel(
                        BatchSummary::class.java.simpleName,
                        listOf("Exception" to (exception.message ?: "Error occurred")),
                        true
                    )
                    it.copy(model = model)
                }
            }
        }
    }

    private fun closeBatch(batchReferenceNumber: String): BatchSummary {
        return BatchService.closeBatch(batchReferenceNumber, Constants.DEFAULT_GPAPI_CONFIG)
    }
}

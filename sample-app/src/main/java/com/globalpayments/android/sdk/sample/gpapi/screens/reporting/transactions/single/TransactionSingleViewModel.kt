package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.transactions.single

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.TransactionSummary
import com.global.api.services.ReportingService
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionSingleViewModel : ViewModel() {

    val model = MutableStateFlow(TransactionSingleModel())

    fun transactionSingleIdChanged(value: String) {
        model.update { it.copy(transactionId = value) }
    }

    fun getSingleTransaction() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val transactionId = model
                    .value
                    .transactionId
                    .takeIf(String::isNotBlank)
                    ?: throw IllegalArgumentException("Transaction ID is null")
                model.update { it.copy(gpSnippetResponseModel = GPSnippetResponseModel()) }
                val response = getTransaction(transactionId)
                val responseToShow = response.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(TransactionSummary::class.java.simpleName, responseToShow, false)
                model.update { it.copy(gpSnippetResponseModel = gpSnippetResponseModel) }
            } catch (exception: Exception) {
                model.update {
                    val gpSnippetResponseModel = GPSnippetResponseModel(
                        TransactionSummary::class.java.simpleName,
                        listOf("Exception" to (exception.message ?: "Error occurred")),
                        true
                    )
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }

        }
    }

    private fun getTransaction(transactionId: String): TransactionSummary {
        return ReportingService
            .transactionDetail(transactionId)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }
}

package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.disputes.single

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.reporting.DisputeSummary
import com.global.api.services.ReportingService
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DisputesSingleViewModel : ViewModel() {

    val screenModel = MutableStateFlow(DisputesSingleModel())

    fun onDisputeIdChanged(value: String) = screenModel.update { it.copy(disputeId = value) }
    fun updateIsFromSettlements(value: Boolean) = screenModel.update { it.copy(isFromSettlements = value) }

    fun getDispute() {
        val disputeId = screenModel.value.disputeId.takeIf { it.isNotBlank() } ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val summary = getDisputeById(disputeId, screenModel.value.isFromSettlements)
                val responseToShow = summary.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(DisputeSummary::class.java.simpleName, responseToShow, false)
                screenModel.update { it.copy(gpSnippetResponseModel = gpSnippetResponseModel) }
            } catch (exception: Exception) {
                val gpSnippetResponseModel = GPSnippetResponseModel(
                    DisputeSummary::class.java.simpleName,
                    listOf("Exception" to (exception.message ?: "Error occurred")),
                    true
                )
                screenModel.update {
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    private fun getDisputeById(disputeId: String, fromSettlements: Boolean): DisputeSummary {
        val reportBuilder = if (fromSettlements)
            ReportingService.settlementDisputeDetail(disputeId)
        else
            ReportingService.disputeDetail(disputeId)
        return reportBuilder.execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

}

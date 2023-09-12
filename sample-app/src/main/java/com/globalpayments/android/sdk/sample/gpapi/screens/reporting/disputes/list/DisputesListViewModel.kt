package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.disputes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.enums.DisputeSortProperty
import com.global.api.entities.enums.DisputeStage
import com.global.api.entities.enums.DisputeStatus
import com.global.api.entities.enums.SortDirection
import com.global.api.entities.reporting.DataServiceCriteria
import com.global.api.entities.reporting.DisputeSummaryPaged
import com.global.api.entities.reporting.SearchCriteria
import com.global.api.services.ReportingService
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class DisputesListViewModel : ViewModel() {

    val screenModel = MutableStateFlow(DisputesListModel())

    fun updatePage(value: String) = screenModel.update { it.copy(page = value) }
    fun updatePageSize(value: String) = screenModel.update { it.copy(pageSize = value) }
    fun updateIsFromSettlements(value: Boolean) = screenModel.update { it.copy(isFromSettlements = value) }
    fun updateOrder(value: SortDirection?) = screenModel.update { it.copy(order = value) }
    fun updateOrderBy(value: DisputeSortProperty?) = screenModel.update { it.copy(orderBy = value) }
    fun updateBrand(value: String) = screenModel.update { it.copy(brand = value) }
    fun updateArn(value: String) = screenModel.update { it.copy(arn = value) }
    fun updateDisputeStage(value: DisputeStage?) = screenModel.update { it.copy(disputeStage = value) }
    fun updateFromTimeCreated(value: Date) = screenModel.update { it.copy(fromTimeCreated = value) }
    fun updateToTimeCreated(value: Date) = screenModel.update { it.copy(toTimeCreated = value) }
    fun updateMerchantID(value: String) = screenModel.update { it.copy(systemMID = value) }
    fun updateSystemHierarchy(value: String) = screenModel.update { it.copy(systemHierarchy = value) }
    fun updateStatus(value: DisputeStatus?) = screenModel.update { it.copy(status = value) }

    fun getDisputes() {
        val model = screenModel.value
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getDisputes(model)
                val sampleResponse = result.results.map {
                    GPSampleResponseModel(
                        it.caseId,
                        listOf(
                            "Reason" to it.reason,
                            "Status" to it.caseStatus.toString(),
                            "Stage" to it.caseStage,
                            "Result" to it.result
                        )
                    )
                }
                val response = result.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(DisputeSummaryPaged::class.java.simpleName, response)
                screenModel.update { it.copy(responses = sampleResponse, gpSnippetResponseModel = gpSnippetResponseModel) }
            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(DisputeSummaryPaged::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    fun resetListTransaction() {
        screenModel.update { DisputesListModel() }
    }

    fun loadMore() {
        val nextPage = screenModel.value.page.toInt().inc().toString()
        screenModel.update { it.copy(page = nextPage) }
        getDisputes()
    }

    private fun getDisputes(model: DisputesListModel): DisputeSummaryPaged {
        val page: Int = model.page.toInt()
        val pageSize: Int = model.pageSize.toInt()

        return (
            if (model.isFromSettlements)
                ReportingService.findSettlementDisputesPaged(page, pageSize)
            else
                ReportingService.findDisputesPaged(page, pageSize))
            .apply {
                orderBy(model.orderBy, model.order)
                disputeOrderBy = model.orderBy
                where(SearchCriteria.AquirerReferenceNumber, model.arn)
                    .and(SearchCriteria.CardBrand, model.brand)
                    .and<DisputeStatus>(SearchCriteria.DisputeStatus, model.status)
                    .and<DisputeStage>(SearchCriteria.DisputeStage, model.disputeStage)
                    .and<Date>(DataServiceCriteria.StartStageDate, model.fromTimeCreated)
                    .and<Date>(DataServiceCriteria.EndStageDate, model.toTimeCreated)
                    .and(DataServiceCriteria.MerchantId, model.systemMID)
                    .and(DataServiceCriteria.SystemHierarchy, model.systemHierarchy)
            }
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }
}

package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.actions.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.enums.ActionSortProperty
import com.global.api.entities.enums.SortDirection
import com.global.api.entities.reporting.ActionSummaryPaged
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

class ActionsListViewModel : ViewModel() {

    val screenModel = MutableStateFlow(ActionsListModel())

    fun updatePage(value: String) = screenModel.update { it.copy(page = value) }
    fun updatePageSize(value: String) = screenModel.update { it.copy(pageSize = value) }
    fun updateOrder(value: SortDirection?) = screenModel.update { it.copy(order = value) }
    fun updateOrderBy(value: ActionSortProperty?) = screenModel.update { it.copy(orderBy = value) }
    fun updateId(value: String) = screenModel.update { it.copy(id = value) }
    fun updateFromTimeCreated(value: Date) = screenModel.update { it.copy(fromTimeCreated = value) }
    fun updateToTimeCreated(value: Date) = screenModel.update { it.copy(toTimeCreated = value) }
    fun updateType(value: String) = screenModel.update { it.copy(type = value) }
    fun updateResource(value: String) = screenModel.update { it.copy(resource = value) }
    fun updateResourceStatus(value: String) = screenModel.update { it.copy(resourceStatus = value) }
    fun updateResourceId(value: String) = screenModel.update { it.copy(resourceId = value) }
    fun updateMerchantName(value: String) = screenModel.update { it.copy(merchantName = value) }
    fun updateAccountName(value: String) = screenModel.update { it.copy(accountName = value) }
    fun updateAppName(value: String) = screenModel.update { it.copy(appName = value) }
    fun updateVersion(value: String) = screenModel.update { it.copy(version = value) }
    fun updateResponseCode(value: String) = screenModel.update { it.copy(responseCode = value) }
    fun updateResponseHttpCode(value: String) = screenModel.update { it.copy(responseHttpCode = value) }

    fun getActions() {
        val model = screenModel.value
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getActions(model)
                val sampleResponse = result.results.map {
                    GPSampleResponseModel(
                        it.id,
                        listOf(
                            "Time Created" to it.timeCreated.toString(),
                            "App Name" to it.appName,
                            "Type" to it.type
                        )
                    )
                }
                val response = result.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(ActionSummaryPaged::class.java.simpleName, response)
                screenModel.update { it.copy(responses = sampleResponse, gpSnippetResponseModel = gpSnippetResponseModel) }
            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(ActionSummaryPaged::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    fun resetListTransaction() {
        screenModel.update { ActionsListModel() }
    }

    fun loadMore() {
        val nextPage = screenModel.value.page.toInt().inc().toString()
        screenModel.update { it.copy(page = nextPage) }
        getActions()
    }

    private fun getActions(model: ActionsListModel): ActionSummaryPaged {
        val page: Int = model.page.toInt()
        val pageSize: Int = model.pageSize.toInt()
        return ReportingService.findActionsPaged(page, pageSize)
            .orderBy(model.orderBy, model.order)
            .where(SearchCriteria.ActionType, model.type)
            .and(SearchCriteria.ActionId, model.id)
            .and(SearchCriteria.Resource, model.resource)
            .and(SearchCriteria.ResourceStatus, model.resourceStatus)
            .and(SearchCriteria.ResourceId, model.resourceId)
            .and(SearchCriteria.StartDate, model.fromTimeCreated)
            .and(SearchCriteria.EndDate, model.toTimeCreated)
            .and(SearchCriteria.AccountName, model.accountName)
            .and(SearchCriteria.MerchantName, model.merchantName)
            .and(SearchCriteria.AppName, model.appName)
            .and(SearchCriteria.Version, model.version)
            .and(SearchCriteria.ResponseCode, model.responseCode)
            .and(SearchCriteria.HttpResponseCode, model.responseHttpCode)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }
}

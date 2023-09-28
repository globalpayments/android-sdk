package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.account.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.enums.MerchantAccountStatus
import com.global.api.entities.enums.MerchantAccountsSortProperty
import com.global.api.entities.enums.SortDirection
import com.global.api.entities.reporting.ActionSummaryPaged
import com.global.api.entities.reporting.MerchantAccountSummaryPaged
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

class AccountsListViewModel : ViewModel() {

    val screenModel = MutableStateFlow(AccountsListModel())

    fun updatePage(value: String) = screenModel.update { it.copy(page = value) }
    fun updatePageSize(value: String) = screenModel.update { it.copy(pageSize = value) }
    fun updateOrder(value: SortDirection?) = screenModel.update { it.copy(order = value) }
    fun updateOrderBy(value: MerchantAccountsSortProperty?) = screenModel.update { it.copy(orderBy = value) }
    fun updateId(value: String) = screenModel.update { it.copy(id = value) }
    fun updateFromTimeCreated(value: Date) = screenModel.update { it.copy(fromTimeCreated = value) }
    fun updateToTimeCreated(value: Date) = screenModel.update { it.copy(toTimeCreated = value) }
    fun updateName(value: String) = screenModel.update { it.copy(name = value) }
    fun updateStatus(value: MerchantAccountStatus?) = screenModel.update { it.copy(status = value) }

    fun getAccounts() {
        val model = screenModel.value
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getAccounts(model)
                val sampleResponse = result.results.map {
                    GPSampleResponseModel(
                        it.id,
                        listOf(
                            "Name" to it.name,
                            "Status" to it.status.toString(),
                            "Type" to it.type.toString()
                        )
                    )
                }
                val response = result.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(ActionSummaryPaged::class.java.simpleName, response)
                screenModel.update { it.copy(responses = sampleResponse, gpSnippetResponseModel = gpSnippetResponseModel) }
            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(MerchantAccountSummaryPaged::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    fun resetAccountsList() {
        screenModel.update { AccountsListModel() }
    }

    fun loadMore() {
        val nextPage = screenModel.value.page.toInt().inc().toString()
        screenModel.update { it.copy(page = nextPage) }
        getAccounts()
    }

    private fun getAccounts(model: AccountsListModel): MerchantAccountSummaryPaged {
        val page: Int = model.page.toInt()
        val pageSize: Int = model.pageSize.toInt()
        return ReportingService
            .findAccounts(page, pageSize)
            .orderBy(model.orderBy, model.order)
            .where(SearchCriteria.StartDate, model.fromTimeCreated)
            .and(SearchCriteria.EndDate, model.toTimeCreated)
            .and(SearchCriteria.AccountStatus, model.status)
            .and(SearchCriteria.AccountName, model.name)
            .and(SearchCriteria.ResourceId, model.id)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

}

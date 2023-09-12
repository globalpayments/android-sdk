package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.deposits.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.enums.DepositSortProperty
import com.global.api.entities.enums.DepositStatus
import com.global.api.entities.enums.SortDirection
import com.global.api.entities.reporting.DepositSummaryPaged
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

class DepositsListViewModel : ViewModel() {

    val screenModel = MutableStateFlow(DepositsListModel())

    fun updatePage(value: String) = screenModel.update { it.copy(page = value) }
    fun updatePageSize(value: String) = screenModel.update { it.copy(pageSize = value) }
    fun updateOrder(value: SortDirection?) = screenModel.update { it.copy(order = value) }
    fun updateOrderBy(value: DepositSortProperty?) = screenModel.update { it.copy(orderBy = value) }
    fun updateId(value: String) = screenModel.update { it.copy(id = value) }
    fun updateAmount(value: String) = screenModel.update { it.copy(amount = value) }
    fun updateFromTimeCreated(value: Date) = screenModel.update { it.copy(fromTimeCreated = value) }
    fun updateToTimeCreated(value: Date) = screenModel.update { it.copy(toTimeCreated = value) }
    fun updateMerchantID(value: String) = screenModel.update { it.copy(systemMID = value) }
    fun updateSystemHierarchy(value: String) = screenModel.update { it.copy(systemHierarchy = value) }
    fun updateStatus(value: DepositStatus?) = screenModel.update { it.copy(status = value) }
    fun updateMaskedAccountNumberLast4(value: String) = screenModel.update { it.copy(maskedAccountNumberLast4 = value) }

    fun getDeposits() {
        val model = screenModel.value
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getDeposits(model)
                val sampleResponse = result.results.map {
                    GPSampleResponseModel(
                        it.depositId,
                        listOf(
                            "Time created" to it.depositDate.toString(),
                            "Status" to it.status,
                            "Type" to it.type
                        )
                    )
                }
                val response = result.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(DepositSummaryPaged::class.java.simpleName, response)
                screenModel.update { it.copy(responses = sampleResponse, gpSnippetResponseModel = gpSnippetResponseModel) }
            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(DepositSummaryPaged::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    fun resetListTransaction() {
        screenModel.update { DepositsListModel() }
    }

    fun loadMore() {
        val nextPage = screenModel.value.page.toInt().inc().toString()
        screenModel.update { it.copy(page = nextPage) }
        getDeposits()
    }

    private fun getDeposits(model: DepositsListModel): DepositSummaryPaged {
        val page: Int = model.page.toInt()
        val pageSize: Int = model.pageSize.toInt()

        val reportBuilder = ReportingService.findDepositsPaged(page, pageSize)
        val searchBuilder = reportBuilder.searchBuilder

        reportBuilder.depositOrderBy = model.orderBy
        reportBuilder.order = model.order
        searchBuilder.startDate = model.fromTimeCreated
        searchBuilder.endDate = model.toTimeCreated
        searchBuilder.depositReference = model.id
        searchBuilder.depositStatus = model.status
        searchBuilder.amount = model.amount.toBigDecimalOrNull()
        searchBuilder.accountNumberLastFour = model.maskedAccountNumberLast4
        searchBuilder.merchantId = model.systemMID
        searchBuilder.systemHierarchy = model.systemHierarchy

        return reportBuilder.execute(Constants.DEFAULT_GPAPI_CONFIG)
    }
}

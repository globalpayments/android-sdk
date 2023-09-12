package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.paymentmethods.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.enums.SortDirection
import com.global.api.entities.enums.StoredPaymentMethodStatus
import com.global.api.entities.enums.TransactionSortProperty
import com.global.api.entities.reporting.DataServiceCriteria
import com.global.api.entities.reporting.SearchCriteria
import com.global.api.entities.reporting.StoredPaymentMethodSummary
import com.global.api.entities.reporting.StoredPaymentMethodSummaryPaged
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

class PaymentMethodsListViewModel : ViewModel() {

    val screenModel = MutableStateFlow(PaymentMethodsListModel())

    fun updatePage(value: String) = screenModel.update { it.copy(page = value) }
    fun updatePageSize(value: String) = screenModel.update { it.copy(pageSize = value) }
    fun updateOrder(value: SortDirection?) = screenModel.update { it.copy(order = value) }
    fun updateOrderBy(value: TransactionSortProperty?) = screenModel.update { it.copy(orderBy = value) }
    fun updateId(value: String) = screenModel.update { it.copy(id = value) }
    fun updateStartDate(value: Date) = screenModel.update { it.copy(startDate = value) }
    fun updateEndDate(value: Date) = screenModel.update { it.copy(endDate = value) }
    fun updateReferenceNumber(value: String) = screenModel.update { it.copy(referenceNumber = value) }
    fun updateStartLastUpdatedDate(value: Date) = screenModel.update { it.copy(startLastUpdatedDate = value) }
    fun updateEndLastUpdatedDate(value: Date) = screenModel.update { it.copy(endLastUpdatedDate = value) }
    fun updateStatus(value: StoredPaymentMethodStatus?) = screenModel.update { it.copy(status = value) }

    fun getPaymentMethods() {
        val model = screenModel.value
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getPaymentMethods(model)
                val sampleResponse = result.results.map {
                    GPSampleResponseModel(
                        it.id,
                        listOf(
                            "Time created" to it.timeCreated.toString(),
                            "Status" to it.status,
                            "Reference" to it.reference
                        )
                    )
                }
                val response = result.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(StoredPaymentMethodSummaryPaged::class.java.simpleName, response, false)
                screenModel.update { it.copy(responses = sampleResponse, gpSnippetResponseModel = gpSnippetResponseModel) }
            } catch (exception: Exception) {
                val gpSnippetResponseModel = GPSnippetResponseModel(
                    StoredPaymentMethodSummary::class.java.simpleName,
                    listOf("Exception" to (exception.message ?: "Error occurred")),
                    true
                )
                screenModel.update {
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    fun resetListTransaction() {
        screenModel.update { PaymentMethodsListModel() }
    }

    fun loadMore() {
        val nextPage = screenModel.value.page.toInt().inc().toString()
        screenModel.update { it.copy(page = nextPage) }
        getPaymentMethods()
    }

    private fun getPaymentMethods(model: PaymentMethodsListModel): StoredPaymentMethodSummaryPaged {
        val page: Int = model.page.toInt()
        val pageSize: Int = model.pageSize.toInt()

        val reportBuilder = ReportingService.findStoredPaymentMethodsPaged(page, pageSize)
        reportBuilder.order = model.order
        reportBuilder.transactionOrderBy = model.orderBy
        reportBuilder.withStoredPaymentMethodId(model.id)
        reportBuilder.where(SearchCriteria.ReferenceNumber, model.referenceNumber)
        reportBuilder.where<StoredPaymentMethodStatus>(SearchCriteria.StoredPaymentMethodStatus, model.status)
        reportBuilder.where<Date>(SearchCriteria.StartDate, model.startDate)
            .and<Date>(SearchCriteria.EndDate, model.endDate)
        reportBuilder.where<Date>(DataServiceCriteria.StartLastUpdatedDate, model.startLastUpdatedDate)
            .and<Date>(DataServiceCriteria.EndLastUpdatedDate, model.endLastUpdatedDate)

        return reportBuilder.execute(Constants.DEFAULT_GPAPI_CONFIG)
    }
}

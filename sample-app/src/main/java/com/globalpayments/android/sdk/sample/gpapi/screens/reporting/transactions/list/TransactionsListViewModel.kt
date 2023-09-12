package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.transactions.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.enums.Channel
import com.global.api.entities.enums.DepositStatus
import com.global.api.entities.enums.PaymentEntryMode
import com.global.api.entities.enums.PaymentType
import com.global.api.entities.enums.SortDirection
import com.global.api.entities.enums.TransactionSortProperty
import com.global.api.entities.enums.TransactionStatus
import com.global.api.entities.reporting.DataServiceCriteria
import com.global.api.entities.reporting.SearchCriteria
import com.global.api.entities.reporting.TransactionSummaryPaged
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

class TransactionsListViewModel : ViewModel() {

    val screenModel = MutableStateFlow(TransactionsListModel())

    fun updateGetFromSettlements(value: Boolean) = screenModel.update { it.copy(getFromSettlements = value) }
    fun updatePage(value: String) = screenModel.update { it.copy(page = value) }
    fun updatePageSize(value: String) = screenModel.update { it.copy(pageSize = value) }
    fun updateOrder(value: SortDirection?) = screenModel.update { it.copy(order = value) }
    fun updateOrderBy(value: TransactionSortProperty?) = screenModel.update { it.copy(orderBy = value) }
    fun updateId(value: String) = screenModel.update { it.copy(id = value) }
    fun updateType(value: PaymentType?) = screenModel.update { it.copy(type = value) }
    fun updateChannel(value: Channel?) = screenModel.update { it.copy(channel = value) }
    fun updateAmount(value: String) = screenModel.update { it.copy(amount = value) }
    fun updateCurrency(value: String) = screenModel.update { it.copy(currency = value) }
    fun updateNumberFirst6(value: String) = screenModel.update { it.copy(numberFirst6 = value) }
    fun updateNumberLast4(value: String) = screenModel.update { it.copy(numberLast4 = value) }
    fun updateTokenFirst6(value: String) = screenModel.update { it.copy(tokenFirst6 = value) }
    fun updateTokenLast4(value: String) = screenModel.update { it.copy(tokenLast4 = value) }
    fun updateAccountName(value: String) = screenModel.update { it.copy(accountName = value) }
    fun updateBrand(value: String) = screenModel.update { it.copy(brand = value) }
    fun updateBrandReference(value: String) = screenModel.update { it.copy(brandReference = value) }
    fun updateAuthCode(value: String) = screenModel.update { it.copy(authCode = value) }
    fun updateReference(value: String) = screenModel.update { it.copy(reference = value) }
    fun updateStatus(value: TransactionStatus?) = screenModel.update { it.copy(status = value) }
    fun updateFromTimeCreated(value: Date) = screenModel.update { it.copy(fromTimeCreated = value) }
    fun updateToTimeCreated(value: Date) = screenModel.update { it.copy(toTimeCreated = value) }
    fun updateCountry(value: String) = screenModel.update { it.copy(country = value) }
    fun updateBatchId(value: String) = screenModel.update { it.copy(batchId = value) }
    fun updateEntryMode(value: PaymentEntryMode?) = screenModel.update { it.copy(entryMode = value) }
    fun updateName(value: String) = screenModel.update { it.copy(name = value) }
    fun updateFromDepositTimeCreated(date: Date?) = screenModel.update { it.copy(fromDepositTimeCreated = date) }
    fun updateToDepositTimeCreated(date: Date?) = screenModel.update { it.copy(toDepositTimeCreated = date) }
    fun updateFromBatchTimeCreated(date: Date?) = screenModel.update { it.copy(fromBatchTimeCreated = date) }
    fun updateToBatchTimeCreated(date: Date?) = screenModel.update { it.copy(toBatchTimeCreated = date) }
    fun updateMerchantID(value: String) = screenModel.update { it.copy(merchantID = value) }
    fun updateSystemHierarchy(value: String) = screenModel.update { it.copy(systemHierarchy = value) }
    fun updateDepositStatus(value: DepositStatus?) = screenModel.update { it.copy(depositStatus = value) }
    fun updateArn(value: String) = screenModel.update { it.copy(arn = value) }
    fun updateDepositId(value: String) = screenModel.update { it.copy(depositId = value) }
    fun getTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            val model = screenModel.value
            try {
                val result = getTransactions(model)
                val sampleResponse = result.results.map {
                    if (model.getFromSettlements) {
                        GPSampleResponseModel(
                            if (it.depositReference.isNullOrBlank()) it.transactionId else it.depositReference,
                            listOf(
                                "Time created" to it.depositDate.toString(),
                                "Status" to (it.transactionStatus.takeIf(String::isNotBlank) ?: "-"),
                                "Type" to it.transactionType
                            )
                        )
                    } else {
                        GPSampleResponseModel(
                            it.transactionId,
                            listOf(
                                "Time created" to it.transactionDate.toString(),
                                "Status" to it.transactionStatus,
                                "Type" to it.transactionType
                            )
                        )
                    }
                }
                val response = result.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(TransactionSummaryPaged::class.java.simpleName, response)
                screenModel.update { it.copy(responses = sampleResponse, gpSnippetResponseModel = gpSnippetResponseModel) }

            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(TransactionSummaryPaged::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    fun resetListTransaction() {
        screenModel.update { TransactionsListModel() }
    }

    fun loadMore() {
        val nextPage = screenModel.value.page.toInt().inc().toString()
        screenModel.update { it.copy(page = nextPage) }
        getTransactions()
    }

    private fun getTransactions(model: TransactionsListModel): TransactionSummaryPaged {
        val reportBuilder = if (model.getFromSettlements)
            ReportingService.findSettlementTransactionsPaged(
                model.page.toIntOrNull() ?: 1,
                model.pageSize.toIntOrNull() ?: 5
            ) else
            ReportingService.findTransactionsPaged(
                model.page.toIntOrNull() ?: 1,
                model.pageSize.toIntOrNull() ?: 5
            )
        reportBuilder.orderBy(model.orderBy, model.order)
        reportBuilder.withTransactionId(model.id)
        reportBuilder.where(SearchCriteria.PaymentType, model.type)
        reportBuilder.where(SearchCriteria.Channel, model.channel)
        reportBuilder.where(DataServiceCriteria.Amount, model.amount.toBigDecimalOrNull())
        reportBuilder.where(DataServiceCriteria.Currency, model.currency)
        reportBuilder.where(SearchCriteria.CardNumberFirstSix, model.numberFirst6)
            .and(SearchCriteria.CardNumberLastFour, model.numberLast4)
        reportBuilder.where(SearchCriteria.TokenFirstSix, model.tokenFirst6)
            .and(SearchCriteria.TokenLastFour, model.tokenLast4)
        reportBuilder.where(SearchCriteria.AccountName, model.accountName)
        reportBuilder.where(SearchCriteria.CardBrand, model.brand)
        reportBuilder.where(SearchCriteria.BrandReference, model.brandReference)
        reportBuilder.where(SearchCriteria.AuthCode, model.authCode)
        reportBuilder.where(SearchCriteria.ReferenceNumber, model.reference)
        reportBuilder.where(SearchCriteria.TransactionStatus, model.status)
        reportBuilder.where<Date>(SearchCriteria.StartDate, model.fromTimeCreated)
            .and<Date>(SearchCriteria.EndDate, model.toTimeCreated)
        reportBuilder.where(DataServiceCriteria.Country, model.currency)
        reportBuilder.where(SearchCriteria.BatchId, model.batchId)
        reportBuilder.where(SearchCriteria.PaymentEntryMode, model.entryMode)
        reportBuilder.where(SearchCriteria.Name, model.name)
        reportBuilder.where<DepositStatus>(SearchCriteria.DepositStatus, model.depositStatus)
        reportBuilder.where(SearchCriteria.AquirerReferenceNumber, model.arn)
        reportBuilder.withDepositReference(model.depositId)
        reportBuilder.where<Date>(DataServiceCriteria.StartDepositDate, model.fromDepositTimeCreated)
            .and<Date>(DataServiceCriteria.EndDepositDate, model.toDepositTimeCreated)
        reportBuilder.where<Date>(DataServiceCriteria.StartBatchDate, model.fromBatchTimeCreated)
            .and<Date>(DataServiceCriteria.EndBatchDate, model.toBatchTimeCreated)
        reportBuilder.where(DataServiceCriteria.MerchantId, model.merchantID)
            .and(DataServiceCriteria.SystemHierarchy, model.systemHierarchy)

        return reportBuilder.execute(Constants.DEFAULT_GPAPI_CONFIG)
    }
}

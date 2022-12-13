package com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.global.api.entities.reporting.ActionSummary
import com.global.api.entities.reporting.SearchCriteria
import com.global.api.services.ReportingService
import com.globalpayments.android.sdk.TaskExecutor
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.common.base.BaseAndroidViewModel
import com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport.model.ActionsReportParametersModel

class ActionsReportViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val actionsLiveData = MutableLiveData<List<ActionSummary>>()

    fun getActionsLiveData(): LiveData<List<ActionSummary>> {
        return actionsLiveData
    }

    private var currentActionsParams: ActionsReportParametersModel? = null
    private var pageSize = 0
    private var currentPage = 1
    private var totalRecordCount = -1

    private fun resetPagination() {
        pageSize = 0
        currentPage = 1
        totalRecordCount = -1
        currentActionsParams = null
    }


    fun getActionsList(actionsReportParametersModel: ActionsReportParametersModel) {
        resetPagination()
        currentActionsParams = actionsReportParametersModel
        pageSize = actionsReportParametersModel.pageSize
        getActionsList()
    }

    fun loadMore() {
        val hasMore = pageSize * currentPage < totalRecordCount
        if (!hasMore || progressStatus.value == true) return
        currentPage += 1
        currentActionsParams?.page = currentPage
        getActionsList()
    }

    private fun getActionsList() {
        val actionsReportParametersModel = currentActionsParams ?: return
        showProgress()
        TaskExecutor.executeAsync<List<ActionSummary>>(object :
            TaskExecutor.Task<List<ActionSummary>?> {
            @Throws(Exception::class)
            override fun executeAsync(): List<ActionSummary> {
                return executeGetActionsListRequest(actionsReportParametersModel)
            }

            override fun onSuccess(value: List<ActionSummary>?) {
                showResult(value)
            }

            override fun onError(exception: Exception) {
                showError(exception)
            }
        })
    }

    fun getActionsById(actionId: String) {
        showProgress()
        resetPagination()
        TaskExecutor.executeAsync(object : TaskExecutor.Task<ActionSummary?> {
            @Throws(Exception::class)
            override fun executeAsync(): ActionSummary {
                return executeGetActionByIdRequest(actionId)
            }

            override fun onSuccess(value: ActionSummary?) {
                showResult(listOf(value))
            }

            override fun onError(exception: Exception) {
                showError(exception)
            }
        })
    }

    private fun showResult(actionSummaries: List<ActionSummary?>?) {
        if (actionSummaries == null || actionSummaries.isEmpty()) {
            showError(Exception("Empty Actions List"))
        } else {
            hideProgress()
            actionsLiveData.postValue(actionSummaries.filterNotNull())
        }
    }

    @Throws(Exception::class)
    private fun executeGetActionsListRequest(
        parametersModel: ActionsReportParametersModel
    ): List<ActionSummary> {
        val page = parametersModel.page
        val pageSize = parametersModel.pageSize
        val reportResult = ReportingService.findActionsPaged(page, pageSize)
            .orderBy(parametersModel.orderBy, parametersModel.order)
            .where(SearchCriteria.ActionType, parametersModel.type)
            .and(SearchCriteria.ActionId, parametersModel.id)
            .and(SearchCriteria.Resource, parametersModel.resource)
            .and(SearchCriteria.ResourceStatus, parametersModel.resourceStatus)
            .and(SearchCriteria.ResourceId, parametersModel.resourceId)
            .and(SearchCriteria.StartDate, parametersModel.fromTimeCreated)
            .and(SearchCriteria.EndDate, parametersModel.toTimeCreated)
            .and(SearchCriteria.AccountName, parametersModel.accountName)
            .and(SearchCriteria.MerchantName, parametersModel.merchantName)
            .and(SearchCriteria.AppName, parametersModel.appName)
            .and(SearchCriteria.Version, parametersModel.version)
            .and(SearchCriteria.ResponseCode, parametersModel.responseCode)
            .and(SearchCriteria.HttpResponseCode, parametersModel.responseHttpCode)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
        if (currentPage == 1) {
            totalRecordCount = reportResult.totalRecordCount
        }
        return reportResult.results
    }

    @Throws(Exception::class)
    private fun executeGetActionByIdRequest(
        actionId: String
    ): ActionSummary {
        return ReportingService.actionDetail(actionId)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }
}

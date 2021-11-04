package com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport;


import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.builders.TransactionReportBuilder;
import com.global.api.entities.reporting.ActionSummary;
import com.global.api.entities.reporting.ActionSummaryPaged;
import com.global.api.entities.reporting.SearchCriteria;
import com.global.api.services.ReportingService;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.common.base.BaseAndroidViewModel;
import com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport.model.ActionsReportParametersModel;

import java.util.Collections;
import java.util.List;

public class ActionsReportViewModel extends BaseAndroidViewModel {

    private final MutableLiveData<List<ActionSummary>> actionsLiveData = new MutableLiveData<>();

    public ActionsReportViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<ActionSummary>> getActionsLiveData() {
        return actionsLiveData;
    }

    public void getActionsList(ActionsReportParametersModel actionsReportParametersModel) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<List<ActionSummary>>() {
            @Override
            public List<ActionSummary> executeAsync() throws Exception {
                return executeGetActionsListRequest(actionsReportParametersModel);
            }

            @Override
            public void onSuccess(List<ActionSummary> value) {
                showResult(value);
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    public void getActionsById(String actionId) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<ActionSummary>() {
            @Override
            public ActionSummary executeAsync() throws Exception {
                return executeGetActionByIdRequest(actionId);
            }

            @Override
            public void onSuccess(ActionSummary value) {
                showResult(Collections.singletonList(value));
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    private void showResult(List<ActionSummary> actionSummaries) {
        if (actionSummaries == null || actionSummaries.isEmpty()) {
            showError(new Exception("Empty Actions List"));
        } else {
            hideProgress();
            actionsLiveData.setValue(actionSummaries);
        }
    }

    private List<ActionSummary> executeGetActionsListRequest(
            ActionsReportParametersModel parametersModel) throws Exception {
        int page = parametersModel.getPage();
        int pageSize = parametersModel.getPageSize();
        ActionSummaryPaged result =
                ReportingService.findActionsPaged(page, pageSize)
                        .where(SearchCriteria.ActionType, parametersModel.getType())
                        .and(SearchCriteria.Resource, parametersModel.getResource())
                        .and(SearchCriteria.ResourceStatus, parametersModel.getResourceStatus())
                        .and(SearchCriteria.AccountName, parametersModel.getAccountName())
                        .and(SearchCriteria.MerchantName, parametersModel.getMerchantName())
                        .and(SearchCriteria.Version, parametersModel.getVersion())
                        .and(SearchCriteria.StartDate, parametersModel.getFromTimeCreated())
                        .and(SearchCriteria.EndDate, parametersModel.getToTimeCreated())
                        .execute(DEFAULT_GPAPI_CONFIG);

        return result.getResults();
    }

    private ActionSummary executeGetActionByIdRequest(String actionId) throws Exception {
        return ReportingService.actionDetail(actionId)
        .execute(DEFAULT_GPAPI_CONFIG);
    }
}
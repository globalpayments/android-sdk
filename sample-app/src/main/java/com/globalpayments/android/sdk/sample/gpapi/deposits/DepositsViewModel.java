package com.globalpayments.android.sdk.sample.gpapi.deposits;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.builders.TransactionReportBuilder;
import com.global.api.entities.reporting.DepositSummary;
import com.global.api.entities.reporting.DepositSummaryList;
import com.global.api.entities.reporting.SearchCriteriaBuilder;
import com.global.api.services.ReportingService;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel;
import com.globalpayments.android.sdk.sample.gpapi.deposits.model.DepositParametersModel;

import java.util.Collections;
import java.util.List;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;

public class DepositsViewModel extends BaseViewModel {
    private final MutableLiveData<List<DepositSummary>> depositSummaryListLiveData = new MutableLiveData<>();

    public LiveData<List<DepositSummary>> getDepositSummaryListLiveData() {
        return depositSummaryListLiveData;
    }

    public void getDepositsList(DepositParametersModel depositParametersModel) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<List<DepositSummary>>() {
            @Override
            public List<DepositSummary> executeAsync() throws Exception {
                return executeGetDepositsListRequest(depositParametersModel);
            }

            @Override
            public void onSuccess(List<DepositSummary> value) {
                showResult(value);
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    public void getDepositById(String depositId) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<DepositSummary>() {
            @Override
            public DepositSummary executeAsync() throws Exception {
                return executeGetDepositByIdRequest(depositId);
            }

            @Override
            public void onSuccess(DepositSummary value) {
                showResult(Collections.singletonList(value));
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    private void showResult(List<DepositSummary> deposits) {
        if (deposits == null || deposits.isEmpty()) {
            showError(new Exception("Empty Deposits List"));
        } else {
            hideProgress();
            depositSummaryListLiveData.setValue(deposits);
        }
    }

    private DepositSummary executeGetDepositByIdRequest(String depositId) throws Exception {
        return ReportingService
                .depositDetail(depositId)
                .execute(DEFAULT_GPAPI_CONFIG);
    }

    private List<DepositSummary> executeGetDepositsListRequest(DepositParametersModel parametersModel) throws Exception {
        TransactionReportBuilder<DepositSummaryList> reportBuilder = ReportingService.findDeposits();
        SearchCriteriaBuilder<DepositSummaryList> searchBuilder = reportBuilder.getSearchBuilder();

        reportBuilder.setPage(parametersModel.getPage());
        reportBuilder.setPageSize(parametersModel.getPageSize());
        reportBuilder.setDepositOrderBy(parametersModel.getOrderBy());
        reportBuilder.setDepositOrder(parametersModel.getOrder());

        searchBuilder.setStartDate(parametersModel.getFromTimeCreated());
        searchBuilder.setEndDate(parametersModel.getToTimeCreated());
        searchBuilder.setDepositId(parametersModel.getId());
        searchBuilder.setDepositStatus(parametersModel.getStatus());
        searchBuilder.setAmount(parametersModel.getAmount());
        searchBuilder.setAccountNumberLastFour(parametersModel.getMaskedAccountNumberLast4());
        searchBuilder.setMerchantId(parametersModel.getSystemMID());
        searchBuilder.setSystemHierarchy(parametersModel.getSystemHierarchy());

        return reportBuilder.execute(DEFAULT_GPAPI_CONFIG);
    }
}

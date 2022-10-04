package com.globalpayments.android.sdk.sample.gpapi.deposits;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.builders.TransactionReportBuilder;
import com.global.api.entities.reporting.DepositSummary;
import com.global.api.entities.reporting.DepositSummaryPaged;
import com.global.api.entities.reporting.SearchCriteriaBuilder;
import com.global.api.services.ReportingService;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel;
import com.globalpayments.android.sdk.sample.gpapi.deposits.model.DepositParametersModel;

import java.util.Collections;
import java.util.List;

public class DepositsViewModel extends BaseViewModel {
    private final MutableLiveData<List<DepositSummary>> depositSummaryListLiveData = new MutableLiveData<>();

    private DepositParametersModel currentReportParameters;
    private int pageSize = 0;
    private int currentPage = 1;
    private int totalRecordCount = -1;

    public LiveData<List<DepositSummary>> getDepositSummaryListLiveData() {
        return depositSummaryListLiveData;
    }

    public void getDepositsList(DepositParametersModel depositParametersModel) {
        resetPagination();
        currentReportParameters = depositParametersModel;
        pageSize = depositParametersModel.getPageSize();
        getDepositsList();
    }

    public void loadMore() {
        boolean hasMore = pageSize * currentPage < totalRecordCount;
        if (!hasMore || Boolean.TRUE.equals(getProgressStatus().getValue())) return;
        currentPage += 1;
        currentReportParameters.setPage(currentPage);
        getDepositsList();
    }

    private void resetPagination() {
        currentPage = 1;
        totalRecordCount = -1;
        pageSize = 5;
        currentReportParameters = null;
    }

    private void getDepositsList() {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<List<DepositSummary>>() {
            @Override
            public List<DepositSummary> executeAsync() throws Exception {
                return executeGetDepositsListRequest(currentReportParameters);
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
        resetPagination();
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
        int page = parametersModel.getPage();
        int pageSize = parametersModel.getPageSize();

        TransactionReportBuilder<DepositSummaryPaged> reportBuilder = ReportingService.findDepositsPaged(page, pageSize);
        SearchCriteriaBuilder<DepositSummaryPaged> searchBuilder = reportBuilder.getSearchBuilder();

        reportBuilder.setDepositOrderBy(parametersModel.getOrderBy());
        reportBuilder.setOrder(parametersModel.getOrder());
        searchBuilder.setStartDate(parametersModel.getFromTimeCreated());
        searchBuilder.setEndDate(parametersModel.getToTimeCreated());
        searchBuilder.setDepositReference(parametersModel.getId());
        searchBuilder.setDepositStatus(parametersModel.getStatus());
        searchBuilder.setAmount(parametersModel.getAmount());
        searchBuilder.setAccountNumberLastFour(parametersModel.getMaskedAccountNumberLast4());
        searchBuilder.setMerchantId(parametersModel.getSystemMID());
        searchBuilder.setSystemHierarchy(parametersModel.getSystemHierarchy());
        DepositSummaryPaged result = reportBuilder.execute(DEFAULT_GPAPI_CONFIG);
        if (currentPage == 1) {
            totalRecordCount = result.totalRecordCount;
        }
        return result.getResults();
    }
}

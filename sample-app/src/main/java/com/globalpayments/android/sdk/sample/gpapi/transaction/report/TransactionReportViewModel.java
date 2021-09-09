package com.globalpayments.android.sdk.sample.gpapi.transaction.report;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.builders.TransactionReportBuilder;
import com.global.api.entities.TransactionSummary;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.reporting.TransactionSummaryPaged;
import com.global.api.services.ReportingService;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel;
import com.globalpayments.android.sdk.sample.gpapi.transaction.report.model.TransactionReportParameters;

import java.util.Collections;
import java.util.List;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;

public class TransactionReportViewModel extends BaseViewModel {

    private MutableLiveData<List<TransactionSummary>> transactionsLiveData = new MutableLiveData<>();

    public LiveData<List<TransactionSummary>> getTransactionsLiveData() {
        return transactionsLiveData;
    }

    public void getTransactionList(TransactionReportParameters transactionReportParameters) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<List<TransactionSummary>>() {
            @Override
            public List<TransactionSummary> executeAsync() throws Exception {
                return executeGetTransactionListRequest(transactionReportParameters);
            }

            @Override
            public void onSuccess(List<TransactionSummary> value) {
                showResult(value);
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    public void getTransactionById(String transactionId) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<TransactionSummary>() {
            @Override
            public TransactionSummary executeAsync() throws Exception {
                return executeGetTransactionByIdRequest(transactionId);
            }

            @Override
            public void onSuccess(TransactionSummary value) {
                showResult(Collections.singletonList(value));
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    private void showResult(List<TransactionSummary> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            showError(new Exception("Empty Transaction Report List"));
        } else {
            hideProgress();
            transactionsLiveData.setValue(transactions);
        }
    }

    private TransactionSummary executeGetTransactionByIdRequest(String transactionId) throws ApiException {
        return ReportingService.transactionDetail(transactionId).execute(DEFAULT_GPAPI_CONFIG);
    }

    private List<TransactionSummary> executeGetTransactionListRequest(
            TransactionReportParameters reportParameters) throws ApiException {
        return getTransactionReportBuilder(reportParameters).execute(DEFAULT_GPAPI_CONFIG).getResults();
    }

    private TransactionReportBuilder<TransactionSummaryPaged> getTransactionReportBuilder(
            TransactionReportParameters reportParameters) {
        return reportParameters.isFromSettlements()
                ? ReportingService.findSettlementTransactionsPaged(reportParameters.getPage(), reportParameters.getPageSize())
                : ReportingService.findTransactionsPaged(reportParameters.getPage(), reportParameters.getPageSize());
    }

}
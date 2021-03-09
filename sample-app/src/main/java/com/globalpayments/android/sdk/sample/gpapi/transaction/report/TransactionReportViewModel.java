package com.globalpayments.android.sdk.sample.gpapi.transaction.report;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.builders.TransactionReportBuilder;
import com.global.api.entities.TransactionSummary;
import com.global.api.entities.TransactionSummaryList;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.reporting.SearchCriteriaBuilder;
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
        return ReportingService
                .transactionDetail(transactionId)
                .execute(DEFAULT_GPAPI_CONFIG);
    }

    private List<TransactionSummary> executeGetTransactionListRequest(TransactionReportParameters reportParameters)
            throws ApiException {
        return getTransactionReportBuilder(reportParameters)
                .execute(DEFAULT_GPAPI_CONFIG);
    }

    private TransactionReportBuilder<TransactionSummaryList> getTransactionReportBuilder(
            TransactionReportParameters reportParameters) {

        TransactionReportBuilder<TransactionSummaryList> transactionReportBuilder = reportParameters.isFromSettlements()
                ? ReportingService.findSettlementTransactions()
                : ReportingService.findTransactions();

        SearchCriteriaBuilder<TransactionSummaryList> searchBuilder = transactionReportBuilder.getSearchBuilder();

        transactionReportBuilder.setPage(reportParameters.getPage());
        transactionReportBuilder.setPageSize(reportParameters.getPageSize());
        transactionReportBuilder.setTransactionOrder(reportParameters.getOrder());
        transactionReportBuilder.setTransactionOrderBy(reportParameters.getOrderBy());

        searchBuilder.setCardNumberFirstSix(reportParameters.getNumberFirst6());
        searchBuilder.setCardNumberLastFour(reportParameters.getNumberLast4());
        searchBuilder.setCardBrand(reportParameters.getBrand());
        searchBuilder.setBrandReference(reportParameters.getBrandReference());
        searchBuilder.setAuthCode(reportParameters.getAuthCode());
        searchBuilder.setReferenceNumber(reportParameters.getReference());
        searchBuilder.setTransactionStatus(reportParameters.getStatus());
        searchBuilder.setStartDate(reportParameters.getFromTimeCreated());
        searchBuilder.setEndDate(reportParameters.getToTimeCreated());

        if (reportParameters.isFromSettlements()) {
            searchBuilder.setDepositStatus(reportParameters.getDepositStatus());
            searchBuilder.setAquirerReferenceNumber(reportParameters.getArn());
            searchBuilder.setDepositId(reportParameters.getDepositId());
            searchBuilder.setStartDepositDate(reportParameters.getFromDepositTimeCreated());
            searchBuilder.setEndDepositDate(reportParameters.getToDepositTimeCreated());
            searchBuilder.setStartBatchDate(reportParameters.getFromBatchTimeCreated());
            searchBuilder.setEndBatchDate(reportParameters.getToBatchTimeCreated());
            searchBuilder.setMerchantId(reportParameters.getSystemMID());
            searchBuilder.setSystemHierarchy(reportParameters.getSystemHierarchy());
        } else {
            transactionReportBuilder.setTransactionId(reportParameters.getId());

            searchBuilder.setPaymentType(reportParameters.getType());
            searchBuilder.setChannel(reportParameters.getChannel());
            searchBuilder.setAmount(reportParameters.getAmount());
            searchBuilder.setCurrency(reportParameters.getCurrency());
            searchBuilder.setTokenFirstSix(reportParameters.getTokenFirst6());
            searchBuilder.setTokenLastFour(reportParameters.getTokenLast4());
            searchBuilder.setAccountName(reportParameters.getAccountName());
            searchBuilder.setCountry(reportParameters.getCountry());
            searchBuilder.setBatchId(reportParameters.getBatchId());
            searchBuilder.setPaymentEntryMode(reportParameters.getEntryMode());
            searchBuilder.setName(reportParameters.getName());
        }

        return transactionReportBuilder;
    }
}
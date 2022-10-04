package com.globalpayments.android.sdk.sample.gpapi.transaction.report;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.builders.TransactionReportBuilder;
import com.global.api.entities.TransactionSummary;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.reporting.DataServiceCriteria;
import com.global.api.entities.reporting.SearchCriteria;
import com.global.api.entities.reporting.TransactionSummaryPaged;
import com.global.api.services.ReportingService;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel;
import com.globalpayments.android.sdk.sample.gpapi.transaction.report.model.TransactionReportParameters;

import java.util.Collections;
import java.util.List;

public class TransactionReportViewModel extends BaseViewModel {

    private final MutableLiveData<List<TransactionSummary>> transactionsLiveData = new MutableLiveData<>();

    private TransactionReportParameters currentReportParameters;
    private int pageSize = 0;
    private int currentPage = 1;
    private int totalRecordCount = -1;

    public LiveData<List<TransactionSummary>> getTransactionsLiveData() {
        return transactionsLiveData;
    }

    public void getTransactionList(TransactionReportParameters transactionReportParameters) {
        resetPagination();
        currentReportParameters = transactionReportParameters;
        pageSize = transactionReportParameters.getPageSize();
        getTransactionList();
    }

    public void loadMore() {
        boolean hasMore = pageSize * currentPage < totalRecordCount;
        if (!hasMore || Boolean.TRUE.equals(getProgressStatus().getValue())) return;
        currentPage += 1;
        currentReportParameters.setPage(currentPage);
        getTransactionList();
    }

    private void resetPagination() {
        pageSize = 0;
        currentPage = 1;
        totalRecordCount = -1;
        currentReportParameters = null;
    }

    private void getTransactionList() {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<List<TransactionSummary>>() {
            @Override
            public List<TransactionSummary> executeAsync() throws Exception {
                return executeGetTransactionListRequest(currentReportParameters);
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
        resetPagination();
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
        TransactionReportBuilder<TransactionSummaryPaged> reportBuilder = reportParameters.isFromSettlements()
                ? ReportingService.findSettlementTransactionsPaged(reportParameters.getPage(), reportParameters.getPageSize())
                : ReportingService.findTransactionsPaged(reportParameters.getPage(), reportParameters.getPageSize());
        reportBuilder.orderBy(reportParameters.getOrderBy(), reportParameters.getOrder());
        reportBuilder.withTransactionId(reportParameters.getId());
        reportBuilder.where(SearchCriteria.PaymentType, reportParameters.getType());
        reportBuilder.where(SearchCriteria.Channel, reportParameters.getChannel());
        reportBuilder.where(DataServiceCriteria.Amount, reportParameters.getAmount());
        reportBuilder.where(DataServiceCriteria.Currency, reportParameters.getCurrency());
        reportBuilder.where(SearchCriteria.CardNumberFirstSix, reportParameters.getNumberFirst6())
                .and(SearchCriteria.CardNumberLastFour, reportParameters.getNumberLast4());
        reportBuilder.where(SearchCriteria.TokenFirstSix, reportParameters.getTokenFirst6())
                .and(SearchCriteria.TokenLastFour, reportParameters.getTokenLast4());
        reportBuilder.where(SearchCriteria.AccountName, reportParameters.getAccountName());
        reportBuilder.where(SearchCriteria.CardBrand, reportParameters.getBrand());
        reportBuilder.where(SearchCriteria.BrandReference, reportParameters.getBrandReference());
        reportBuilder.where(SearchCriteria.AuthCode, reportParameters.getAuthCode());
        reportBuilder.where(SearchCriteria.ReferenceNumber, reportParameters.getDepositReference());
        reportBuilder.where(SearchCriteria.TransactionStatus, reportParameters.getStatus());
        reportBuilder.where(SearchCriteria.StartDate, reportParameters.getFromTimeCreated())
                .and(SearchCriteria.EndDate, reportParameters.getToTimeCreated());
        reportBuilder.where(DataServiceCriteria.Country, reportParameters.getCountry());
        reportBuilder.where(SearchCriteria.BatchId, reportParameters.getBatchId());
        reportBuilder.where(SearchCriteria.PaymentEntryMode, reportParameters.getEntryMode());
        reportBuilder.where(SearchCriteria.Name, reportParameters.getName());
        reportBuilder.where(SearchCriteria.DepositStatus, reportParameters.getDepositStatus());
        reportBuilder.where(SearchCriteria.AquirerReferenceNumber, reportParameters.getArn());
        reportBuilder.withDepositReference(reportParameters.getId());
        reportBuilder.where(DataServiceCriteria.StartDepositDate, reportParameters.getFromDepositTimeCreated())
                .and(DataServiceCriteria.EndDepositDate, reportParameters.getToDepositTimeCreated());
        reportBuilder.where(DataServiceCriteria.StartBatchDate, reportParameters.getFromBatchTimeCreated())
                .and(DataServiceCriteria.EndBatchDate, reportParameters.getToBatchTimeCreated());
        reportBuilder.where(DataServiceCriteria.MerchantId, reportParameters.getSystemMID())
                .and(DataServiceCriteria.SystemHierarchy, reportParameters.getSystemHierarchy());

        TransactionSummaryPaged result = reportBuilder.execute(DEFAULT_GPAPI_CONFIG);
        if (currentPage == 1) {
            totalRecordCount = result.totalRecordCount;
        }
        return result.getResults();
    }

}

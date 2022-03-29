package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.builders.TransactionReportBuilder;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.reporting.DataServiceCriteria;
import com.global.api.entities.reporting.SearchCriteria;
import com.global.api.entities.reporting.StoredPaymentMethodSummary;
import com.global.api.entities.reporting.StoredPaymentMethodSummaryPaged;
import com.global.api.paymentMethods.CreditCardData;
import com.global.api.services.ReportingService;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel;
import com.globalpayments.android.sdk.sample.gpapi.transaction.report.model.TransactionReportParameters;
import com.globalpayments.android.sdk.sample.gpapi.verifications.model.VerificationsModel;

import java.util.Collections;
import java.util.List;

public class PaymentMethodReportViewModel extends BaseViewModel {

    private final MutableLiveData<List<StoredPaymentMethodSummary>> paymentMethodsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<StoredPaymentMethodSummary>> paymentMethodsListLiveData = new MutableLiveData<>();

    public LiveData<List<StoredPaymentMethodSummary>> getPaymentMethodsLiveData() {
        return paymentMethodsLiveData;
    }

    public LiveData<List<StoredPaymentMethodSummary>> getPaymentMethodsListLiveData() {
        return paymentMethodsListLiveData;
    }

    public void getPaymentMethodList(TransactionReportParameters reportParameters) {
        showProgress();
        TaskExecutor.executeAsync(new TaskExecutor.Task<List<StoredPaymentMethodSummary>>() {
            @Override
            public List<StoredPaymentMethodSummary> executeAsync() throws Exception {
                return executeGetPaymentMethodsRequest(reportParameters);
            }

            @Override
            public void onSuccess(List<StoredPaymentMethodSummary> value) {
                showResultPaymentMethodsList(value);
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    public void getPaymentMethodById(String paymentMethodId) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<StoredPaymentMethodSummary>() {
            @Override
            public StoredPaymentMethodSummary executeAsync() throws Exception {
                return executeGetPaymentMethodByIdRequest(paymentMethodId);
            }

            @Override
            public void onSuccess(StoredPaymentMethodSummary value) {
                showResultPaymentMethodById(Collections.singletonList(value));
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    private void showResultPaymentMethodsList(List<StoredPaymentMethodSummary> storedPaymentMethodSummaryList) {
        if (storedPaymentMethodSummaryList == null || storedPaymentMethodSummaryList.isEmpty()) {
            showError(new Exception("Empty Payment Method Report List"));
        } else {
            hideProgress();
            paymentMethodsListLiveData.setValue(storedPaymentMethodSummaryList);
        }
    }

    private void showResultPaymentMethodById(List<StoredPaymentMethodSummary> paymentMethods) {
        if (paymentMethods == null || paymentMethods.isEmpty()) {
            showError(new Exception("Empty Payment Method Report List"));
        } else {
            hideProgress();
            paymentMethodsLiveData.setValue(paymentMethods);
        }
    }

    private StoredPaymentMethodSummary executeGetPaymentMethodByIdRequest(String paymentMethodId) throws ApiException {
        return ReportingService
                .storedPaymentMethodDetail(paymentMethodId)
                .execute(DEFAULT_GPAPI_CONFIG);
    }

    private List<StoredPaymentMethodSummary> executeGetPaymentMethodsRequest(TransactionReportParameters reportParameters) throws ApiException {
        TransactionReportBuilder<StoredPaymentMethodSummaryPaged> reportBuilder =
                ReportingService.findStoredPaymentMethodsPaged(reportParameters.getPage(), reportParameters.getPageSize());
                    reportBuilder.setOrder(reportParameters.getOrder());
                    reportBuilder.setTransactionOrderBy(reportParameters.getOrderBy());
                    reportBuilder.withStoredPaymentMethodId(reportParameters.getId());
                    reportBuilder.where(SearchCriteria.ReferenceNumber, reportParameters.getReference());
                    reportBuilder.where(SearchCriteria.StoredPaymentMethodStatus, reportParameters.getTransactionStatus());
                    reportBuilder.where(SearchCriteria.StartDate, reportParameters.getFromTimeCreated())
                            .and(SearchCriteria.EndDate, reportParameters.getToTimeCreated());
                    reportBuilder.where(DataServiceCriteria.StartLastUpdatedDate, reportParameters.getFromTimeLastUpdated())
                            .and(DataServiceCriteria.EndLastUpdatedDate, reportParameters.getToTimeLastUpdated());

        return reportBuilder.execute(DEFAULT_GPAPI_CONFIG).getResults();
    }

}

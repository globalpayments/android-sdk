package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.builders.TransactionReportBuilder;
import com.global.api.entities.Transaction;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.reporting.StoredPaymentMethodSummary;
import com.global.api.entities.reporting.StoredPaymentMethodSummaryPaged;
import com.global.api.paymentMethods.CreditCardData;
import com.global.api.services.ReportingService;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel;

import java.util.Collections;
import java.util.List;

public class PaymentMethodReportViewModel extends BaseViewModel {

    int page = 1;
    int pageSize = 5;

    private MutableLiveData<List<Transaction>> paymentMethodsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<StoredPaymentMethodSummary>> paymentMethodsListLiveData = new MutableLiveData<>();

    public LiveData<List<Transaction>> getPaymentMethodsLiveData() {
        return paymentMethodsLiveData;
    }

    public LiveData<List<StoredPaymentMethodSummary>> getPaymentMethodsListLiveData() {
        return paymentMethodsListLiveData;
    }

    public void getPaymentMethodList() {
        showProgress();
        TaskExecutor.executeAsync(new TaskExecutor.Task<List<StoredPaymentMethodSummary>>() {
            @Override
            public List<StoredPaymentMethodSummary> executeAsync() throws Exception {
                return executeGetPaymentMethodsRequest();
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

        TaskExecutor.executeAsync(new TaskExecutor.Task<Transaction>() {
            @Override
            public Transaction executeAsync() throws Exception {
                return executeGetPaymentMethodByIdRequest(paymentMethodId);
            }

            @Override
            public void onSuccess(Transaction value) {
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

    private void showResultPaymentMethodById(List<Transaction> paymentMethods) {
        if (paymentMethods == null || paymentMethods.isEmpty()) {
            showError(new Exception("Empty Payment Method Report List"));
        } else {
            hideProgress();
            paymentMethodsLiveData.setValue(paymentMethods);
        }
    }

    private Transaction executeGetPaymentMethodByIdRequest(String paymentMethodId) throws ApiException {
        CreditCardData tokenizedCard = new CreditCardData();
        tokenizedCard.setToken(paymentMethodId);
        return tokenizedCard
                .verify()
                .execute(DEFAULT_GPAPI_CONFIG);
    }

    private List<StoredPaymentMethodSummary> executeGetPaymentMethodsRequest() throws ApiException {
        return getPaymentMethodsReportBuilder().execute(DEFAULT_GPAPI_CONFIG).getResults();
    }

    private TransactionReportBuilder<StoredPaymentMethodSummaryPaged> getPaymentMethodsReportBuilder() {
        return ReportingService.findStoredPaymentMethodsPaged(page, pageSize);
    }

}

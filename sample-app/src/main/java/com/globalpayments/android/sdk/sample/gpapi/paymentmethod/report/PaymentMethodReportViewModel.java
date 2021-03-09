package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.entities.Transaction;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.paymentMethods.CreditCardData;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel;
import com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report.model.PaymentMethodReportParameters;

import java.util.Collections;
import java.util.List;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;

public class PaymentMethodReportViewModel extends BaseViewModel {
    private MutableLiveData<List<Transaction>> paymentMethodsLiveData = new MutableLiveData<>();

    public LiveData<List<Transaction>> getPaymentMethodsLiveData() {
        return paymentMethodsLiveData;
    }

    public void getPaymentMethodList(PaymentMethodReportParameters paymentMethodReportParameters) {

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
                showResult(Collections.singletonList(value));
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    private void showResult(List<Transaction> paymentMethods) {
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
}

package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;
import static com.globalpayments.android.sdk.utils.Utils.isNullOrBlank;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.entities.exceptions.ApiException;
import com.global.api.paymentMethods.CreditCardData;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel;
import com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations.model.PaymentMethodOperationModel;
import com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations.model.PaymentMethodOperationUIModel;

public class PaymentMethodOperationsViewModel extends BaseViewModel {
    private final MutableLiveData<PaymentMethodOperationUIModel> paymentMethodOperationUIModelLiveData = new MutableLiveData<>();

    public LiveData<PaymentMethodOperationUIModel> getPaymentMethodOperationUIModelLiveData() {
        return paymentMethodOperationUIModelLiveData;
    }

    private void showResult(PaymentMethodOperationUIModel paymentMethodOperationUIModel) {
        hideProgress();
        paymentMethodOperationUIModelLiveData.setValue(paymentMethodOperationUIModel);
    }

    public void executePaymentMethodOperation(PaymentMethodOperationModel paymentMethodOperationModel) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<PaymentMethodOperationUIModel>() {
            @Override
            public PaymentMethodOperationUIModel executeAsync() throws Exception {
                return executeRequest(paymentMethodOperationModel);
            }

            @Override
            public void onSuccess(PaymentMethodOperationUIModel value) {
                showResult(value);
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    private PaymentMethodOperationUIModel executeRequest(PaymentMethodOperationModel paymentMethodOperationModel) throws Exception {
        PaymentMethodOperationUIModel paymentMethodOperationUIModel = new PaymentMethodOperationUIModel();

        switch (paymentMethodOperationModel.getPaymentMethodOperationType()) {
            case TOKENIZE:
                tokenize(paymentMethodOperationUIModel, paymentMethodOperationModel);
                break;

            case EDIT:
                paymentMethodOperationUIModel.setResult(edit(paymentMethodOperationModel));
                break;

            case DELETE:
                paymentMethodOperationUIModel.setResult(delete(paymentMethodOperationModel));
                break;
        }

        return paymentMethodOperationUIModel;
    }

    private void tokenize(PaymentMethodOperationUIModel paymentMethodOperationUIModel,
                          PaymentMethodOperationModel paymentMethodOperationModel) throws ApiException {

        CreditCardData card = new CreditCardData();
        card.setNumber(paymentMethodOperationModel.getCardNumber());
        card.setExpMonth(paymentMethodOperationModel.getExpiryMonth());
        card.setExpYear(paymentMethodOperationModel.getExpiryYear());
        card.setCvn(paymentMethodOperationModel.getCvnCvv());

        String token = card.tokenize(DEFAULT_GPAPI_CONFIG);

        if (isNullOrBlank(token)) {
            paymentMethodOperationUIModel.setResult(false);
        } else {
            paymentMethodOperationUIModel.setPaymentMethodId(token);
        }
    }

    private boolean edit(PaymentMethodOperationModel paymentMethodOperationModel) throws ApiException {
        CreditCardData card = new CreditCardData();
        card.setNumber(paymentMethodOperationModel.getCardNumber());
        card.setExpMonth(paymentMethodOperationModel.getExpiryMonth());
        card.setExpYear(paymentMethodOperationModel.getExpiryYear());
        card.setCvn(paymentMethodOperationModel.getCvnCvv());
        card.setToken(paymentMethodOperationModel.getPaymentMethodId());
        return card.updateTokenExpiry(DEFAULT_GPAPI_CONFIG);
    }

    private boolean delete(PaymentMethodOperationModel paymentMethodOperationModel) throws ApiException {
        CreditCardData card = new CreditCardData();
        card.setToken(paymentMethodOperationModel.getPaymentMethodId());
        return card.deleteToken(DEFAULT_GPAPI_CONFIG);
    }
}
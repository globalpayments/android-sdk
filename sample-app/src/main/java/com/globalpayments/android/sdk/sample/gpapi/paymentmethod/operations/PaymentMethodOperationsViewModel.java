package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.exceptions.BuilderException;
import com.global.api.paymentMethods.CreditCardData;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel;
import com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations.model.PaymentMethodOperationModel;
import com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations.model.PaymentMethodOperationUIModel;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;
import static com.globalpayments.android.sdk.utils.Utils.isNullOrBlank;

public class PaymentMethodOperationsViewModel extends BaseViewModel {
    private MutableLiveData<PaymentMethodOperationUIModel> paymentMethodOperationUIModelLiveData = new MutableLiveData<>();

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

            case DETOKENIZE:
                detokenize(paymentMethodOperationUIModel, paymentMethodOperationModel);
                break;

            case DELETE:
                paymentMethodOperationUIModel.setResult(delete(paymentMethodOperationModel));
                break;
        }

        return paymentMethodOperationUIModel;
    }

    private void tokenize(PaymentMethodOperationUIModel paymentMethodOperationUIModel,
                            PaymentMethodOperationModel paymentMethodOperationModel) {

        CreditCardData card = new CreditCardData();
        card.setNumber(paymentMethodOperationModel.getCardNumber());
        card.setExpMonth(paymentMethodOperationModel.getExpiryMonth());
        card.setExpYear(paymentMethodOperationModel.getExpiryYear());
        card.setCvn(paymentMethodOperationModel.getCvnCvv());

        String token = card.tokenizeWithIdempotencyKey(true, DEFAULT_GPAPI_CONFIG,
                paymentMethodOperationModel.getIdempotencyKey());

        if (isNullOrBlank(token)) {
            paymentMethodOperationUIModel.setResult(false);
        } else {
            paymentMethodOperationUIModel.setPaymentMethodId(token);
        }
    }

    private boolean edit(PaymentMethodOperationModel paymentMethodOperationModel) throws BuilderException {
        CreditCardData card = new CreditCardData();
        card.setNumber(paymentMethodOperationModel.getCardNumber());
        card.setExpMonth(paymentMethodOperationModel.getExpiryMonth());
        card.setExpYear(paymentMethodOperationModel.getExpiryYear());
        card.setCvn(paymentMethodOperationModel.getCvnCvv());
        card.setToken(paymentMethodOperationModel.getPaymentMethodId());
        return card.updateTokenExpiryWithIdemPotencyKey(DEFAULT_GPAPI_CONFIG, paymentMethodOperationModel.getIdempotencyKey());
    }

    private void detokenize(PaymentMethodOperationUIModel paymentMethodOperationUIModel,
                            PaymentMethodOperationModel paymentMethodOperationModel) throws ApiException {

        CreditCardData tokenizedCard = new CreditCardData();
        tokenizedCard.setToken(paymentMethodOperationModel.getPaymentMethodId());

        CreditCardData detokenizedCard = tokenizedCard.detokenizeWithIdemPotencyKey(DEFAULT_GPAPI_CONFIG,
                paymentMethodOperationModel.getIdempotencyKey());

        paymentMethodOperationUIModel.setCardNumber(detokenizedCard.getNumber());
        paymentMethodOperationUIModel.setExpiryMonth(String.valueOf(detokenizedCard.getExpMonth()));
        paymentMethodOperationUIModel.setExpiryYear(String.valueOf(detokenizedCard.getExpYear()));
        paymentMethodOperationUIModel.setCardType(detokenizedCard.getCardType());
    }

    private boolean delete(PaymentMethodOperationModel paymentMethodOperationModel) throws BuilderException {
        CreditCardData card = new CreditCardData();
        card.setToken(paymentMethodOperationModel.getPaymentMethodId());
        return card.deleteTokenWithIdempotencyKey(DEFAULT_GPAPI_CONFIG, paymentMethodOperationModel.getIdempotencyKey());
    }
}
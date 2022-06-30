package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;
import static com.globalpayments.android.sdk.sample.utils.FingerPrintUsageMethod.fingerPrintSelectedOption;
import static com.globalpayments.android.sdk.utils.Utils.isNullOrBlank;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.entities.Customer;
import com.global.api.entities.Transaction;
import com.global.api.entities.enums.PaymentMethodUsageMode;
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
                paymentMethodOperationUIModel.setTokenUpdate(edit(paymentMethodOperationModel));
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
        card.setExpMonth(Integer.parseInt(paymentMethodOperationModel.getExpiryMonth()));
        card.setExpYear(Integer.parseInt(paymentMethodOperationModel.getExpiryYear()));
        card.setCvn(paymentMethodOperationModel.getCvnCvv());

        Customer customer =
                fingerPrintSelectedOption(
                        paymentMethodOperationModel.getFingerprintMethodUsageMode()
                );

        String token =
                paymentMethodUsageMode(paymentMethodOperationModel, card, customer);

        if (isNullOrBlank(token)) {
            paymentMethodOperationUIModel.setResult(false);
        } else {
            paymentMethodOperationUIModel.setPaymentMethodId(token);
        }
    }

    private String edit(PaymentMethodOperationModel paymentMethodOperationModel) throws ApiException {
        CreditCardData card = new CreditCardData();

        Transaction responseUpdateToken = null;

        switch (paymentMethodOperationModel.getPaymentMethodUsageMode()) {
            case SINGLE:
                if (paymentMethodOperationModel.getCardHolderName().equals("") ||
                        paymentMethodOperationModel.getCardNumber().equals("") ||
                        paymentMethodOperationModel.getExpiryYear().equals("") ||
                        paymentMethodOperationModel.getExpiryMonth().equals("")
                ) {
                    CreditCardData tokenizedCard = new CreditCardData();
                    tokenizedCard.setToken(paymentMethodOperationModel.getPaymentMethodId());
                    tokenizedCard.setCardHolderName(paymentMethodOperationModel.getCardHolderName());

                    responseUpdateToken =
                            tokenizedCard
                                    .updateToken()
                                    .withPaymentMethodUsageMode(PaymentMethodUsageMode.SINGLE)
                                    .execute(DEFAULT_GPAPI_CONFIG);
                } else {
                    card.setToken(paymentMethodOperationModel.getPaymentMethodId());
                    card.setCardHolderName(paymentMethodOperationModel.getCardHolderName());
                    card.setExpYear(Integer.parseInt(paymentMethodOperationModel.getExpiryYear()));
                    card.setExpMonth(Integer.parseInt(paymentMethodOperationModel.getExpiryMonth()));
                    card.setNumber(paymentMethodOperationModel.getCardNumber());

                    responseUpdateToken = card
                            .updateToken()
                            .withPaymentMethodUsageMode(PaymentMethodUsageMode.SINGLE)
                            .execute(DEFAULT_GPAPI_CONFIG);
                }
                break;
            case MULTIPLE:
                if (paymentMethodOperationModel.getCardHolderName().equals("") &&
                        paymentMethodOperationModel.getCardNumber().equals("") &&
                        paymentMethodOperationModel.getExpiryYear().equals("") &&
                        paymentMethodOperationModel.getExpiryMonth().equals("")
                ) {
                    CreditCardData tokenizedCard = new CreditCardData();
                    tokenizedCard.setToken(paymentMethodOperationModel.getPaymentMethodId());

                    responseUpdateToken =
                            tokenizedCard
                                    .updateToken()
                                    .withPaymentMethodUsageMode(PaymentMethodUsageMode.MULTIPLE)
                                    .execute(DEFAULT_GPAPI_CONFIG);
                } else if (paymentMethodOperationModel.getCardHolderName() != null &&
                            paymentMethodOperationModel.getCardNumber().equals("") &&
                            paymentMethodOperationModel.getExpiryYear().equals("") &&
                            paymentMethodOperationModel.getExpiryMonth().equals("")) {
                    CreditCardData tokenizedCard = new CreditCardData();
                    tokenizedCard.setToken(paymentMethodOperationModel.getPaymentMethodId());
                    tokenizedCard.setCardHolderName(paymentMethodOperationModel.getCardHolderName());

                    responseUpdateToken =
                            tokenizedCard
                                    .updateToken()
                                    .withPaymentMethodUsageMode(PaymentMethodUsageMode.MULTIPLE)
                                    .execute(DEFAULT_GPAPI_CONFIG);
                } else if (paymentMethodOperationModel.getCardNumber() != null &&
                            paymentMethodOperationModel.getCardHolderName().equals("") &&
                            paymentMethodOperationModel.getExpiryYear().equals("") &&
                            paymentMethodOperationModel.getExpiryMonth().equals("")) {
                    CreditCardData tokenizedCard = new CreditCardData();
                    tokenizedCard.setToken(paymentMethodOperationModel.getPaymentMethodId());
                    tokenizedCard.setNumber(paymentMethodOperationModel.getCardNumber());

                    responseUpdateToken =
                            tokenizedCard
                                    .updateToken()
                                    .withPaymentMethodUsageMode(PaymentMethodUsageMode.MULTIPLE)
                                    .execute(DEFAULT_GPAPI_CONFIG);
                } else if (paymentMethodOperationModel.getCardHolderName() != null &&
                        paymentMethodOperationModel.getCardNumber() != null &&
                        paymentMethodOperationModel.getExpiryYear().equals("") &&
                        paymentMethodOperationModel.getExpiryMonth().equals("")) {
                    CreditCardData tokenizedCard = new CreditCardData();
                    tokenizedCard.setToken(paymentMethodOperationModel.getPaymentMethodId());
                    tokenizedCard.setCardHolderName(paymentMethodOperationModel.getCardHolderName());
                    tokenizedCard.setNumber(paymentMethodOperationModel.getCardNumber());

                    responseUpdateToken =
                            tokenizedCard
                                    .updateToken()
                                    .withPaymentMethodUsageMode(PaymentMethodUsageMode.MULTIPLE)
                                    .execute(DEFAULT_GPAPI_CONFIG);
                } else if (paymentMethodOperationModel.getCardHolderName() != null &&
                        paymentMethodOperationModel.getCardNumber() != null &&
                        paymentMethodOperationModel.getExpiryYear() != null &&
                        paymentMethodOperationModel.getExpiryMonth() != null) {
                    CreditCardData tokenizedCard = new CreditCardData();
                    tokenizedCard.setToken(paymentMethodOperationModel.getPaymentMethodId());
                    tokenizedCard.setCardHolderName(paymentMethodOperationModel.getCardHolderName());
                    tokenizedCard.setNumber(paymentMethodOperationModel.getCardNumber());
                    tokenizedCard.setExpYear(Integer.parseInt(paymentMethodOperationModel.getExpiryYear()));
                    tokenizedCard.setExpMonth(Integer.parseInt(paymentMethodOperationModel.getExpiryMonth()));

                    responseUpdateToken =
                            tokenizedCard
                                    .updateToken()
                                    .withPaymentMethodUsageMode(PaymentMethodUsageMode.MULTIPLE)
                                    .execute(DEFAULT_GPAPI_CONFIG);
                } else if (paymentMethodOperationModel.getExpiryYear() != null &&
                        paymentMethodOperationModel.getExpiryMonth() != null &&
                        paymentMethodOperationModel.getCardHolderName().equals("") &&
                        paymentMethodOperationModel.getCardNumber().equals("")) {
                    CreditCardData tokenizedCard = new CreditCardData();
                    tokenizedCard.setToken(paymentMethodOperationModel.getPaymentMethodId());
                    tokenizedCard.setCardHolderName(paymentMethodOperationModel.getCardHolderName());
                    tokenizedCard.setNumber(paymentMethodOperationModel.getCardNumber());
                    tokenizedCard.setExpYear(Integer.parseInt(paymentMethodOperationModel.getExpiryYear()));
                    tokenizedCard.setExpMonth(Integer.parseInt(paymentMethodOperationModel.getExpiryMonth()));

                    responseUpdateToken =
                            tokenizedCard
                                    .updateToken()
                                    .withPaymentMethodUsageMode(PaymentMethodUsageMode.MULTIPLE)
                                    .execute(DEFAULT_GPAPI_CONFIG);
                } else {
                    card.setToken(paymentMethodOperationModel.getPaymentMethodId());
                    card.setCardHolderName(paymentMethodOperationModel.getCardHolderName());
                    card.setNumber(paymentMethodOperationModel.getCardNumber());
                    card.setExpYear(Integer.parseInt(paymentMethodOperationModel.getExpiryYear()));
                    card.setExpMonth(Integer.parseInt(paymentMethodOperationModel.getExpiryMonth()));

                    responseUpdateToken = card
                            .updateToken()
                            .withPaymentMethodUsageMode(PaymentMethodUsageMode.MULTIPLE)
                            .execute(DEFAULT_GPAPI_CONFIG);
                }
                break;
        }

        return responseUpdateToken.getToken();
    }

    private boolean delete(PaymentMethodOperationModel paymentMethodOperationModel) throws ApiException {
        CreditCardData card = new CreditCardData();
        card.setToken(paymentMethodOperationModel.getPaymentMethodId());
        return card.deleteToken(DEFAULT_GPAPI_CONFIG);
    }

    private String paymentMethodUsageMode(
            PaymentMethodOperationModel paymentMethodOperationModel,
            CreditCardData creditCardData,
            Customer customer
    ) throws ApiException {
        String token = null;

        if (paymentMethodOperationModel.getFingerPrintSelection()) {
            switch (paymentMethodOperationModel.getPaymentMethodUsageMode()) {
                case SINGLE:
                    token = creditCardData
                            .tokenize(true, PaymentMethodUsageMode.SINGLE)
                            .withCustomerData(customer)
                            .execute(DEFAULT_GPAPI_CONFIG)
                            .getToken();
                    break;
                case MULTIPLE:
                    token = creditCardData
                            .tokenize(true, PaymentMethodUsageMode.MULTIPLE)
                            .withCustomerData(customer)
                            .execute(DEFAULT_GPAPI_CONFIG)
                            .getToken();
                    break;
            }
        } else {
            switch (paymentMethodOperationModel.getPaymentMethodUsageMode()) {
                case SINGLE:
                    token = creditCardData
                            .tokenize(true, PaymentMethodUsageMode.SINGLE)
                            .execute(DEFAULT_GPAPI_CONFIG)
                            .getToken();
                    break;
                case MULTIPLE:
                    token = creditCardData
                            .tokenize(true, PaymentMethodUsageMode.MULTIPLE)
                            .execute(DEFAULT_GPAPI_CONFIG)
                            .getToken();
                    break;
            }
        }

        return token;
    }
}

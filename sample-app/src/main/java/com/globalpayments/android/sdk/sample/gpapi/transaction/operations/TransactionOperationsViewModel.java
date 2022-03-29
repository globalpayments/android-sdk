package com.globalpayments.android.sdk.sample.gpapi.transaction.operations;

import static com.globalpayments.android.sdk.sample.utils.GPAPIConfigurationUtils.buildDefaultGpApiConfig;
import static com.globalpayments.android.sdk.sample.utils.GPAPIConfigurationUtils.configureService;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.entities.ThreeDSecure;
import com.global.api.entities.Transaction;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.paymentMethods.CreditCardData;
import com.global.api.serviceConfigs.GpApiConfig;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseAndroidViewModel;
import com.globalpayments.android.sdk.sample.gpapi.configuration.GPAPIConfiguration;
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationModel;
import com.globalpayments.android.sdk.sample.utils.AppPreferences;
import com.globalpayments.android.sdk.sample.utils.PaymentMethodUsageMode;

public class TransactionOperationsViewModel extends BaseAndroidViewModel {

    private static final String TRANSACTION_OPERATIONS_GPAPI_CONFIG = "TRANSACTION_OPERATIONS_GPAPI_CONFIG";

    public TransactionOperationsViewModel(@NonNull Application application) {
        super(application);
    }

    private final MutableLiveData<Transaction> transactionLiveData = new MutableLiveData<>();
    private final MutableLiveData<ThreeDSecure> transactionLiveDataError = new MutableLiveData<>();
    private final MutableLiveData<String> transactionMessageError = new MutableLiveData<>();

    public LiveData<Transaction> getTransactionLiveData() {
        return transactionLiveData;
    }

    public LiveData<ThreeDSecure> getTransactionLiveDataError() {
        return transactionLiveDataError;
    }

    public LiveData<String> getTransactionMessageError() {
        return transactionMessageError;
    }

    private final MutableLiveData<ThreeDSecure> typeCardSelectedOption = new MutableLiveData<>();

    public LiveData<ThreeDSecure> getTypeCardSelectedOption() {
        return typeCardSelectedOption;
    }

    private TransactionOperationModel transactionOperationModel;

    private CreditCardData card;

    @Override
    protected void init() {
        GPAPIConfiguration gpapiConfiguration = new AppPreferences(getApplication()).getGPAPIConfiguration();
        GpApiConfig gpApiConfig = buildDefaultGpApiConfig(gpapiConfiguration);

        card = new CreditCardData();

        if (!configureService(gpApiConfig, gpapiConfiguration, TRANSACTION_OPERATIONS_GPAPI_CONFIG)) {
            Toast.makeText(getApplication(), R.string.configure_service_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void showResult(Transaction transaction) {
        hideProgress();
        transactionLiveData.setValue(transaction);
    }

    private void setCreditCardData(String cardNumber, int expiryMonth, int expiryYear, String cvnCvv) {
        card.setNumber(cardNumber);
        card.setExpMonth(expiryMonth);
        card.setExpYear(expiryYear);
        card.setCvn(cvnCvv);
    }

    public void executeTransactionOperation(TransactionOperationModel model) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<Transaction>() {
            @Override
            public Transaction executeAsync() throws Exception {
                transactionOperationModel = model;
                return executeRequest();
            }

            @Override
            public void onSuccess(Transaction value) {
                showResult(value);
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    private Transaction executeRequest() throws ApiException {
        setCreditCardData(transactionOperationModel.getCardNumber(),
                transactionOperationModel.getExpiryMonth(),
                transactionOperationModel.getExpiryYear(),
                transactionOperationModel.getCvnCvv());
        return executeTransaction();
    }

    private Transaction executeTransaction() throws ApiException {
        card.setNumber(transactionOperationModel.getCardNumber());
        card.setExpMonth(transactionOperationModel.getExpiryMonth());
        card.setExpYear(transactionOperationModel.getExpiryYear());
        card.setCvn(transactionOperationModel.getCvnCvv());

        boolean isRequestedMultiUseToken = transactionOperationModel.getRequestMultiUseToken();

        Transaction transaction = null;

        card = finishTransactionOperation(
                transactionOperationModel,
                transactionOperationModel.getPaymentMethodUsageMode(),
                card
        );

        switch (transactionOperationModel.getTransactionOperationType()) {

            case AUTHORIZATION:
                transaction = card.authorize(transactionOperationModel.getAmount())
                        .withCurrency(transactionOperationModel.getCurrency())
                        .withRequestMultiUseToken(isRequestedMultiUseToken)
                        .withIdempotencyKey(transactionOperationModel.getIdempotencyKey())
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;

            case SALE:
                transaction = card.charge(transactionOperationModel.getAmount())
                        .withCurrency(transactionOperationModel.getCurrency())
                        .withRequestMultiUseToken(isRequestedMultiUseToken)
                        .withIdempotencyKey(transactionOperationModel.getIdempotencyKey())
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;

            case CAPTURE:
                Transaction authorizationTransaction = card.authorize(transactionOperationModel.getAmount())
                        .withCurrency(transactionOperationModel.getCurrency())
                        .withRequestMultiUseToken(isRequestedMultiUseToken)
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);

                transaction = authorizationTransaction.capture(transactionOperationModel.getAmount())
                        .withIdempotencyKey(transactionOperationModel.getIdempotencyKey())
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;

            case REFUND:
                Transaction saleTransaction = card.charge(transactionOperationModel.getAmount())
                        .withCurrency(transactionOperationModel.getCurrency())
                        .withRequestMultiUseToken(isRequestedMultiUseToken)
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);

                transaction = saleTransaction.refund(transactionOperationModel.getAmount())
                        .withIdempotencyKey(transactionOperationModel.getIdempotencyKey())
                        .withCurrency(transactionOperationModel.getCurrency())
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;

            case REVERSE:
                Transaction saleTransaction2 = card.charge(transactionOperationModel.getAmount())
                        .withCurrency(transactionOperationModel.getCurrency())
                        .withRequestMultiUseToken(isRequestedMultiUseToken)
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);

                transaction = saleTransaction2.reverse(transactionOperationModel.getAmount())
                        .withIdempotencyKey(transactionOperationModel.getIdempotencyKey())
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;
            case REAUTHORIZATION:
                Transaction chargeTransaction =
                        card
                                .charge(transactionOperationModel.getAmount())
                                .withCurrency(transactionOperationModel.getCurrency())
                                .withRequestMultiUseToken(isRequestedMultiUseToken)
                                .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);

                Transaction reverseTransaction =
                        chargeTransaction
                                .reverse(transactionOperationModel.getAmount())
                                .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);

                transaction =
                        reverseTransaction
                                .reauthorize()
                                .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
        }
        return transaction;
    }

    private CreditCardData finishTransactionOperation(
            TransactionOperationModel transactionOperationModel,
            PaymentMethodUsageMode paymentMethodUsageMode,
            CreditCardData creditCardData
    ) throws ApiException {
        String token;
        CreditCardData tokenizedCard = new CreditCardData();

        switch (paymentMethodUsageMode) {
            case No:
                tokenizedCard.setNumber(transactionOperationModel.getCardNumber());
                tokenizedCard.setExpMonth(transactionOperationModel.getExpiryMonth());
                tokenizedCard.setExpYear(transactionOperationModel.getExpiryYear());
                tokenizedCard.setCvn(transactionOperationModel.getCvnCvv());

                break;
            case Single_use_token:
                token = creditCardData.tokenize(true, TRANSACTION_OPERATIONS_GPAPI_CONFIG,
                        com.global.api.entities.enums.PaymentMethodUsageMode.Single);

                tokenizedCard = new CreditCardData();
                tokenizedCard.setToken(token);
                break;
            case Multiple_use_token:
                token = creditCardData.tokenize(true, TRANSACTION_OPERATIONS_GPAPI_CONFIG,
                        com.global.api.entities.enums.PaymentMethodUsageMode.Multiple);

                tokenizedCard = new CreditCardData();
                tokenizedCard.setToken(token);
                break;
        }
        return tokenizedCard;
    }

}

package com.globalpayments.android.sdk.sample.gpapi.transaction.operations;

import static com.globalpayments.android.sdk.sample.utils.GPAPIConfigurationUtils.buildDefaultGpApiConfig;
import static com.globalpayments.android.sdk.sample.utils.GPAPIConfigurationUtils.configureService;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.entities.Transaction;
import com.global.api.entities.enums.Channel;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.paymentMethods.CreditCardData;
import com.global.api.serviceConfigs.GpApiConfig;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseAndroidViewModel;
import com.globalpayments.android.sdk.sample.gpapi.configuration.GPAPIConfiguration;
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationModel;
import com.globalpayments.android.sdk.sample.utils.AppPreferences;

import java.math.BigDecimal;

public class TransactionOperationsViewModel extends BaseAndroidViewModel {
    private static final String TRANSACTION_OPERATIONS_GPAPI_CONFIG = "TRANSACTION_OPERATIONS_GPAPI_CONFIG";

    private final MutableLiveData<Transaction> transactionLiveData = new MutableLiveData<>();

    public TransactionOperationsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Transaction> getTransactionLiveData() {
        return transactionLiveData;
    }

    @Override
    protected void init() {
        GPAPIConfiguration gpapiConfiguration = new AppPreferences(getApplication()).getGPAPIConfiguration();
        GpApiConfig gpApiConfig = buildDefaultGpApiConfig(gpapiConfiguration);
        gpApiConfig.setChannel(Channel.CardNotPresent.getValue());

        if (!configureService(gpApiConfig, gpapiConfiguration, TRANSACTION_OPERATIONS_GPAPI_CONFIG)) {
            Toast.makeText(getApplication(), R.string.configure_service_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void showResult(Transaction transaction) {
        hideProgress();
        transactionLiveData.setValue(transaction);
    }

    public void executeTransactionOperation(TransactionOperationModel transactionOperationModel) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<Transaction>() {
            @Override
            public Transaction executeAsync() throws Exception {
                return executeRequest(transactionOperationModel);
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

    private Transaction executeRequest(TransactionOperationModel transactionOperationModel) throws ApiException {
        CreditCardData card = new CreditCardData();
        card.setNumber(transactionOperationModel.getCardNumber());
        card.setExpMonth(transactionOperationModel.getExpiryMonth());
        card.setExpYear(transactionOperationModel.getExpiryYear());
        card.setCvn(transactionOperationModel.getCvnCvv());

        BigDecimal amount = transactionOperationModel.getAmount();
        String currency = transactionOperationModel.getCurrency();
        String idempotencyKey = transactionOperationModel.getIdempotencyKey();
        boolean isRequestedMultiUseToken = transactionOperationModel.isRequestedMultiUseToken();

        Transaction transaction = null;

        switch (transactionOperationModel.getTransactionOperationType()) {

            case AUTHORIZATION:
                transaction = card
                        .authorize(amount)
                        .withCurrency(currency)
                        .withRequestMultiUseToken(isRequestedMultiUseToken)
                        .withIdempotencyKey(idempotencyKey)
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;

            case SALE:
                transaction = card
                        .charge(amount)
                        .withCurrency(currency)
                        .withRequestMultiUseToken(isRequestedMultiUseToken)
                        .withIdempotencyKey(idempotencyKey)
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;

            case CAPTURE:
                Transaction authorizationTransaction = card
                        .authorize(amount)
                        .withCurrency(currency)
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);

                transaction = authorizationTransaction
                        .capture(amount)
                        .withIdempotencyKey(idempotencyKey)
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;

            case REFUND:
                Transaction saleTransaction = card
                        .charge(amount)
                        .withCurrency(currency)
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);

                transaction = saleTransaction
                        .refund(amount)
                        .withIdempotencyKey(idempotencyKey)
                        .withCurrency(currency)
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;

            case REVERSE:
                Transaction saleTransaction2 = card
                        .charge(amount)
                        .withCurrency(currency)
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);

                transaction = saleTransaction2
                        .reverse(amount)
                        .withIdempotencyKey(idempotencyKey)
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;
        }

        return transaction;
    }
}
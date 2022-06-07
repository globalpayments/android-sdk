package com.globalpayments.android.sdk.sample.gpapi.verifications;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;
import static com.globalpayments.android.sdk.sample.utils.FingerPrintUsageMethod.fingerPrintSelectedOption;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.entities.Customer;
import com.global.api.entities.Transaction;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.paymentMethods.CreditCardData;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel;
import com.globalpayments.android.sdk.sample.gpapi.verifications.model.VerificationsModel;

public class VerificationsViewModel extends BaseViewModel {
    private final MutableLiveData<Transaction> transactionLiveData = new MutableLiveData<>();

    public LiveData<Transaction> getTransactionLiveData() {
        return transactionLiveData;
    }

    private void showResult(Transaction transaction) {
        hideProgress();
        transactionLiveData.setValue(transaction);
    }

    public void executeVerification(VerificationsModel verificationsModel) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<Transaction>() {
            @Override
            public Transaction executeAsync() throws Exception {
                return executeRequest(verificationsModel);
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

    private Transaction executeRequest(VerificationsModel verificationsModel) throws Exception {
        CreditCardData creditCardData = new CreditCardData();
        creditCardData.setNumber(verificationsModel.getCardNumber());
        creditCardData.setExpMonth(verificationsModel.getExpiryMonth());
        creditCardData.setExpYear(verificationsModel.getExpiryYear());
        creditCardData.setCvn(verificationsModel.getCvnCvv());

        return sendFingerPrint(verificationsModel, creditCardData);
    }

    private Transaction sendFingerPrint(
            VerificationsModel verificationsModel,
            CreditCardData creditCardData
    ) throws ApiException {
        Transaction transaction;
        if (verificationsModel.getFingerPrintSelection()) {
            Customer customer =
                    fingerPrintSelectedOption(
                            verificationsModel.getFingerprintMethodUsageMode()
                    );

            CreditCardData tokenizedCard = new CreditCardData();
            tokenizedCard.setToken(creditCardData.tokenize(DEFAULT_GPAPI_CONFIG));

            transaction = tokenizedCard
                    .verify()
                    .withIdempotencyKey(verificationsModel.getIdempotencyKey())
                    .withCurrency(verificationsModel.getCurrency())
                    .withCustomerData(customer)
                    .execute(DEFAULT_GPAPI_CONFIG);
        } else {
            transaction = creditCardData
                    .verify()
                    .withIdempotencyKey(verificationsModel.getIdempotencyKey())
                    .withCurrency(verificationsModel.getCurrency())
                    .execute(DEFAULT_GPAPI_CONFIG);
        }
        return transaction;
    }
}

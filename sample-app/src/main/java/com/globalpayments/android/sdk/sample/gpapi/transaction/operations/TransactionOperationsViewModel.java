package com.globalpayments.android.sdk.sample.gpapi.transaction.operations;

import static com.globalpayments.android.sdk.sample.common.Constants.CHALLENGE_REQUIRED;
import static com.globalpayments.android.sdk.sample.common.Constants.VISA_3DS1_ENROLLED;
import static com.globalpayments.android.sdk.sample.common.Constants.VISA_3DS1_NOT_ENROLLED;
import static com.globalpayments.android.sdk.sample.common.Constants.VISA_3DS2_CHALLENGE;
import static com.globalpayments.android.sdk.sample.common.Constants.VISA_3DS2_FRICTIONLESS;
import static com.globalpayments.android.sdk.sample.utils.GPAPIConfigurationUtils.buildDefaultGpApiConfig;
import static com.globalpayments.android.sdk.sample.utils.GPAPIConfigurationUtils.configureService;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.entities.BrowserData;
import com.global.api.entities.ThreeDSecure;
import com.global.api.entities.Transaction;
import com.global.api.entities.enums.AuthenticationSource;
import com.global.api.entities.enums.ChallengeWindowSize;
import com.global.api.entities.enums.ColorDepth;
import com.global.api.entities.enums.MethodUrlCompletion;
import com.global.api.entities.enums.Secure3dVersion;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.paymentMethods.CreditCardData;
import com.global.api.serviceConfigs.GpApiConfig;
import com.global.api.services.Secure3dService;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseAndroidViewModel;
import com.globalpayments.android.sdk.sample.gpapi.configuration.GPAPIConfiguration;
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationModel;
import com.globalpayments.android.sdk.sample.utils.AppPreferences;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionOperationsViewModel extends BaseAndroidViewModel {

    private static final String TRANSACTION_OPERATIONS_GPAPI_CONFIG = "TRANSACTION_OPERATIONS_GPAPI_CONFIG";

    private final static String ENROLLED = "ENROLLED";
    private final static String NOT_ENROLLED = "NOT_ENROLLED";
    private final static String AVAILABLE = "AVAILABLE";
    private final static String SUCCESS_AUTHENTICATED = "SUCCESS_AUTHENTICATED";
    private final static String AUTHENTICATION_SUCCESSFUL = "AUTHENTICATION_SUCCESSFUL";

    public TransactionOperationsViewModel(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<Transaction> transactionLiveData = new MutableLiveData<>();

    public LiveData<Transaction> getTransactionLiveData() {
        return transactionLiveData;
    }

    private MutableLiveData<ThreeDSecure> typeCardSelectedOption = new MutableLiveData<>();

    public LiveData<ThreeDSecure> getTypeCardSelectedOption() {
        return typeCardSelectedOption;
    }

    private TransactionOperationModel transactionOperationModel;

    private ThreeDSecure secureEcom;
    private ThreeDSecure initAuth;
    private CreditCardData card;

    private MutableLiveData<List<String>> urlToWebView = new MutableLiveData<>();

    public LiveData<List<String>> getUrlToWebView() {
        return urlToWebView;
    }

    @Override
    protected void init() {
        GPAPIConfiguration gpapiConfiguration = new AppPreferences(getApplication()).getGPAPIConfiguration();
        GpApiConfig gpApiConfig = buildDefaultGpApiConfig(gpapiConfiguration);

        if (!configureService(gpApiConfig, gpapiConfiguration, TRANSACTION_OPERATIONS_GPAPI_CONFIG)) {
            Toast.makeText(getApplication(), R.string.configure_service_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void showResult(Transaction transaction) {
        hideProgress();
        transactionLiveData.setValue(transaction);
    }

    private void showResult3ds(List<String> urls) {
        hideProgress();
        urlToWebView.setValue(urls);
    }

    private void showTypeCard(ThreeDSecure typeMessage) {
        typeCardSelectedOption.setValue(typeMessage);
    }

    private void setCreditCardData(String cardNumber, int expiryMonth, int expiryYear, String cvnCvv) {
        card = new CreditCardData();
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

    public void executeTransactionOperationWith3ds(TransactionOperationModel model) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<List<String>>() {
            @Override
            public List<String> executeAsync() throws Exception {
                transactionOperationModel = model;
                return executeValidations3ds();
            }

            @Override
            public void onSuccess(List<String> value) {
                showTypeCard(secureEcom);
                showResult3ds(value);
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    private List<String> executeValidations3ds() throws ApiException {
        List<String> array = new ArrayList<>();
        setCreditCardData(transactionOperationModel.getCardNumber(),
                transactionOperationModel.getExpiryMonth(),
                transactionOperationModel.getExpiryYear(),
                transactionOperationModel.getCvnCvv());

        switch (transactionOperationModel.getTypeCardOption()) {
            case VISA_3DS2_FRICTIONLESS:
                checkEnrollment3Ds2(transactionOperationModel.getCurrency(),
                        transactionOperationModel.getAmount());

                initAuth = initiateAuthentication3DS2(transactionOperationModel.getAmount(),
                        transactionOperationModel.getCurrency());

                if (initAuth.getStatus().equals(SUCCESS_AUTHENTICATED) && initAuth.getLiabilityShift().equals("YES")) {
                    array.add(initAuth.getServerTransactionId());
                }
                break;
            case VISA_3DS1_ENROLLED:
                checkEnrollment3Ds1(transactionOperationModel.getCurrency(),
                        transactionOperationModel.getAmount());

                if (secureEcom.getStatus().equals(CHALLENGE_REQUIRED) && secureEcom.getLiabilityShift().equals("")) {
                    array.add(secureEcom.getIssuerAcsUrl());
                    array.add(secureEcom.getPayerAuthenticationRequest());
                    array.add(secureEcom.getChallengeReturnUrl());
                    array.add(secureEcom.getSessionDataFieldName());
                    array.add(secureEcom.getServerTransactionId());
                }
                break;
            case VISA_3DS1_NOT_ENROLLED:
                checkEnrollment3Ds1_Not_Enrolled(transactionOperationModel.getCurrency(),
                        transactionOperationModel.getAmount());

                if (secureEcom.getEnrolledStatus().equals(NOT_ENROLLED) && secureEcom.getLiabilityShift().equals("YES")) {
                    array.add("");
                }
                break;
            case VISA_3DS2_CHALLENGE:
                checkEnrollment3Ds2(transactionOperationModel.getCurrency(),
                        transactionOperationModel.getAmount());

                initAuth = initiateAuthentication3DS2(transactionOperationModel.getAmount(),
                        transactionOperationModel.getCurrency());

                if (initAuth.getStatus().equals(CHALLENGE_REQUIRED)) {
                    array.add(initAuth.getIssuerAcsUrl());
                    array.add(initAuth.getPayerAuthenticationRequest());
                }
                break;
        }
        return array;
    }

    public void executeFinishTransactionOperationWith3ds(String authenticationResponse) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<Transaction>() {
            @Override
            public Transaction executeAsync() throws Exception {
                return executeFinishTransfers(authenticationResponse);
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

    private Transaction executeFinishTransfers(String authenticationResponse) throws ApiException {
        switch (transactionOperationModel.getTypeCardOption()) {
            case VISA_3DS2_FRICTIONLESS:
                secureEcom = Secure3dService
                        .getAuthenticationData()
                        .withServerTransactionId(authenticationResponse)
                        .execute(Secure3dVersion.TWO, TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;
            case VISA_3DS1_ENROLLED:
                secureEcom = Secure3dService.getAuthenticationData()
                        .withServerTransactionId(secureEcom.getServerTransactionId())
                        .withPayerAuthenticationResponse(authenticationResponse)
                        .execute(Secure3dVersion.ONE, TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;
            case VISA_3DS1_NOT_ENROLLED:
                break;
            case VISA_3DS2_CHALLENGE:
                secureEcom = Secure3dService.getAuthenticationData()
                        .withServerTransactionId(initAuth.getServerTransactionId())
                        .execute(Secure3dVersion.TWO, TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;
        }
        if (!secureEcom.getStatus().equals(AUTHENTICATION_SUCCESSFUL)
                && !secureEcom.getLiabilityShift().equals("YES")) {
            showError(new ApiException("authenticationData error"));
        }
        card.setThreeDSecure(secureEcom);
        return executeTransaction();
    }

    private void checkEnrollment3Ds1(String currency, BigDecimal amount) throws ApiException {
        secureEcom = Secure3dService
                .checkEnrollment(card)
                .withCurrency(currency)
                .withAmount(amount)
                .execute(Secure3dVersion.ONE, TRANSACTION_OPERATIONS_GPAPI_CONFIG);
        if (!secureEcom.getEnrolledStatus().equals(ENROLLED) && !secureEcom.getStatus().equals(CHALLENGE_REQUIRED)) {
            showError(new Exception("checkEnrollment error"));
        }
    }

    private void checkEnrollment3Ds1_Not_Enrolled(String currency, BigDecimal amount) throws ApiException {
        secureEcom = Secure3dService
                .checkEnrollment(card)
                .withCurrency(currency)
                .withAmount(amount)
                .execute(Secure3dVersion.ONE, TRANSACTION_OPERATIONS_GPAPI_CONFIG);
        if (!secureEcom.getEnrolledStatus().equals(NOT_ENROLLED) && !secureEcom.getLiabilityShift().equals("YES")) {
            showError(new Exception("checkEnrollment error"));
        }
    }

    private void checkEnrollment3Ds2(String currency, BigDecimal amount) throws ApiException {
        secureEcom = Secure3dService
                .checkEnrollment(card)
                .withCurrency(currency)
                .withAmount(amount)
                .execute(Secure3dVersion.TWO, TRANSACTION_OPERATIONS_GPAPI_CONFIG);
        if (!secureEcom.getEnrolledStatus().equals(ENROLLED) && !secureEcom.getStatus().equals(AVAILABLE)) {
            showError(new Exception("checkEnrollment error"));
        }
    }

    private ThreeDSecure initiateAuthentication3DS2(BigDecimal amount, String currency) throws ApiException {
        // Browser data
        BrowserData browserData = new BrowserData();
        browserData.setAcceptHeader("text/html,application/xhtml+xml,application/xml;q=9,image/webp,img/apng,*/*;q=0.8");
        browserData.setColorDepth(ColorDepth.TwentyFourBit);
        browserData.setIpAddress("123.123.123.123");
        browserData.setJavaEnabled(true);
        browserData.setLanguage("en");
        browserData.setScreenHeight(1080);
        browserData.setScreenWidth(1920);
        browserData.setChallengeWindowSize(ChallengeWindowSize.Windowed_600x400);
        browserData.setTimezone("0");
        browserData.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64, x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");

        return Secure3dService
                .initiateAuthentication(card, secureEcom)
                .withAmount(amount)
                .withCurrency(currency)
                .withAuthenticationSource(AuthenticationSource.Browser)
                .withMethodUrlCompletion(MethodUrlCompletion.Yes)
                .withOrderCreateDate(DateTime.now())
                .withBrowserData(browserData)
                .execute(Secure3dVersion.TWO, TRANSACTION_OPERATIONS_GPAPI_CONFIG);
    }

    private Transaction executeTransaction() throws ApiException {
        Transaction transaction = null;

        switch (transactionOperationModel.getTransactionOperationType()) {

            case AUTHORIZATION:
                transaction = card.authorize(transactionOperationModel.getAmount())
                        .withCurrency(transactionOperationModel.getCurrency())
                        .withIdempotencyKey(transactionOperationModel.getIdempotencyKey())
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;

            case SALE:
                transaction = card.charge(transactionOperationModel.getAmount())
                        .withCurrency(transactionOperationModel.getCurrency())
                        .withIdempotencyKey(transactionOperationModel.getIdempotencyKey())
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;

            case CAPTURE:
                Transaction authorizationTransaction = card.authorize(transactionOperationModel.getAmount())
                        .withCurrency(transactionOperationModel.getCurrency())
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);

                transaction = authorizationTransaction.capture(transactionOperationModel.getAmount())
                        .withIdempotencyKey(transactionOperationModel.getIdempotencyKey())
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;

            case REFUND:
                Transaction saleTransaction = card.charge(transactionOperationModel.getAmount())
                        .withCurrency(transactionOperationModel.getCurrency())
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);

                transaction = saleTransaction.refund(transactionOperationModel.getAmount())
                        .withIdempotencyKey(transactionOperationModel.getIdempotencyKey())
                        .withCurrency(transactionOperationModel.getCurrency())
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;

            case REVERSE:
                Transaction saleTransaction2 = card.charge(transactionOperationModel.getAmount())
                        .withCurrency(transactionOperationModel.getCurrency())
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);

                transaction = saleTransaction2.reverse(transactionOperationModel.getAmount())
                        .withIdempotencyKey(transactionOperationModel.getIdempotencyKey())
                        .execute(TRANSACTION_OPERATIONS_GPAPI_CONFIG);
                break;
        }
        return transaction;
    }

}

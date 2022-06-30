package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations.model;


import com.global.api.entities.enums.PaymentMethodUsageMode;
import com.globalpayments.android.sdk.sample.utils.FingerprintMethodUsageMode;

public class PaymentMethodOperationModel {
    private Boolean fingerPrintSelection;
    private FingerprintMethodUsageMode fingerprintMethodUsageMode;
    private String cardHolderName;
    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private String cvnCvv;
    private String paymentMethodId;
    private PaymentMethodOperationType paymentMethodOperationType;
    private PaymentMethodUsageMode paymentMethodUsageMode;
    private String currency;

    public Boolean getFingerPrintSelection() {
        return fingerPrintSelection;
    }

    public void setFingerPrintSelection(Boolean fingerPrintSelection) {
        this.fingerPrintSelection = fingerPrintSelection;
    }

    public FingerprintMethodUsageMode getFingerprintMethodUsageMode() {
        return fingerprintMethodUsageMode;
    }

    public void setFingerprintMethodUsageMode(FingerprintMethodUsageMode fingerprintMethodUsageMode) {
        this.fingerprintMethodUsageMode = fingerprintMethodUsageMode;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getCvnCvv() {
        return cvnCvv;
    }

    public void setCvnCvv(String cvnCvv) {
        this.cvnCvv = cvnCvv;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public PaymentMethodOperationType getPaymentMethodOperationType() {
        return paymentMethodOperationType;
    }

    public void setPaymentMethodOperationType(PaymentMethodOperationType paymentMethodOperationType) {
        this.paymentMethodOperationType = paymentMethodOperationType;
    }

    public PaymentMethodUsageMode getPaymentMethodUsageMode() {
        return paymentMethodUsageMode;
    }

    public void setPaymentMethodUsageMode(PaymentMethodUsageMode paymentMethodUsageMode) {
        this.paymentMethodUsageMode = paymentMethodUsageMode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}

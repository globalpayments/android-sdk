package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations.model;

public class PaymentMethodOperationModel {
    private String cardNumber;
    private int expiryMonth;
    private int expiryYear;
    private String cvnCvv;
    private String paymentMethodId;
    private PaymentMethodOperationType paymentMethodOperationType;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(int expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(int expiryYear) {
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
}

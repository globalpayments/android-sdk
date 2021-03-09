package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations.model;

public class PaymentMethodOperationUIModel {
    private final static String SUCCESS = "Success";
    private final static String FAILURE = "Failure";

    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private String cardType;
    private String paymentMethodId;
    private String result;

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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(boolean isSuccessful) {
        this.result = isSuccessful ? SUCCESS : FAILURE;
    }
}

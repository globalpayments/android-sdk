package com.globalpayments.android.sdk.sample.gpapi.verifications.model;

public class VerificationsModel {
    private String cardNumber;
    private int expiryMonth;
    private int expiryYear;
    private String cvnCvv;
    private String currency;

    private String idempotencyKey;

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}

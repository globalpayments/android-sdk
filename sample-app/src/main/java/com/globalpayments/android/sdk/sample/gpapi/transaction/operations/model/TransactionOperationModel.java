package com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model;

import java.math.BigDecimal;

public class TransactionOperationModel {
    private String cardNumber;
    private int expiryMonth;
    private int expiryYear;
    private String cvnCvv;
    private BigDecimal amount;
    private String currency;
    private TransactionOperationType transactionOperationType;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public TransactionOperationType getTransactionOperationType() {
        return transactionOperationType;
    }

    public void setTransactionOperationType(TransactionOperationType transactionOperationType) {
        this.transactionOperationType = transactionOperationType;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}

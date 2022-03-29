package com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model;

import com.globalpayments.android.sdk.sample.utils.PaymentMethodUsageMode;

import java.math.BigDecimal;

public class TransactionOperationModel {
    private String typeCardOption;
    private String cardNumber;
    private int expiryMonth;
    private int expiryYear;
    private String cvnCvv;
    private BigDecimal amount;
    private String currency;
    private TransactionOperationType transactionOperationType;
    private PaymentMethodUsageMode paymentMethodUsageMode;
    private Boolean requestMultiUseToken;
    private String idempotencyKey;

    public String getTypeCardOption() {
        return typeCardOption;
    }

    public void setTypeCardOption(String typeCardOption) {
        this.typeCardOption = typeCardOption;
    }

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

    public PaymentMethodUsageMode getPaymentMethodUsageMode() {
        return paymentMethodUsageMode;
    }

    public void setPaymentMethodUsageMode(PaymentMethodUsageMode paymentMethodUsageMode) {
        this.paymentMethodUsageMode = paymentMethodUsageMode;
    }

    public Boolean getRequestMultiUseToken() {
        return requestMultiUseToken;
    }

    public void setRequestMultiUseToken(Boolean requestMultiUseToken) {
        this.requestMultiUseToken = requestMultiUseToken;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

}

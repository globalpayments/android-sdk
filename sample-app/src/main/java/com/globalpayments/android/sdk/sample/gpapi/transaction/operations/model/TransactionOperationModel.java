package com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model;

import com.globalpayments.android.sdk.sample.utils.FingerprintMethodUsageMode;
import com.globalpayments.android.sdk.sample.utils.ManualEntryMethodUsageMode;
import com.globalpayments.android.sdk.sample.utils.PaymentMethodUsageMode;

import java.math.BigDecimal;

public class TransactionOperationModel {
    private String cardNumber;
    private int expiryMonth;
    private int expiryYear;
    private String cvnCvv;
    private BigDecimal amount;
    private String currency;
    private String address;
    private TransactionOperationType transactionOperationType;
    private PaymentMethodUsageMode paymentMethodUsageMode;
    private Boolean requestMultiUseToken;
    private FingerprintMethodUsageMode fingerprintMethodUsageMode;
    private String idempotencyKey;
    private Boolean fingerPrintSelection;
    private String paymentLinkId;
    private String dynamicDescriptor;
    private ManualEntryMethodUsageMode manualEntryMethodUsageMode;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public FingerprintMethodUsageMode getFingerprintMethodUsageMode() {
        return fingerprintMethodUsageMode;
    }

    public void setFingerprintMethodUsageMode(FingerprintMethodUsageMode fingerprintMethodUsageMode) {
        this.fingerprintMethodUsageMode = fingerprintMethodUsageMode;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Boolean getFingerPrintSelection() {
        return fingerPrintSelection;
    }

    public void setFingerPrintSelection(Boolean fingerPrintSelection) {
        this.fingerPrintSelection = fingerPrintSelection;
    }

    public String getPaymentLinkId() {
        return paymentLinkId;
    }

    public void setPaymentLinkId(String paymentLinkId) {
        this.paymentLinkId = paymentLinkId;
    }

    public String getDynamicDescriptor() {
        return dynamicDescriptor;
    }

    public void setDynamicDescriptor(String dynamicDescriptor) {
        this.dynamicDescriptor = dynamicDescriptor;
    }

    public ManualEntryMethodUsageMode getManualEntryMethodUsageMode() {
        return manualEntryMethodUsageMode;
    }

    public void setManualEntryMethodUsageMode(ManualEntryMethodUsageMode manualEntryMethodUsageMode) {
        this.manualEntryMethodUsageMode = manualEntryMethodUsageMode;
    }
}

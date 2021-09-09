package com.globalpayments.android.sdk.sample.gpapi.transaction.report.model;

import com.global.api.entities.enums.Channel;
import com.global.api.entities.enums.DepositStatus;
import com.global.api.entities.enums.PaymentEntryMode;
import com.global.api.entities.enums.PaymentType;
import com.global.api.entities.enums.SortDirection;
import com.global.api.entities.enums.TransactionSortProperty;
import com.global.api.entities.enums.TransactionStatus;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionReportParameters {
    private boolean fromSettlements;

    //Common fields
    private int page = 1;
    private int pageSize = 5;
    private SortDirection order;
    private TransactionSortProperty orderBy;
    private String numberFirst6;
    private String numberLast4;
    private String brand;
    private String brandReference;
    private String authCode;
    private String depositReference;
    private TransactionStatus status;
    private Date fromTimeCreated;
    private Date toTimeCreated;

    // Non Settlements fields
    private String id;
    private PaymentType type;
    private Channel channel;
    private BigDecimal amount;
    private String currency;
    private String tokenFirst6;
    private String tokenLast4;
    private String accountName;
    private String country;
    private String batchId;
    private PaymentEntryMode entryMode;
    private String name;

    // Settlements fields
    private DepositStatus depositStatus;
    private String arn;
    private Date fromDepositTimeCreated;
    private Date toDepositTimeCreated;
    private Date fromBatchTimeCreated;
    private Date toBatchTimeCreated;
    private String systemMID;
    private String systemHierarchy;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public TransactionSortProperty getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(TransactionSortProperty orderBy) {
        this.orderBy = orderBy;
    }

    public SortDirection getOrder() {
        return order;
    }

    public void setOrder(SortDirection order) {
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
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

    public String getNumberFirst6() {
        return numberFirst6;
    }

    public void setNumberFirst6(String numberFirst6) {
        this.numberFirst6 = numberFirst6;
    }

    public String getNumberLast4() {
        return numberLast4;
    }

    public void setNumberLast4(String numberLast4) {
        this.numberLast4 = numberLast4;
    }

    public String getTokenFirst6() {
        return tokenFirst6;
    }

    public void setTokenFirst6(String tokenFirst6) {
        this.tokenFirst6 = tokenFirst6;
    }

    public String getTokenLast4() {
        return tokenLast4;
    }

    public void setTokenLast4(String tokenLast4) {
        this.tokenLast4 = tokenLast4;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBrandReference() {
        return brandReference;
    }

    public void setBrandReference(String brandReference) {
        this.brandReference = brandReference;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getDepositReference() {
        return depositReference;
    }

    public void setDepositReference(String depositReference) {
        this.depositReference = depositReference;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Date getFromTimeCreated() {
        return fromTimeCreated;
    }

    public void setFromTimeCreated(Date fromTimeCreated) {
        this.fromTimeCreated = fromTimeCreated;
    }

    public Date getToTimeCreated() {
        return toTimeCreated;
    }

    public void setToTimeCreated(Date toTimeCreated) {
        this.toTimeCreated = toTimeCreated;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public PaymentEntryMode getEntryMode() {
        return entryMode;
    }

    public void setEntryMode(PaymentEntryMode entryMode) {
        this.entryMode = entryMode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFromSettlements() {
        return fromSettlements;
    }

    public void setFromSettlements(boolean fromSettlements) {
        this.fromSettlements = fromSettlements;
    }

    public DepositStatus getDepositStatus() {
        return depositStatus;
    }

    public void setDepositStatus(DepositStatus depositStatus) {
        this.depositStatus = depositStatus;
    }

    public String getArn() {
        return arn;
    }

    public void setArn(String arn) {
        this.arn = arn;
    }

    public Date getFromDepositTimeCreated() {
        return fromDepositTimeCreated;
    }

    public void setFromDepositTimeCreated(Date fromDepositTimeCreated) {
        this.fromDepositTimeCreated = fromDepositTimeCreated;
    }

    public Date getToDepositTimeCreated() {
        return toDepositTimeCreated;
    }

    public void setToDepositTimeCreated(Date toDepositTimeCreated) {
        this.toDepositTimeCreated = toDepositTimeCreated;
    }

    public Date getFromBatchTimeCreated() {
        return fromBatchTimeCreated;
    }

    public void setFromBatchTimeCreated(Date fromBatchTimeCreated) {
        this.fromBatchTimeCreated = fromBatchTimeCreated;
    }

    public Date getToBatchTimeCreated() {
        return toBatchTimeCreated;
    }

    public void setToBatchTimeCreated(Date toBatchTimeCreated) {
        this.toBatchTimeCreated = toBatchTimeCreated;
    }

    public String getSystemMID() {
        return systemMID;
    }

    public void setSystemMID(String systemMID) {
        this.systemMID = systemMID;
    }

    public String getSystemHierarchy() {
        return systemHierarchy;
    }

    public void setSystemHierarchy(String systemHierarchy) {
        this.systemHierarchy = systemHierarchy;
    }
}

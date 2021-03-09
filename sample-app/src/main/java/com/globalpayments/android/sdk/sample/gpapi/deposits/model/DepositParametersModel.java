package com.globalpayments.android.sdk.sample.gpapi.deposits.model;

import com.global.api.entities.enums.DepositSortProperty;
import com.global.api.entities.enums.DepositStatus;
import com.global.api.entities.enums.SortDirection;

import java.math.BigDecimal;
import java.util.Date;

public class DepositParametersModel {
    private int page = 1;
    private int pageSize = 5;
    private DepositSortProperty orderBy;
    private SortDirection order;
    private Date fromTimeCreated;
    private String id;
    private DepositStatus status;
    private Date toTimeCreated;
    private BigDecimal amount;
    private String maskedAccountNumberLast4;
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

    public DepositSortProperty getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(DepositSortProperty orderBy) {
        this.orderBy = orderBy;
    }

    public SortDirection getOrder() {
        return order;
    }

    public void setOrder(SortDirection order) {
        this.order = order;
    }

    public Date getFromTimeCreated() {
        return fromTimeCreated;
    }

    public void setFromTimeCreated(Date fromTimeCreated) {
        this.fromTimeCreated = fromTimeCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DepositStatus getStatus() {
        return status;
    }

    public void setStatus(DepositStatus status) {
        this.status = status;
    }

    public Date getToTimeCreated() {
        return toTimeCreated;
    }

    public void setToTimeCreated(Date toTimeCreated) {
        this.toTimeCreated = toTimeCreated;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMaskedAccountNumberLast4() {
        return maskedAccountNumberLast4;
    }

    public void setMaskedAccountNumberLast4(String maskedAccountNumberLast4) {
        this.maskedAccountNumberLast4 = maskedAccountNumberLast4;
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

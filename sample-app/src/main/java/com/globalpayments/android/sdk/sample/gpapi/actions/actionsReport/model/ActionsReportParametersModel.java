package com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport.model;

import com.global.api.entities.enums.SortDirection;

import java.util.Date;

public class ActionsReportParametersModel {
    private int page = 1;
    private int pageSize = 5;
    private SortDirection order;
    private SortDirection orderBy;
    private String type;
    private String resource;
    private String resourceStatus;
    private String resourceId;
    private Date fromTimeCreated;
    private Date toTimeCreated;
    private String merchantName;
    private String accountName;
    private String appName;
    private String version;

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

    public SortDirection getOrder() {
        return order;
    }

    public void setOrder(SortDirection order) {
        this.order = order;
    }

    public SortDirection getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SortDirection orderBy) {
        this.orderBy = orderBy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getResourceStatus() {
        return resourceStatus;
    }

    public void setResourceStatus(String resourceStatus) {
        this.resourceStatus = resourceStatus;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
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

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

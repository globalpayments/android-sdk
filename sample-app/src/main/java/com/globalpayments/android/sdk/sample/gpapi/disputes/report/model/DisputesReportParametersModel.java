package com.globalpayments.android.sdk.sample.gpapi.disputes.report.model;

import com.global.api.entities.enums.DisputeSortProperty;
import com.global.api.entities.enums.DisputeStage;
import com.global.api.entities.enums.DisputeStatus;
import com.global.api.entities.enums.SortDirection;

import java.util.Date;

public class DisputesReportParametersModel {
    private int page = 1;
    private int pageSize = 5;
    private DisputeSortProperty orderBy;
    private SortDirection order;
    private String arn;
    private String brand;
    private DisputeStatus status;
    private DisputeStage stage;
    private Date fromStageTimeCreated;
    private Date toStageTimeCreated;
    private String systemMID;
    private String systemHierarchy;

    private boolean fromSettlements;

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

    public DisputeSortProperty getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(DisputeSortProperty orderBy) {
        this.orderBy = orderBy;
    }

    public SortDirection getOrder() {
        return order;
    }

    public void setOrder(SortDirection order) {
        this.order = order;
    }

    public String getArn() {
        return arn;
    }

    public void setArn(String arn) {
        this.arn = arn;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public DisputeStatus getStatus() {
        return status;
    }

    public void setStatus(DisputeStatus status) {
        this.status = status;
    }

    public DisputeStage getStage() {
        return stage;
    }

    public void setStage(DisputeStage stage) {
        this.stage = stage;
    }

    public Date getFromStageTimeCreated() {
        return fromStageTimeCreated;
    }

    public void setFromStageTimeCreated(Date fromStageTimeCreated) {
        this.fromStageTimeCreated = fromStageTimeCreated;
    }

    public Date getToStageTimeCreated() {
        return toStageTimeCreated;
    }

    public void setToStageTimeCreated(Date toStageTimeCreated) {
        this.toStageTimeCreated = toStageTimeCreated;
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

    public boolean isFromSettlements() {
        return fromSettlements;
    }

    public void setFromSettlements(boolean fromSettlements) {
        this.fromSettlements = fromSettlements;
    }
}

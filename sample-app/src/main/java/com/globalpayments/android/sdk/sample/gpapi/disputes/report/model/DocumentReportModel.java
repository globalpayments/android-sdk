package com.globalpayments.android.sdk.sample.gpapi.disputes.report.model;

public class DocumentReportModel {
    private String disputeId;
    private String documentId;

    public String getDisputeId() {
        return disputeId;
    }

    public void setDisputeId(String disputeId) {
        this.disputeId = disputeId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}

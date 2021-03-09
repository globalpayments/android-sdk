package com.globalpayments.android.sdk.sample.gpapi.disputes.report.model;

public class DocumentContent {
    private String documentId;
    private String base64Content;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getBase64Content() {
        return base64Content;
    }

    public void setBase64Content(String base64Content) {
        this.base64Content = base64Content;
    }
}

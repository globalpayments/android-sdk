package com.globalpayments.android.sdk.sample.gpapi.disputes.operations.model;

import java.util.List;

public class DisputeOperationModel {
    private DisputeOperationType disputeOperationType;
    private String disputeId;
    private List<DisputeDocument> disputeDocumentList;
    private String idempotencyKey;

    public DisputeOperationType getDisputeOperationType() {
        return disputeOperationType;
    }

    public void setDisputeOperationType(DisputeOperationType disputeOperationType) {
        this.disputeOperationType = disputeOperationType;
    }

    public String getDisputeId() {
        return disputeId;
    }

    public void setDisputeId(String disputeId) {
        this.disputeId = disputeId;
    }

    public List<DisputeDocument> getDisputeDocumentList() {
        return disputeDocumentList;
    }

    public void setDisputeDocumentList(List<DisputeDocument> disputeDocumentList) {
        this.disputeDocumentList = disputeDocumentList;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}

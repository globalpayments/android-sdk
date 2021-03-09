package com.globalpayments.android.sdk.sample.gpapi.disputes.operations.model;

import android.net.Uri;

import static com.globalpayments.android.sdk.utils.Strings.EMPTY;

public class DisputeDocument {
    private String filename = EMPTY;
    private Type type;
    private Uri uri;

    public DisputeDocument(String filename, Type type, Uri uri) {
        this.filename = filename;
        this.type = type;
        this.uri = uri;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public enum Type {
        SALES_RECEIPT,
        PROOF_OF_DELIVERY,
        REFUND_POLICY,
        TERMS_AND_CONDITIONS,
        CANCELLATION_POLICY,
        OTHER;
    }
}
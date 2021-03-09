package com.globalpayments.android.sdk.model;

public enum DisputeDocumentMimeType {
    GIF("image/gif"),
    TIFF("image/tiff"),
    PDF("application/pdf"),
    JPEG("image/jpeg"),
    PNG("image/png");

    private final String mimeType;

    DisputeDocumentMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static String[] getMimeTypes() {
        DisputeDocumentMimeType[] disputeDocumentMimeTypes = values();
        String[] mimeTypes = new String[disputeDocumentMimeTypes.length];

        for (int i = 0; i < disputeDocumentMimeTypes.length; i++) {
            mimeTypes[i] = disputeDocumentMimeTypes[i].mimeType;
        }

        return mimeTypes;
    }
}

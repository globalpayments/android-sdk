package com.globalpayments.android.sdk.model;

public enum PaymentCardModel {
    VISA_SUCCESSFUL("4263970000005262", "5", "2025", "852"),
    MASTERCARD_SUCCESSFUL("5425230000004415", "5", "2025", "852"),
    AMEX_SUCCESSFUL("374101000000608", "5", "2025", "8522"),
    VISA_DECLINED("4000120000001154", "5", "2025", "852"),
    MASTERCARD_DECLINED("5114610000004778", "5", "2025", "852"),
    AMEX_DECLINED("376525000000010", "5", "2025", "8522"),
    VISA_3DS2_FRICTIONLESS("4263970000005262", "5", "2025", "852"),
    VISA_3DS2_CHALLENGE("4012001038488884", "5", "2025", "852"),
    VISA_3DS1_NOT_ENROLLED("4012001038443335", "5", "2025", "852"),
    VISA_3DS1_ENROLLED("4012001037141112", "5", "2025", "852");

    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private String cvnCvv;

    PaymentCardModel(String cardNumber, String expiryMonth, String expiryYear, String cvnCvv) {
        this.cardNumber = cardNumber;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.cvnCvv = cvnCvv;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public String getCvnCvv() {
        return cvnCvv;
    }
}

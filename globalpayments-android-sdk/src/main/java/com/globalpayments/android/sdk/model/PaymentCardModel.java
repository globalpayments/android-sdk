package com.globalpayments.android.sdk.model;

public enum PaymentCardModel {
    VISA_SUCCESSFUL("4263970000005262", "5", "2025", "852", "VISA_SUCCESSFUL"),
    MASTERCARD_SUCCESSFUL("5425230000004415", "5", "2025", "852", "MASTERCARD_SUCCESSFUL"),
    AMEX_SUCCESSFUL("374101000000608", "5", "2025", "8522", "AMEX_SUCCESSFUL"),
    VISA_DECLINED("4000120000001154", "5", "2025", "852", "VISA_DECLINED"),
    MASTERCARD_DECLINED("5114610000004778", "5", "2025", "852", "MASTERCARD_DECLINED"),
    AMEX_DECLINED("376525000000010", "5", "2025", "8522", "AMEX_DECLINED"),
    VISA_3DS2_FRICTIONLESS("4263970000005262", "12", "2025", "852", "VISA_3DS2_FRICTIONLESS"),
    VISA_3DS2_CHALLENGE("4012001038488884", "5", "2025", "852", "VISA_3DS2_CHALLENGE"),
    VISA_3DS1_NOT_ENROLLED("4917000000000087", "12", "2025", "852", "VISA_3DS1_NOT_ENROLLED"),
    VISA_3DS1_ENROLLED("4012001037141112", "5", "2025", "852", "VISA_3DS1_ENROLLED");

    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private String cvnCvv;
    private String typeCardOption;

    PaymentCardModel(String cardNumber, String expiryMonth, String expiryYear, String cvnCvv,
                        String typeCardOption) {
        this.cardNumber = cardNumber;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.cvnCvv = cvnCvv;
        this.typeCardOption = typeCardOption;
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

    public String getTypeCardOption() {
        return typeCardOption;
    }
}

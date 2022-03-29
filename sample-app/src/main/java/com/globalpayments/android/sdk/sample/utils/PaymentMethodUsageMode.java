package com.globalpayments.android.sdk.sample.utils;

import com.global.api.entities.enums.IStringConstant;

public enum PaymentMethodUsageMode implements IStringConstant {
    No("NO"),
    Single_use_token("SINGLE"),
    Multiple_use_token("MULTIPLE");

    String value;
    PaymentMethodUsageMode(String value) {
        this.value = value;
    }
    public String getValue() { return this.value; }
    public byte[] getBytes() { return this.value.getBytes(); }
}

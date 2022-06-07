package com.globalpayments.android.sdk.sample.utils;

import static com.globalpayments.android.sdk.sample.common.Constants.FP_ALWAYS;
import static com.globalpayments.android.sdk.sample.common.Constants.FP_ON_SUCCESS;

import com.global.api.entities.Customer;

public class FingerPrintUsageMethod {

    public static Customer fingerPrintSelectedOption(FingerprintMethodUsageMode fingerprintMethodUsageMode) {
        Customer customer = new Customer();

        switch (fingerprintMethodUsageMode) {
            case ALWAYS:
                customer.setDeviceFingerPrint(FP_ALWAYS);
                break;
            case ON_SUCCESS:
                customer.setDeviceFingerPrint(FP_ON_SUCCESS);
                break;
        }
        return customer;
    }
}

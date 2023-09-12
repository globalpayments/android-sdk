package com.globalpayments.android.sdk.sample.utils

import com.global.api.entities.Customer
import com.globalpayments.android.sdk.sample.common.Constants

object FingerPrintUsageMethod {
    @JvmStatic
    fun fingerPrintSelectedOption(fingerprintMethodUsageMode: FingerprintMethodUsageMode?): Customer {
        val customer = Customer()
        if (fingerprintMethodUsageMode != null) {
            when (fingerprintMethodUsageMode) {
                FingerprintMethodUsageMode.ALWAYS -> customer.deviceFingerPrint = Constants.FP_ALWAYS
                FingerprintMethodUsageMode.ON_SUCCESS -> customer.deviceFingerPrint = Constants.FP_ON_SUCCESS
            }
        }
        return customer
    }
}

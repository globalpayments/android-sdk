package com.globalpayments.android.sdk.sample.gpapi.utils

import com.global.api.entities.Address

fun createEmptyAddress(): Address {
    return Address().apply {
        streetAddress1 = "10 Glenlake Pkwy NE"
        streetAddress2 = "Complex 741"
        streetAddress3 = "no"
        city = "Birmingham"
        postalCode = "50001"
        countryCode = "US"
        state = "IL"
    }
}

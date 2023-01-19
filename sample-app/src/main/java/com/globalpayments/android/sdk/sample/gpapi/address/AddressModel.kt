package com.globalpayments.android.sdk.sample.gpapi.address

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressModel(
    val streetAddress1: String,
    val streetAddress2: String,
    val streetAddress3: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val countryCode: String
) : Parcelable{
    override fun toString(): String {
        return "$streetAddress1|$city|$state"
    }
}

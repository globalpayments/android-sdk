package com.globalpayments.android.sdk.sample.gpapi.openbanking.models

sealed interface PaymentDetails {
    data class SepaDetails(
        val accountName: String = "",
        val iban: String = ""
    ) : PaymentDetails

    data class FasterPaymentDetails(
        val accountNumber: String = "",
        val accountName: String = "",
        val sortCode: String = ""
    ) : PaymentDetails
}


package com.globalpayments.android.sdk.model

data class DccRateModel(
    val payerCurrency: String,
    val merchantCurrency: String,
    val payerAmount: String,
    val merchantAmount: String,
    val exchangeRate: String,
    val marginRatePercentage: String,
    val commissionPercentage: String,
    val exchangeRateSource: String
)

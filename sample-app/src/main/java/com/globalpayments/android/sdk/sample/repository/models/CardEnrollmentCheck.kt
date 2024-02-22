package com.globalpayments.android.sdk.sample.repository.models

data class CardEnrollmentCheck(
    val isCardEnrolled: Boolean,
    val messageVersion: String,
    val cardBrand: String
)

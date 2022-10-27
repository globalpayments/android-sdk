package com.globalpatments.android.sdk.merchant3ds.networking.models.request

@kotlinx.serialization.Serializable
data class CheckEnrollmentRequest(
    val cardToken: String,
    val amount: String,
    val currency: String
)

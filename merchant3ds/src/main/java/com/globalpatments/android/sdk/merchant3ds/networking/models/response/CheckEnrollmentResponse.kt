package com.globalpatments.android.sdk.merchant3ds.networking.models.response

@kotlinx.serialization.Serializable
data class CheckEnrollmentResponse(
    val enrolled: String,
    val version: String,
    val messageVersion: String,
    val status: String,
    val liabilityShift: String,
    val serverTransactionId: String,
    val sessionDataFieldName: String,
    val methodUrl: String,
    val methodData: String?,
    val messageType: String
)

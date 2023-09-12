package com.globalpayments.android.sdk.merchant3ds.networking.models.response

@kotlinx.serialization.Serializable
data class AuthorizationDataResponse(
    val transactionId: String,
    val status: String,
    val amount: String,
    val date: String,
    val reference: String,
    val batchId: String,
    val responseCode: String,
    val responseMessage: String
)

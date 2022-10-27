package com.globalpatments.android.sdk.merchant3ds.networking.models.response

@kotlinx.serialization.Serializable
data class GetAuthenticationDataResponse(
    val status: String,
    val liabilityShift: String,
    val serverTransactionId: String
)
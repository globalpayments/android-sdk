package com.globalpatments.android.sdk.merchant3ds.networking.models.request

@kotlinx.serialization.Serializable
data class GetAuthenticationDataRequest(
    val serverTransactionId: String
)

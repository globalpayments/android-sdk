package com.globalpayments.android.sdk.merchant3ds.networking.models.request

@kotlinx.serialization.Serializable
data class AccessTokenRequest(
    val appId: String,
    val appKey: String,
    val permissions: Array<String>
)

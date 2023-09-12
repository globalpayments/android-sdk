package com.globalpayments.android.sdk.merchant3ds.networking.models.response

@kotlinx.serialization.Serializable
data class EphemeralPublicKey(
    val x: String,
    val y: String
)

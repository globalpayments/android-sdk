package com.globalpayments.android.sdk.merchant3ds.networking.models.response

@kotlinx.serialization.Serializable
data class InitiateAuthenticationResponse(
    val enrolled: String?,
    val version: String,
    val status: String,
    val serverTransactionId: String,
    val dsTransferReference: String,
    val liabilityShift: String,
    val acsTransactionId: String,
    val acsReferenceNumber: String,
    val methodUrl: String?,
    val payerAuthenticationRequest: String?,
    val sessionDataFieldName: String?,
    val messageVersion: String,
    val authenticationValue: String,
    val eci: String?,
    val challengeMandated: String?,
    val challenge: String?
)

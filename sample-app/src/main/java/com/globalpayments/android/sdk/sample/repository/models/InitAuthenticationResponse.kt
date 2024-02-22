package com.globalpayments.android.sdk.sample.repository.models

data class InitAuthenticationResponse(
    val isChallengeRequired: Boolean,
    val serverTransactionId: String,
    val acsReferenceNumber: String,
    val payerAuthenticationRequest: String,
    val acsTransactionId: String,
    val providerServerTransRef: String
)

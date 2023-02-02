package com.globalpatments.android.sdk.merchant3ds.screens.processing

data class ProcessingScreenModel(
    val transactionModel: TransactionModel = TransactionModel(),
    val startChallenge: Boolean = false,
    val error: String = "",
    val isLoading: Boolean = false,
    val isWaitingForAuth: Boolean = false,
)

data class TransactionModel(
    val messageVersion: String = "",
    val serverTransactionId: String = "",
    val acsReferenceNumber: String = "",
    val payerAuthenticationRequest: String = "",
    val acsTransactionId: String = "",
    val providerServerTransRef: String = ""
)

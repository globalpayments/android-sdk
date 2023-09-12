package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.disputes

import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

data class DisputesScreenModel(
    val operationType: DisputeOperationType = DisputeOperationType.ACCEPT,
    val disputeId: String = "DIS_SAND_abcd1234",
    val idempotencyKey: String = "",
    val files: List<DisputeDocumentModel> = emptyList(),
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

data class DisputeDocumentModel(
    val fileEncoded: String,
    val fileType: DisputeDocumentType,
    val fileName: String
)

enum class DisputeDocumentType {
    SALES_RECEIPT,
    PROOF_OF_DELIVERY,
    REFUND_POLICY,
    TERMS_AND_CONDITIONS,
    CANCELLATION_POLICY,
    OTHER
}

package com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model

enum class TransactionOperationType(val parent: TransactionOperationType? = null) {
    Authorization,
        Capture(Authorization),
            RefundCapture(Capture),
            ReverseCapture(Capture),
        HoldAuthorization(Authorization),
            ReleaseHoldAuthorization(HoldAuthorization),
            ReverseHoldAuthorization(HoldAuthorization),
        PendingReviewAuthorization(Authorization),
            HoldPendingReviewAuthorization(PendingReviewAuthorization),
            CapturePendingReviewAuthorization(PendingReviewAuthorization),
        ReverseAuthorization(Authorization),
            Reauthorize(ReverseAuthorization),
        Increment(Authorization),
            CaptureIncrement(Increment),
            ReverseIncrement(Increment),
    Sale,
        RefundSale(Sale),
        ReverseSale(Sale),
        HoldSale(Sale),
            ReleaseHoldSale(HoldSale),
        PendingReviewSale(Sale),
            HoldPendingSale(PendingReviewSale),
    Refund,
        ReverseRefund(Refund);

    val children = mutableListOf<TransactionOperationType>()

    init {
        if (this.parent != null) {
            this.parent.children.add(this)
        }
    }

    companion object {
        @JvmStatic
        fun getRoots(): List<TransactionOperationType> = values().filter { it.parent == null }
    }
}

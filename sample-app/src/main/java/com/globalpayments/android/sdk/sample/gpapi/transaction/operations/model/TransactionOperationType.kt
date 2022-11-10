package com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model

import java.util.*

enum class TransactionOperationType(val parent: TransactionOperationType? = null) {
    Authorization,
        Capture(Authorization),
            RefundCapture(Capture),
            ReverseCapture(Capture),
        ReverseAuthorization(Authorization),
            Reauthorize(ReverseAuthorization),
        Increment(Authorization),
            CaptureIncrement(Increment),
            ReverseIncrement(Increment),
    Sale,
        RefundSale(Sale),
        ReverseSale(Sale),
    Refund,
        ReverseRefund(Refund);

    val children = LinkedList<TransactionOperationType>()

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

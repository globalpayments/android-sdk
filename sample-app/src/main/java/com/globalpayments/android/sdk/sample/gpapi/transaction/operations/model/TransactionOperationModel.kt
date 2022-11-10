package com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model

import com.globalpayments.android.sdk.sample.utils.FingerprintMethodUsageMode
import com.globalpayments.android.sdk.sample.utils.ManualEntryMethodUsageMode
import com.globalpayments.android.sdk.sample.utils.PaymentMethodUsageMode
import java.math.BigDecimal

data class TransactionOperationModel(
    val cardNumber: String,
    val expiryMonth: Int = 0,
    val expiryYear: Int = 0,
    val cvnCvv: String,
    val amount: BigDecimal,
    val currency: String,
    val transactionOperationType: TransactionOperationType,
    val paymentMethodUsageMode: PaymentMethodUsageMode,
    val requestMultiUseToken: Boolean,
    val fingerprintMethodUsageMode: FingerprintMethodUsageMode? = null,
    val idempotencyKey: String? = null,
    val fingerPrintSelection: Boolean,
    val paymentLinkId: String? = null,
    val dynamicDescriptor: String? = null,
    val manualEntryMethodUsageMode: ManualEntryMethodUsageMode?,
)

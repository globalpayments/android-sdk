package com.globalpayments.android.sdk.sample.gpapi.paylink

import com.global.api.entities.enums.PaymentMethodUsageMode
import org.joda.time.DateTime
import java.math.BigDecimal

data class PaylinkDataModel(
    val usageMode: PaymentMethodUsageMode,
    val usageLimit: Int,
    val name: String,
    val amount: BigDecimal,
    val expirationDate: DateTime
)

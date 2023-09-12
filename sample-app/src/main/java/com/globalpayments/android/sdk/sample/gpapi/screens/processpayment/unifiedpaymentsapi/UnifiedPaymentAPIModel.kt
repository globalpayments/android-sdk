package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.unifiedpaymentsapi

import com.global.api.entities.ThreeDSecure
import com.global.api.paymentMethods.CreditCardData
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.netcetera.threeds.sdk.api.transaction.Transaction
import java.math.BigDecimal

data class UnifiedPaymentAPIModel(
    val cardNumber: String = "",
    val cardMonth: String = "",
    val cardYear: String = "",
    val cardCVV: String = "",
    val cardHolderName: String = "",
    val amount: String = "",

    val makePaymentRecurring: Boolean = false,

    val netceteraTransactionParams: NetceteraParams? = null,

    val gpSampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

data class NetceteraParams(
    val netceteraTransaction: Transaction,
    val threeDSecure: ThreeDSecure,
    val tokenizedCard: CreditCardData,
    val amount: BigDecimal
)

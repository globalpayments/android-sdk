package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.unifiedpaymentsapi

import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.repository.models.InitAuthenticationResponse
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
    val initAuthResponse: InitAuthenticationResponse,
    val cardToken: String,
    val amount: BigDecimal
)

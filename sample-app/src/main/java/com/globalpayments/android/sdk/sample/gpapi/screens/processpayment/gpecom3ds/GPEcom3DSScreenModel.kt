package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.gpecom3ds

import com.global.api.entities.ThreeDSecure
import com.global.api.paymentMethods.RecurringPaymentMethod
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.netcetera.threeds.sdk.api.transaction.Transaction
import java.math.BigDecimal

data class GPEcom3DSScreenModel(
    val cardNumber: String = "",
    val cardMonth: String = "",
    val cardYear: String = "",
    val cardCVV: String = "",
    val cardHolderName: String = "",
    val amount: String = "",

    val makePaymentRecurring: Boolean = false,

    val netceteraTransactionParams: NetceteraEcomParams? = null,

    val gpSampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

data class NetceteraEcomParams(
    val netceteraTransaction: Transaction,
    val initAuthResponse: ThreeDSecure,
    val recurringPaymentMethod: RecurringPaymentMethod,
    val amount: BigDecimal
)

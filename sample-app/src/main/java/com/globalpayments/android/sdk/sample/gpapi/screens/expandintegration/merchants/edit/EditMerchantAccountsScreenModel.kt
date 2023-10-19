package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.merchants.edit

import com.global.api.entities.Address
import com.globalpayments.android.sdk.model.PaymentCardModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.createEmptyAddress
import java.util.Date

data class EditMerchantAccountsScreenModel(


    val cardNumber: String = PaymentCardModel.VISA_SUCCESSFUL.cardNumber,
    val expiryMonth: String = PaymentCardModel.VISA_SUCCESSFUL.expiryMonth,
    val expiryYear: String = PaymentCardModel.VISA_SUCCESSFUL.expiryYear,
    val cvn: String = PaymentCardModel.VISA_SUCCESSFUL.cvnCvv,
    val cardHolderName: String = "",
    val fromTimeCreated: Date? = null,
    val toTimeCreated: Date? = null,
    val billingAddress: Address = createEmptyAddress(),

    val showBillingAddressDialog: Boolean = false,

    val sampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.payers.createpayment

import com.globalpayments.android.sdk.model.PaymentCardModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import java.util.UUID

data class CreatePaymentScreenModel (
    //Customer
    val customerId: String = "PYR_",
    val customerFirstName: String = "James",
    val customerLastName: String = "Mason",

    //Address

    //Card
    val paymentId: String = "PMT_${UUID.randomUUID()}",
    val paymentCard: PaymentCardModel = PaymentCardModel.VISA_SUCCESSFUL,
    val cardNumber: String = PaymentCardModel.VISA_SUCCESSFUL.cardNumber,
    val expiryMonth: String = PaymentCardModel.VISA_SUCCESSFUL.expiryMonth,
    val expiryYear: String = PaymentCardModel.VISA_SUCCESSFUL.expiryYear,
    val cvn: String = PaymentCardModel.VISA_SUCCESSFUL.cvnCvv,
    val currency: String = "USD",

    val sampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

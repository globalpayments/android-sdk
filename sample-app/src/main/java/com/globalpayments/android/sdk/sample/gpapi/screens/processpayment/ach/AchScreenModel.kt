package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.ach

import com.global.api.entities.enums.AccountType
import com.global.api.entities.enums.SecCode
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

enum class PaymentType {
    Charge, Refund
}

data class AchScreenModel(
    val amount: String = "",
    val accountHolderName: String = "",
    val accountType: AccountType = AccountType.Savings,
    val secCode: SecCode = SecCode.Web,
    val routingNumber: String = "",
    val accountNumber: String = "",
    val customerFirstName: String = "",
    val customerLastName: String = "",
    val customerBirthDate: String? = null,
    val customerMobilePhone: String = "",
    val customerHomePhone: String = "",
    val billingAddressLine1: String = "",
    val billingAddressLine2: String = "",
    val billingAddressCity: String = "",
    val billingAddressState: String = "",
    val billingAddressPostalCode: String = "",
    val billingAddressCountry: String = "",

    val gpSampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

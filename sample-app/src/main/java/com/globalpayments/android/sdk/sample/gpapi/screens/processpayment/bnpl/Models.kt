package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.bnpl

import com.global.api.entities.Address
import com.global.api.entities.Customer
import com.global.api.entities.CustomerDocument
import com.global.api.entities.PhoneNumber
import com.global.api.entities.Product
import com.global.api.entities.Transaction
import com.global.api.entities.enums.BNPLShippingMethod
import com.global.api.entities.enums.BNPLType
import com.global.api.entities.enums.CustomerDocumentType
import com.global.api.entities.enums.PhoneNumberType
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

data class BnplScreenModel(
    val screenState: ScreenState = ScreenState.Request,

    val bnplRequestModel: BnplRequestModel = BnplRequestModel(),
    val bnplRefundScreenModel: BnplRefundScreenModel = BnplRefundScreenModel(),

    val urlToOpen: String = "",
    val transaction: Transaction? = null,

    val showCustomerDataDialog: Boolean = false,
    val showProductsDialog: Boolean = false,
    val showBillingAddressDialog: Boolean = false,
    val showShippingAddressDialog: Boolean = false,
    val gpSampleResponseModel: GPSampleResponseModel? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

data class BnplRequestModel(
    val authorize: String = "",
    val currency: String = "USD",
    val bnplType: BNPLType = BNPLType.AFFIRM,
    val products: List<Product> = emptyList(),
    val shippingAddress: Address = createEmptyAddress(),
    val billingAddress: Address = createEmptyAddress(),
    val phoneNumber: PhoneNumberModel = PhoneNumberModel(),
    val customerData: Customer = createEmptyCustomer(),
    val bnplShippingMethod: BNPLShippingMethod = BNPLShippingMethod.DELIVERY,
)

data class BnplRefundScreenModel(
    val amount: String = ""
)

data class PhoneNumberModel(
    val countryCode: String = "1", val number: String = "7708298000", val phoneNumberType: PhoneNumberType = PhoneNumberType.Shipping
)


sealed interface ScreenState {
    data object Request : ScreenState
    data object CaptureRefund : ScreenState
    data object Refund : ScreenState
    data object Reset : ScreenState
}

private fun createEmptyAddress(): Address {
    return Address().apply {
        streetAddress1 = "10 Glenlake Pkwy NE"
        streetAddress2 = "Complex 741"
        streetAddress3 = "no"
        city = "Birmingham"
        postalCode = "50001"
        countryCode = "US"
        state = "IL"
    }
}

private fun createEmptyCustomer(): Customer {
    return Customer().apply {
        id = "12345678"
        firstName = "James"
        lastName = "Mason"
        email = "james.mason@example.com"
        phone = PhoneNumber().apply {
            countryCode = "1"
            number = "7708298000"
            areaCode = PhoneNumberType.Home.toString()
        }
        documents = listOf(CustomerDocument().apply {
            reference = "123456789"
            issuer = "US"
            type = CustomerDocumentType.PASSPORT
        })
    }
}

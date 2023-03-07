package com.globalpayments.android.sdk.sample.gpapi.bnpl

import com.global.api.entities.*
import com.global.api.entities.enums.BNPLShippingMethod
import com.global.api.entities.enums.BNPLType
import com.global.api.entities.enums.CustomerDocumentType
import com.global.api.entities.enums.PhoneNumberType

data class BnplScreenModel(
    val currentScreen: ScreenState = ScreenState.General,
    val bnplGeneralScreenModel: BnplGeneralScreenModel = BnplGeneralScreenModel(),
    val bnplRefundScreenModel: BnplRefundScreenModel = BnplRefundScreenModel(),
    val showTransactionSuccessDialog: Boolean = false,
    val urlToOpen: String = "",
    val transaction: Transaction? = null,
    val isLoading: Boolean = false,
    val error: String = ""
)

data class BnplGeneralScreenModel(
    val authorize: String = "",
    val currency: String = "USD",
    val bnplType: BNPLType = BNPLType.AFFIRM,
    val products: List<Product> = emptyList(),
    val shippingAddress: Address = createEmptyAddress(),
    val billingAddress: Address = createEmptyAddress(),
    val phoneNumber: PhoneNumberModel = PhoneNumberModel(),
    val customerData: Customer = createEmptyCustomer(),
    val bnplShippingMethod: BNPLShippingMethod = BNPLShippingMethod.DELIVERY,
    val amountError: Boolean = false,
    val productsError: Boolean = false
)

data class BnplRefundScreenModel(
    val amount: String = ""
)

enum class ScreenState(val parent: ScreenState? = null) {
    General(null),
    Products(General),
    ShippingAddress(General),
    BillingAddress(General),
    CustomerData(General),
    CaptureRefundReverse(General),
    Refund(CaptureRefundReverse),
}

data class PhoneNumberModel(
    val countryCode: String = "1", val number: String = "7708298000", val phoneNumberType: PhoneNumberType = PhoneNumberType.Shipping
)

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
        firstName = "James";
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

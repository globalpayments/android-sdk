package com.globalpayments.android.sdk.sample.gpapi.merchantonboarding.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Address
import com.global.api.entities.PhoneNumber
import com.global.api.entities.User
import com.global.api.entities.enums.PersonFunctions
import com.global.api.entities.enums.UserType
import com.global.api.entities.payFac.PaymentStatistics
import com.global.api.entities.payFac.Person
import com.global.api.entities.payFac.UserPersonalData
import com.global.api.services.PayFacService
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.math.BigDecimal


class CreateMerchantViewModel : BaseViewModel() {

    private val service = PayFacService();

    var merchantDataModel: MerchantDataModel? = null
    var products: List<Product> = emptyList()
    var bankAccountDataModel: BankAccountDataModel? = null
    var paymentStatistics: PaymentStatisticsModel? = null

    val createdMerchant: MutableLiveData<User> = MutableLiveData()
    val errors: MutableLiveData<MissingFields> = MutableLiveData()

    fun onboardMerchant() {
        errors.postValue(MissingFields())
        val merchantData = createMerchantData() ?: run { errors.postValue(MissingFields(merchantData = true)); return }
        val products = createProducts().takeIf { it.isNotEmpty() } ?: run { errors.postValue(MissingFields(products = true)); return }
        val paymentStatistics = createPaymentStatistics() ?: run { errors.postValue(MissingFields(paymentStatistics = true)); return }
        showProgress()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val merchant = service.createMerchant().withUserPersonalData(merchantData).withDescription("Merchant Business Description")
                    .withProductData(products).withPaymentStatistics(paymentStatistics).withPersonsData(getPersonList())
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)

                hideProgress()

                createdMerchant.postValue(merchant)

            } catch (exception: Exception) {
                showError(exception)
            }
        }
    }

    private fun createMerchantData(): UserPersonalData? {
        val merchantDataModel = merchantDataModel ?: return null
        val merchantData =
            UserPersonalData().setUserName(merchantDataModel.merchantName).setLegalName(merchantDataModel.fullLegalName).setDBA(merchantDataModel.dba)
                .setMerchantCategoryCode(merchantDataModel.merchantCategory).setWebsite(merchantDataModel.website)
                .setNotificationEmail(merchantDataModel.email).setCurrencyCode(merchantDataModel.currency)
                .setTaxIdReference(merchantDataModel.taxIdReference).setTier("test").setType(UserType.MERCHANT)

        val businessAddress = Address().setStreetAddress1(merchantDataModel.businessAddress.streetAddress1)
            .setStreetAddress2(merchantDataModel.businessAddress.streetAddress2).setStreetAddress3(merchantDataModel.businessAddress.streetAddress3)
            .setCity(merchantDataModel.businessAddress.city).setState(merchantDataModel.businessAddress.state)
            .setPostalCode(merchantDataModel.businessAddress.postalCode).setCountryCode(merchantDataModel.businessAddress.countryCode)

        merchantData.userAddress = businessAddress

        val shippingAddress = Address().setStreetAddress1(merchantDataModel.shippingAddress.streetAddress1)
            .setStreetAddress2(merchantDataModel.shippingAddress.streetAddress2).setStreetAddress3(merchantDataModel.shippingAddress.streetAddress3)
            .setCity(merchantDataModel.shippingAddress.city).setPostalCode(merchantDataModel.shippingAddress.postalCode)
            .setCountryCode(merchantDataModel.shippingAddress.countryCode)

        merchantData.mailingAddress = shippingAddress
        merchantData.notificationStatusUrl = "https://www.example.com/notifications/status"

        return merchantData
    }

    private fun createProducts(): List<com.global.api.entities.Product> {
        return products.map {
            com.global.api.entities.Product().apply {
                quantity = it.quantity
                productId = it.productId
            }
        }
    }

    private fun createPaymentStatistics(): PaymentStatistics? {
        val ps = paymentStatistics ?: return null
        return PaymentStatistics().apply {
            totalMonthlySalesAmount = BigDecimal(ps.totalMonthlySales)
            averageTicketSalesAmount = BigDecimal(ps.averageTicketSales)
            highestTicketSalesAmount = BigDecimal(ps.highestTicketSales)
        }
    }

    private fun getPersonList(type: String? = null): List<Person> {
        val persons = ArrayList<Person>()
        Person().apply {
            functions = PersonFunctions.APPLICANT
            firstName = "James $type"
            middleName = "Mason $type"
            lastName = "Doe  $type"
            email = "uniqueemail@address.com"
            dateOfBirth = DateTime.parse("1982-02-23").toString("yyyy-MM-dd")
            nationalIdReference = "123456789"
            jobTitle = "CEO"
            equityPercentage = "25"
            address = Address()
            address.streetAddress1 = "1 Business Address"
            address.streetAddress2 = "Suite 2"
            address.streetAddress3 = "1234"
            address.city = "Atlanta"
            address.state = "GA"
            address.postalCode = "30346"
            address.country = "US"
            homePhone = PhoneNumber().setCountryCode("01").setNumber("8008675309")
            workPhone = PhoneNumber().setCountryCode("01").setNumber("8008675309")
            persons.add(this)
        }
        return persons
    }
}

data class MissingFields(
    val merchantData: Boolean = false, val products: Boolean = false, val paymentStatistics: Boolean = false
)

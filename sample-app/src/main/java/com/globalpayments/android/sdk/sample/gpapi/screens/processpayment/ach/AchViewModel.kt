package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.ach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Address
import com.global.api.entities.Customer
import com.global.api.entities.FundsData
import com.global.api.entities.Transaction
import com.global.api.entities.enums.AccountType
import com.global.api.entities.enums.MerchantAccountStatus
import com.global.api.entities.enums.MerchantAccountType
import com.global.api.entities.enums.MerchantAccountsSortProperty
import com.global.api.entities.enums.SecCode
import com.global.api.entities.enums.SortDirection
import com.global.api.entities.reporting.MerchantSummaryPaged
import com.global.api.entities.reporting.SearchCriteria
import com.global.api.paymentMethods.eCheck
import com.global.api.services.ReportingService
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.getAccountByType
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import com.globalpayments.android.sdk.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date


class AchViewModel : ViewModel() {

    val screenModel = MutableStateFlow(AchScreenModel())
    private val eCheck = eCheck()
    private val calendar = Calendar.getInstance()

    fun onAmountChanged(value: String) = screenModel.update { it.copy(amount = value) }
    fun onSplitCheckedChange(value: Boolean) = screenModel.update { it.copy(splitTransaction = value) }
    fun onSplitAmountChanged(value: String) = screenModel.update { it.copy(splitAmount = value) }
    fun onSplitMerchantIdChanged(value: String) = screenModel.update { it.copy(splitMerchantId = value) }
    fun onAccountHolderNameChanged(value: String) = screenModel.update { it.copy(accountHolderName = value) }
    fun onAccountTypeChanged(value: AccountType) = screenModel.update { it.copy(accountType = value) }
    fun onSecCodeChanged(value: SecCode) = screenModel.update { it.copy(secCode = value) }
    fun updateRoutingNumber(value: String) = screenModel.update { it.copy(routingNumber = value) }
    fun updateAccountNumber(value: String) = screenModel.update { it.copy(accountNumber = value) }
    fun updateCustomerFirstName(value: String) = screenModel.update { it.copy(customerFirstName = value) }
    fun updateCustomerLastName(value: String) = screenModel.update { it.copy(customerLastName = value) }
    fun updateCustomerBirthDate(value: Date) = screenModel.update {
        calendar.time = value
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val date = "$year-${if (month < 10) "0$month" else month}-${if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth}"
        it.copy(customerBirthDate = date)
    }

    fun updatePhoneNumber(value: String) = screenModel.update { it.copy(customerMobilePhone = value) }
    fun updateHomeNumber(value: String) = screenModel.update { it.copy(customerHomePhone = value) }
    fun updateBillingAddressLine1(value: String) = screenModel.update { it.copy(billingAddressLine1 = value) }
    fun updateBillingAddressLine2(value: String) = screenModel.update { it.copy(billingAddressLine2 = value) }
    fun updateBillingCity(value: String) = screenModel.update { it.copy(billingAddressCity = value) }
    fun updateBillingState(value: String) = screenModel.update { it.copy(billingAddressState = value) }
    fun updateBillingPostalCode(value: String) = screenModel.update { it.copy(billingAddressPostalCode = value) }
    fun updateBillingAddressCountry(value: String) = screenModel.update { it.copy(billingAddressCountry = value) }

    fun makePayment(paymentType: PaymentType) {
        val amount = screenModel.value.amount.toBigDecimalOrNull() ?: return
        viewModelScope.launch {
            try {
                val model = screenModel.value
                eCheck.apply {
                    accountType = model.accountType
                    secCode = model.secCode
                    accountNumber = model.accountNumber
                    routingNumber = model.routingNumber
                    checkHolderName = model.accountHolderName
                    merchantNotes = "123111"
                }
                val billingAddress = getBillingAddressFromModel(model)
                val customer = getCustomerFromModel(model)
                val result = when (paymentType) {
                    PaymentType.Charge -> charge(
                        amount,
                        billingAddress,
                        customer,
                        model.splitTransaction,
                        model.splitMerchantId,
                        model.splitAmount.toBigDecimalOrNull()
                    )

                    PaymentType.Refund -> refund(amount, billingAddress, customer)
                }

                val resultToShow = result.mapNotNullFields()
                val sampleResponse = GPSampleResponseModel(
                    transactionId = result.transactionId, response = listOf(
                        "Time" to result.timestamp, "Status" to result.responseMessage
                    )
                )
                val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, resultToShow)
                screenModel.update {
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel, gpSampleResponseModel = sampleResponse)
                }

            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(Transaction::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    private suspend fun charge(
        amount: BigDecimal,
        billingAddress: Address,
        customer: Customer,
        split: Boolean,
        splitMerchantId: String,
        splitAmount: BigDecimal?,
    ) = withContext(Dispatchers.IO) {
        val transaction = eCheck
            .charge(amount)
            .withCurrency(Currency)
            .withAddress(billingAddress)
            .withCustomer(customer)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
        if (split && splitMerchantId.isNotBlank() && splitAmount != null) {
            splitTransaction(splitAmount, splitMerchantId, transaction)
        } else {
            transaction
        }
    }

    private suspend fun refund(
        amount: BigDecimal,
        billingAddress: Address,
        customer: Customer
    ) = withContext(Dispatchers.IO) {
        eCheck
            .refund(amount)
            .withCurrency(Currency)
            .withAddress(billingAddress)
            .withCustomer(customer)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    private suspend fun splitTransaction(splitAmount: BigDecimal, splitMerchantId: String, transaction: Transaction): Transaction {

        val merchants = getMerchants()
        val merchantSplitting = merchants.results.find { it.id == splitMerchantId } ?: throw IllegalStateException("Merchant not found")

        val accountSplitting = getAccountByType(
            merchantSplitting.id,
            MerchantAccountType.FUND_MANAGEMENT,
            StartDate,
            EndDate
        )
        val fundsData = FundsData().apply {
            this.recipientAccountId = accountSplitting?.id
            this.merchantId = splitMerchantId
        }

        return transaction
            .split(splitAmount)
            .withFundsData(fundsData)
            .withReference("Split Identifier")
            .withDescription("Split Test")
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    private fun getBillingAddressFromModel(model: AchScreenModel): Address {
        return Address().apply {
            streetAddress1 = model.billingAddressLine1
            streetAddress2 = model.billingAddressLine2
            city = model.billingAddressCity
            state = model.billingAddressState
            postalCode = model.billingAddressPostalCode
            country = model.billingAddressCountry
        }
    }

    private fun getCustomerFromModel(model: AchScreenModel): Customer {
        return Customer().apply {
            firstName = model.customerFirstName
            lastName = model.customerLastName
            mobilePhone = model.customerMobilePhone
            homePhone = model.customerHomePhone
            dateOfBirth = model.customerBirthDate ?: ""
        }
    }

    private suspend fun getMerchants(): MerchantSummaryPaged = withContext(Dispatchers.IO) {
        ReportingService
            .findMerchants(1, 10)
            .orderBy(MerchantAccountsSortProperty.TIME_CREATED, SortDirection.Descending)
            .where(SearchCriteria.MerchantStatus, MerchantAccountStatus.ACTIVE)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    fun reset() {
        screenModel.value = AchScreenModel()
    }

    companion object {
        private const val Currency = "USD"

        private val StartDate = DateUtils.addDays(Date(), -360 * 5)
        private val EndDate = Date()
    }
}

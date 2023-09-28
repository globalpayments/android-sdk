package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Address
import com.global.api.entities.Transaction
import com.global.api.entities.User
import com.global.api.entities.enums.MerchantAccountStatus
import com.global.api.entities.enums.MerchantAccountType
import com.global.api.entities.enums.MerchantAccountsSortProperty
import com.global.api.entities.enums.PaymentMethodFunction
import com.global.api.entities.enums.SortDirection
import com.global.api.entities.enums.UserType
import com.global.api.entities.reporting.DataServiceCriteria
import com.global.api.entities.reporting.MerchantAccountSummary
import com.global.api.entities.reporting.MerchantAccountSummaryPaged
import com.global.api.entities.reporting.SearchCriteria
import com.global.api.paymentMethods.CreditCardData
import com.global.api.services.PayFacService
import com.global.api.services.ReportingService
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date


class EditMerchantAccountsScreenViewModel : ViewModel() {

    val screenModel = MutableStateFlow(EditMerchantAccountsScreenModel())

    fun onCardNumberChanged(value: String) = screenModel.update { it.copy(cardNumber = value) }
    fun onExpiryMonthChanged(value: String) = screenModel.update { it.copy(expiryMonth = value) }
    fun onExpiryYearChanged(value: String) = screenModel.update { it.copy(expiryYear = value) }
    fun onCvnChanged(value: String) = screenModel.update { it.copy(cvn = value) }
    fun onCardHolderNameChanged(value: String) = screenModel.update { it.copy(cardHolderName = value) }
    fun billingAddressDialogVisibility(isVisible: Boolean) = screenModel.update { it.copy(showBillingAddressDialog = isVisible) }
    fun updateFromTimeCreated(value: Date) = screenModel.update { it.copy(fromTimeCreated = value) }
    fun updateToTimeCreated(value: Date) = screenModel.update { it.copy(toTimeCreated = value) }
    fun onBillingAddressChanged(address: Address) {
        screenModel.update { it.copy(billingAddress = address) }
        billingAddressDialogVisibility(false)
    }

    fun editAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            val model = screenModel.value
            try {
                val response = editAccount(model)
                val resultToShow = response.mapNotNullFields()
                val sampleResponse = GPSampleResponseModel(
                    transactionId = response.transactionId, response = listOf(
                        "Time" to response.timestamp, "Status" to response.responseMessage
                    )
                )
                val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, resultToShow)
                screenModel.update {
                    it.copy(
                        sampleResponseModel = sampleResponse,
                        gpSnippetResponseModel = gpSnippetResponseModel
                    )
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

    fun resetScreen() = screenModel.update { EditMerchantAccountsScreenModel() }

    private fun editAccount(screenModel: EditMerchantAccountsScreenModel): Transaction {
        val billingAddress = screenModel.billingAddress
        val creditCardInformation = CreditCardData().apply {
            number = screenModel.cardNumber
            expMonth = screenModel.expiryMonth.toIntOrNull()
            expYear = screenModel.expiryYear.toIntOrNull()
            cvn = screenModel.cvn
            cardHolderName = screenModel.cardHolderName
        }
        val merchants = ReportingService
            .findMerchants(1, 10)
            .orderBy(MerchantAccountsSortProperty.TIME_CREATED, SortDirection.Ascending)
            .where(SearchCriteria.MerchantStatus, MerchantAccountStatus.ACTIVE)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)

        val merchant = merchants.results.firstOrNull()?.let { User.fromId(it.id, UserType.MERCHANT) }
            ?: throw IllegalStateException("No merchant found for this account")

        val response: MerchantAccountSummaryPaged = ReportingService
            .findAccounts(1, 10)
            .orderBy(MerchantAccountsSortProperty.TIME_CREATED, SortDirection.Ascending)
            .where<Any>(SearchCriteria.StartDate, screenModel.fromTimeCreated)
            .and<Any>(SearchCriteria.EndDate, screenModel.toTimeCreated)
            .and(DataServiceCriteria.MerchantId, merchants.getResults()[0].id)
            .and(SearchCriteria.AccountStatus, MerchantAccountStatus.ACTIVE)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)

        val accountSummary: MerchantAccountSummary =
            response.results.firstOrNull { it.type == MerchantAccountType.FUND_MANAGEMENT }
                ?: throw IllegalStateException("No merchant found for this account")

        return PayFacService()
            .editAccount()
            .withAccountNumber(accountSummary.id)
            .withUserReference(merchant.userReference)
            .withAddress(billingAddress, null)
            .withCreditCardData(creditCardInformation, PaymentMethodFunction.PRIMARY_PAYOUT)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }
}

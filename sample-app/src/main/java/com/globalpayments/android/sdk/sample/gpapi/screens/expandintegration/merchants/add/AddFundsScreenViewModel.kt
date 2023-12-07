package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.merchants.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Transaction
import com.global.api.entities.User
import com.global.api.entities.enums.PaymentMethodName
import com.global.api.entities.enums.PaymentMethodType
import com.global.api.entities.enums.UserType
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddFundsScreenViewModel : ViewModel() {

    val screenModel = MutableStateFlow(AddFundsScreenModel())

    fun onAmountChanged(value: String) = screenModel.update { it.copy(amount = value) }
    fun onMerchantIdChanged(value: String) = screenModel.update { it.copy(merchantId = value) }
    fun onAccountNumberChanged(value: String) = screenModel.update { it.copy(accountNumber = value) }
    fun resetScreen() = screenModel.update { AddFundsScreenModel() }

    fun addFunds() {
        viewModelScope.launch(Dispatchers.IO) {
            val model = screenModel.value
            try {

                val merchant = User.fromId(model.merchantId, UserType.MERCHANT)
                val response = addFunds(merchant, model)

                val resultToShow = response.mapNotNullFields()
                val sampleResponse = GPSampleResponseModel(
                    transactionId = response.fundsAccountDetails.account.id, response = listOf(
                        "Response Code" to response.responseCode,
                        "Status" to response.fundsAccountDetails.status,
                        "Amount" to response.fundsAccountDetails.amount,
                        "Currency" to response.fundsAccountDetails.currency,
                    )
                )
                val gpSnippetResponseModel = GPSnippetResponseModel(User::class.java.simpleName, resultToShow)
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

    private suspend fun addFunds(
        merchant: User,
        model: AddFundsScreenModel
    ) = withContext(Dispatchers.IO) {
        merchant
            .addFunds()
            .withAmount(model.amount)
            .withAccountNumber(model.accountNumber)
            .withPaymentMethodName(PaymentMethodName.BankTransfer)
            .withPaymentMethodType(PaymentMethodType.Credit)
            .withCurrency(CURRENCY)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    companion object {
        private const val CURRENCY = "USD"
    }
}

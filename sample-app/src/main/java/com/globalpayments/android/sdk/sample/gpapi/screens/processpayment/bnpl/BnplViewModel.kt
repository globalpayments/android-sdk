package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.bnpl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Address
import com.global.api.entities.Customer
import com.global.api.entities.Product
import com.global.api.entities.Transaction
import com.global.api.entities.enums.AddressType
import com.global.api.entities.enums.BNPLShippingMethod
import com.global.api.entities.enums.BNPLType
import com.global.api.entities.enums.PhoneNumberType
import com.global.api.paymentMethods.BNPL
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class BnplViewModel : ViewModel() {

    val screenModel: MutableStateFlow<BnplScreenModel> = MutableStateFlow(BnplScreenModel())

    fun resetState() = screenModel.update { BnplScreenModel() }

    fun onAmountChanged(amount: String) =
        screenModel.update { screenModel.value.updateGeneralScreenModel { it.copy(authorize = amount) } }

    fun onBnplTypeChanged(bnplType: BNPLType) {
        screenModel.value = screenModel.value.updateGeneralScreenModel { it.copy(bnplType = bnplType) }
    }

    fun onProductsSelected(products: List<Product>) {
        screenModel.value = screenModel.value.updateGeneralScreenModel { it.copy(products = products) }
        productsDialogVisibility(false)
    }

    fun onBillingAddressChanged(address: Address) {
        screenModel.value = screenModel.value.updateGeneralScreenModel { it.copy(billingAddress = address) }
        billingAddressDialogVisibility(false)
    }

    fun onShippingAddressChanged(address: Address) {
        screenModel.value = screenModel.value.updateGeneralScreenModel { it.copy(shippingAddress = address) }
        shippingAddressDialogVisibility(false)
    }

    fun onCustomerDataChanged(customerData: Customer) {
        screenModel.update {
            it.updateGeneralScreenModel { generalModel -> generalModel.copy(customerData = customerData) }
            it.copy(showCustomerDataDialog = false)
        }
    }

    fun countryCodeChanged(input: String) {
        screenModel.value = screenModel.value.updateGeneralScreenModel { it.copy(phoneNumber = it.phoneNumber.copy(countryCode = input)) }
    }

    fun phoneNumberChanged(input: String) {
        screenModel.value = screenModel.value.updateGeneralScreenModel { it.copy(phoneNumber = it.phoneNumber.copy(number = input)) }
    }

    fun phoneNumberTypeChanged(input: PhoneNumberType) {
        screenModel.value = screenModel.value.updateGeneralScreenModel { it.copy(phoneNumber = it.phoneNumber.copy(phoneNumberType = input)) }
    }

    fun customerDataDialogVisibility(isVisible: Boolean) {
        screenModel.update { it.copy(showCustomerDataDialog = isVisible) }
    }

    fun productsDialogVisibility(isVisible: Boolean) {
        screenModel.update { it.copy(showProductsDialog = isVisible) }
    }

    fun billingAddressDialogVisibility(isVisible: Boolean) {
        screenModel.update { it.copy(showBillingAddressDialog = isVisible) }
    }

    fun shippingAddressDialogVisibility(isVisible: Boolean) {
        screenModel.update { it.copy(showShippingAddressDialog = isVisible) }
    }

    fun refundAmountChanged(amount: String) {
        screenModel.value = screenModel.value.updateRefundScreenModel { it.copy(amount = amount) }
    }

    fun onBnplClicked() {
        executeCatchingError {
            val requestModel = screenModel.value.bnplRequestModel
            val response = executeBnpl(requestModel)
            val responseAsMap = response.mapNotNullFields()
            val responseToShow = GPSampleResponseModel(
                transactionId = response.transactionId,
                response = listOf(
                    "Time" to response.timestamp,
                    "Status" to response.responseMessage
                )
            )
            val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, responseAsMap)
            screenModel.update {
                it.copy(
                    transaction = response,
                    urlToOpen = response.bnplResponse.redirectUrl ?: "",
                    gpSnippetResponseModel = gpSnippetResponseModel,
                    gpSampleResponseModel = responseToShow
                )
            }
        }
    }

    private suspend fun executeBnpl(requestModel: BnplRequestModel): Transaction = withContext(Dispatchers.IO) {
        var request =
            createPaymentMethod(requestModel.bnplType)
                .authorize(BigDecimal(requestModel.authorize))
                .withCurrency(requestModel.currency)
                .withMiscProductData(ArrayList(requestModel.products))
                .withAddress(requestModel.shippingAddress, AddressType.Shipping)
                .withAddress(requestModel.billingAddress, AddressType.Billing)
                .withCustomerData(requestModel.customerData)

        if (requestModel.bnplType != BNPLType.KLARNA) {
            request = request
                .withPhoneNumber(requestModel.phoneNumber.countryCode, requestModel.phoneNumber.number, requestModel.phoneNumber.phoneNumberType)
                .withBNPLShippingMethod(BNPLShippingMethod.DELIVERY)
        }
        request.execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    fun captureTransaction() {
        val transaction = screenModel.value.transaction ?: return
        executeCatchingError {
            val response = transaction
                .capture()
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            if (response.responseCode != "SUCCESS") throw IllegalStateException("Capturing failed")
            val responseAsMap = response.mapNotNullFields()
            val responseToShow = GPSampleResponseModel(
                transactionId = response.transactionId,
                response = listOf(
                    "Time" to response.timestamp,
                    "Status" to response.responseMessage
                )
            )
            val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, responseAsMap)
            screenModel.update {
                it.copy(
                    transaction = response,
                    gpSnippetResponseModel = gpSnippetResponseModel,
                    gpSampleResponseModel = responseToShow,
                    screenState = ScreenState.Refund
                )
            }
        }
    }

    fun reverseTransaction() {
        val transaction = screenModel.value.transaction ?: return
        executeCatchingError {
            val response = transaction
                .reverse()
                .execute(Constants.DEFAULT_GPAPI_CONFIG)

            val responseAsMap = response.mapNotNullFields()
            val responseToShow = GPSampleResponseModel(
                transactionId = response.transactionId,
                response = listOf(
                    "Time" to response.timestamp,
                    "Status" to response.responseMessage
                )
            )
            val screenState = if (response.responseMessage == "REVERSED") {
                ScreenState.Refund
            } else {
                ScreenState.Reset
            }
            val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, responseAsMap)
            screenModel.update {
                it.copy(
                    transaction = response,
                    gpSampleResponseModel = responseToShow,
                    gpSnippetResponseModel = gpSnippetResponseModel,
                    screenState = screenState
                )
            }
        }
    }

    fun onRefundClick() {
        val transaction = screenModel.value.transaction ?: return
        executeCatchingError {
            val refundAmount = screenModel.value.bnplRefundScreenModel.amount.takeIf(String::isNotBlank)?.let { BigDecimal(it) }
            val response = if (refundAmount == null) {
                transaction.refund()
            } else {
                transaction.refund(refundAmount)
            }
                .withCurrency(screenModel.value.bnplRequestModel.currency)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)

            val responseToShow = GPSampleResponseModel(
                transactionId = response.transactionId,
                response = listOf(
                    "Time" to response.timestamp,
                    "Status" to response.responseMessage
                )
            )
            val responseAsMap = response.mapNotNullFields()
            val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, responseAsMap)
            screenModel.update {
                it.copy(
                    transaction = response,
                    gpSnippetResponseModel = gpSnippetResponseModel,
                    gpSampleResponseModel = responseToShow,
                    screenState = ScreenState.Reset
                )
            }
        }
    }

    private fun createPaymentMethod(bnplType: BNPLType): BNPL {
        return BNPL().apply {
            this.bnplType = bnplType
            this.setReturnUrl("http://example.com")
            this.setStatusUpdateUrl("http://example.com")
            this.setCancelUrl("http://example.com")
        }
    }

    private fun executeCatchingError(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                block()
            } catch (exception: Exception) {
                val gpSnippetResponseModel =
                    GPSnippetResponseModel(Transaction::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
                screenModel.value = screenModel.value.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                Log.e("BnplViewModel", exception.message ?: "Error")
            }
        }
    }

    fun goToCapture() {
        if (screenModel.value.screenState == ScreenState.Request && screenModel.value.transaction != null)
            screenModel.update { it.copy(screenState = ScreenState.CaptureRefund) }
    }

    private fun BnplScreenModel.updateRefundScreenModel(block: (BnplRefundScreenModel) -> BnplRefundScreenModel): BnplScreenModel {
        return copy(bnplRefundScreenModel = block(bnplRefundScreenModel))
    }

    private fun BnplScreenModel.updateGeneralScreenModel(block: (BnplRequestModel) -> BnplRequestModel): BnplScreenModel {
        return copy(bnplRequestModel = block(bnplRequestModel))
    }
}

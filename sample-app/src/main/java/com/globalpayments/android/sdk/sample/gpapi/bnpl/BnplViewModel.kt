package com.globalpayments.android.sdk.sample.gpapi.bnpl

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Address
import com.global.api.entities.Customer
import com.global.api.entities.Product
import com.global.api.entities.enums.AddressType
import com.global.api.entities.enums.BNPLShippingMethod
import com.global.api.entities.enums.BNPLType
import com.global.api.entities.enums.PhoneNumberType
import com.global.api.paymentMethods.BNPL
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class BnplViewModel : BaseViewModel(), DefaultLifecycleObserver {

    val screenModel: MutableStateFlow<BnplScreenModel> = MutableStateFlow(BnplScreenModel())

    fun goToScreen(screenState: ScreenState) {
        screenModel.value = screenModel.value.copy(currentScreen = screenState, showTransactionSuccessDialog = false)
    }

    fun goToPreviousScreen() {
        val parent = screenModel.value.currentScreen.parent ?: return
        screenModel.value = screenModel.value.copy(currentScreen = parent)
    }

    fun resetState() {
        screenModel.value = BnplScreenModel()
    }

    fun onAmountChanged(amount: String) {
        screenModel.value = screenModel.value.updateGeneralScreenModel { it.copy(authorize = amount, amountError = false) }
    }

    fun onBnplTypeChanged(bnplType: BNPLType) {
        screenModel.value = screenModel.value.updateGeneralScreenModel { it.copy(bnplType = bnplType) }
    }

    fun onProductsSelected(products: List<Product>) {
        screenModel.value = screenModel.value.updateGeneralScreenModel { it.copy(products = products, productsError = false) }
    }

    fun onBillingAddressChanged(address: Address) {
        screenModel.value = screenModel.value.updateGeneralScreenModel { it.copy(billingAddress = address) }
    }

    fun onShippingAddressChanged(address: Address) {
        screenModel.value = screenModel.value.updateGeneralScreenModel { it.copy(shippingAddress = address) }
    }

    fun onCustomerDataChanged(customerData: Customer) {
        screenModel.value = screenModel.value.updateGeneralScreenModel { it.copy(customerData = customerData) }
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

    fun onBnplClicked() {
        executeCatchingError {
            val screenModel = screenModel.value.bnplGeneralScreenModel
            this.screenModel.value = this.screenModel.value.copy(
                bnplGeneralScreenModel = screenModel.copy(
                    amountError = screenModel.authorize.isBlank(),
                    productsError = screenModel.products.isEmpty()
                )
            )
            if (screenModel.authorize.isBlank() || screenModel.products.isEmpty()) return@executeCatchingError
            this.screenModel.value = this.screenModel.value.copy(isLoading = true)
            var request =
                createPaymentMethod(screenModel.bnplType)
                    .authorize(BigDecimal(screenModel.authorize))
                    .withCurrency(screenModel.currency)
                    .withMiscProductData(ArrayList(screenModel.products))
                    .withAddress(screenModel.shippingAddress, AddressType.Shipping)
                    .withAddress(screenModel.billingAddress, AddressType.Billing)
                    .withCustomerData(screenModel.customerData)

            if (screenModel.bnplType != BNPLType.KLARNA) {
                request = request
                    .withPhoneNumber(screenModel.phoneNumber.countryCode, screenModel.phoneNumber.number, screenModel.phoneNumber.phoneNumberType)
                    .withBNPLShippingMethod(BNPLShippingMethod.DELIVERY)
            }
            val response = request
                .execute(Constants.DEFAULT_GPAPI_CONFIG)

            this@BnplViewModel.screenModel.value =
                this@BnplViewModel.screenModel.value.copy(
                    transaction = response,
                    isLoading = false,
                    urlToOpen = response.bnplResponse.redirectUrl ?: ""
                )
        }
    }

    private fun BnplScreenModel.updateGeneralScreenModel(block: (BnplGeneralScreenModel) -> BnplGeneralScreenModel): BnplScreenModel {
        return copy(bnplGeneralScreenModel = block(bnplGeneralScreenModel))
    }

    fun urlOpened() {
        screenModel.value = screenModel.value.copy(urlToOpen = "")
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        screenModel.value.transaction ?: return
        goToScreen(ScreenState.CaptureRefundReverse)
    }

    private fun createPaymentMethod(bnplType: BNPLType): BNPL {
        return BNPL().apply {
            this.bnplType = bnplType
            this.setReturnUrl("http://example.com")
            this.setStatusUpdateUrl("http://example.com")
            this.setCancelUrl("http://example.com")
        }
    }

    fun captureTransaction() {
        val transaction = screenModel.value.transaction ?: return
        executeCatchingError {
            screenModel.value = screenModel.value.copy(isLoading = true)
            val response = transaction
                .capture()
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            if (response.responseCode != "SUCCESS") throw IllegalStateException("Capturing failed")
            screenModel.value = screenModel.value.copy(
                transaction = response,
                showTransactionSuccessDialog = true,
                isLoading = false
            )
        }
    }

    fun reverseTransaction() {
        val transaction = screenModel.value.transaction ?: return
        executeCatchingError {
            screenModel.value = screenModel.value.copy(isLoading = true)
            val response = transaction
                .reverse()
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            screenModel.value = screenModel.value.copy(transaction = response, showTransactionSuccessDialog = true, isLoading = false)
        }
    }

    private fun BnplScreenModel.updateRefundScreenModel(block: (BnplRefundScreenModel) -> BnplRefundScreenModel): BnplScreenModel {
        return copy(bnplRefundScreenModel = block(bnplRefundScreenModel))
    }

    fun refundAmountChanged(amount: String) {
        screenModel.value = screenModel.value.updateRefundScreenModel { it.copy(amount = amount) }
    }

    fun onRefundClick() {
        val transaction = screenModel.value.transaction ?: return
        screenModel.value = screenModel.value.copy(isLoading = true)
        executeCatchingError {
            val response = transaction
                .refund(BigDecimal(screenModel.value.bnplRefundScreenModel.amount))
                .withCurrency(screenModel.value.bnplGeneralScreenModel.currency)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            screenModel.value = screenModel.value.copy(transaction = response, showTransactionSuccessDialog = true, isLoading = false)
        }
    }

    private fun executeCatchingError(block: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                block()
            } catch (exception: Exception) {
                screenModel.value = screenModel.value.copy(error = exception.message ?: "Error occurred", isLoading = false)
                Log.e("BnplViewModel", exception.message ?: "Error")
            }
        }
    }
}

package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.payers.createpayment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Customer
import com.global.api.paymentMethods.CreditCardData
import com.global.api.utils.GenerationUtils
import com.globalpayments.android.sdk.model.PaymentCardModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePaymentScreenViewModel : ViewModel() {

    val screenModel = MutableStateFlow(CreatePaymentScreenModel())

    fun onIdChanged(value: String) = screenModel.update { it.copy(customerId = value) }

    fun onFirstNameChanged(value: String) = screenModel.update { it.copy(customerFirstName = value) }

    fun onLastNameChanged(value: String) = screenModel.update { it.copy(customerLastName = value) }

    fun onPaymentCardSelected(paymentCardModel: PaymentCardModel) =
        screenModel.update {
            it.copy(
                paymentCard = paymentCardModel,
                cardNumber = paymentCardModel.cardNumber,
                expiryYear = paymentCardModel.expiryYear,
                expiryMonth = paymentCardModel.expiryMonth,
                cvn = paymentCardModel.cvnCvv
            )
        }

    fun onCardNumberChanged(value: String) = screenModel.update { it.copy(cardNumber = value) }

    fun onExpiryMonthChanged(value: String) = screenModel.update { it.copy(expiryMonth = value) }


    fun onExpiryYearChanged(value: String) = screenModel.update { it.copy(expiryYear = value) }


    fun onCvnChanged(value: String) = screenModel.update { it.copy(cvn = value) }


    fun onCurrencyChanged(value: String) = screenModel.update { it.copy(currency = value) }

    fun resetScreen() = screenModel.update { CreatePaymentScreenModel() }

    fun createPayment() {
        viewModelScope.launch(Dispatchers.IO) {
            val model = screenModel.value
            try {
                val customer = getCustomerFromModel(model)
                val response = addPaymentMethod(model, customer)

                val resultToShow = listOf(
                    "ID" to response.id,
                )
                val sampleResponse = GPSampleResponseModel(
                    transactionId = response.paymentMethods[0].id,
                    response = listOf(
                        "Customer Key" to response.key,
                        "First Name" to response.firstName,
                        "Last Name" to response.lastName,
                    )
                )
                val gpSnippetResponseModel = GPSnippetResponseModel(
                    Customer::class.java.simpleName,
                    resultToShow
                )
                screenModel.update {
                    it.copy(
                        sampleResponseModel = sampleResponse,
                        gpSnippetResponseModel = gpSnippetResponseModel
                    )
                }
            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(
                            Customer::class.java.simpleName,
                            listOf(
                                "Error" to (exception.message ?: "")
                            ), true
                        )
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    private fun getCustomerFromModel(model: CreatePaymentScreenModel): Customer {
        return Customer().apply {
            id = model.customerId
            key = GenerationUtils.generateOrderId()
            firstName = model.customerFirstName
            lastName = model.customerLastName
        }
    }

    private suspend fun addPaymentMethod(
        model: CreatePaymentScreenModel,
        payer: Customer,
    ) = withContext(Dispatchers.IO) {
        val creditCard = CreditCardData().apply {
            number = model.cardNumber.takeIf(String::isNotBlank) ?: throw java.lang.IllegalArgumentException("cardNumber is not set ")
            expMonth = model.expiryMonth.toIntOrNull() ?: throw IllegalArgumentException("Expiry Month is not set")
            expYear = model.expiryYear.takeIf { it.length == 4 }?.toIntOrNull() ?: throw IllegalArgumentException("invalid expiry Year")
            cvn = model.cvn.takeIf(String::isNotBlank) ?: throw java.lang.IllegalArgumentException("cnv is not set ")
        }

        val tokenizedCard = CreditCardData().apply {
            token = creditCard.tokenize()
        }

        payer
            .apply {
                paymentMethods = listOf(
                    payer.addPaymentMethod(tokenizedCard.token, creditCard)
                )
            }
            .saveChanges()
    }

}

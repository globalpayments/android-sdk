package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.verifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Transaction
import com.global.api.paymentMethods.CreditCardData
import com.globalpayments.android.sdk.model.PaymentCardModel
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import com.globalpayments.android.sdk.sample.utils.FingerPrintUsageMethod
import com.globalpayments.android.sdk.sample.utils.FingerprintMethodUsageMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VerificationsScreenViewModel : ViewModel() {

    val screenModel: MutableStateFlow<VerificationsScreenModel> = MutableStateFlow(VerificationsScreenModel())

    fun onPaymentCardSelected(paymentCardModel: PaymentCardModel) {
        screenModel.update {
            it.copy(
                paymentCard = paymentCardModel,
                cardNumber = paymentCardModel.cardNumber,
                expiryYear = paymentCardModel.expiryYear,
                expiryMonth = paymentCardModel.expiryMonth,
                cvn = paymentCardModel.cvnCvv
            )
        }
    }

    fun onCardNumberChanged(value: String) {
        screenModel.update { it.copy(cardNumber = value) }
    }

    fun onExpiryMonthChanged(value: String) {
        screenModel.update { it.copy(expiryMonth = value) }
    }

    fun onExpiryYearChanged(value: String) {
        screenModel.update { it.copy(expiryYear = value) }
    }

    fun onCvnChanged(value: String) {
        screenModel.update { it.copy(cvn = value) }
    }

    fun onCurrencyChanged(value: String) {
        screenModel.update { it.copy(currency = value) }
    }

    fun enableFingerprint(value: Boolean) {
        screenModel.update { it.copy(fingerPrintEnabled = value) }
    }

    fun onFingerPrintUsageMethodChanged(value: FingerprintMethodUsageMode?) = screenModel.update { it.copy(fingerPrintUsageMethod = value) }

    fun onIdempotencyKey(value: String) {
        screenModel.update { it.copy(idempotencyKey = value) }
    }

    fun onSubmitClicked() {
        screenModel.update { it.copy(gpSnippetResponseModel = GPSnippetResponseModel()) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = executeRequest()
                val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, response.mapNotNullFields())
                screenModel.update {
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
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

    private fun executeRequest(): Transaction {
        val model = screenModel.value
        val creditCard = CreditCardData().apply {
            number = model.cardNumber.takeIf(String::isNotBlank) ?: throw java.lang.IllegalArgumentException("cardNumber is not set ")
            expMonth = model.expiryMonth.toIntOrNull() ?: throw IllegalArgumentException("Expiry Month is not set")
            expYear = model.expiryYear.takeIf { it.length == 4 }?.toIntOrNull() ?: throw IllegalArgumentException("invalid expiry Year")
            cvn = model.cvn.takeIf(String::isNotBlank) ?: throw java.lang.IllegalArgumentException("cnv is not set ")
        }
        return if (model.fingerPrintEnabled) {
            val tokenizedCard = CreditCardData().apply {
                token = creditCard.tokenize(Constants.DEFAULT_GPAPI_CONFIG)
            }
            val customer = FingerPrintUsageMethod.fingerPrintSelectedOption(model.fingerPrintUsageMethod)
            tokenizedCard
                .verify()
                .withIdempotencyKey(model.idempotencyKey)
                .withCurrency(model.currency)
                .withCustomerData(customer)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
        } else {
            creditCard
                .verify()
                .withIdempotencyKey(model.idempotencyKey)
                .withCurrency(model.currency)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
        }
    }
}

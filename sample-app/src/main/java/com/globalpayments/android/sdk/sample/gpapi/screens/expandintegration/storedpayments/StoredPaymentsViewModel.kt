package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.storedpayments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Transaction
import com.global.api.entities.enums.PaymentMethodUsageMode
import com.global.api.paymentMethods.CreditCardData
import com.globalpayments.android.sdk.model.PaymentCardModel
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import com.globalpayments.android.sdk.sample.utils.FingerPrintUsageMethod
import com.globalpayments.android.sdk.sample.utils.FingerprintMethodUsageMode
import com.globalpayments.android.sdk.sample.utils.ifSatisfies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StoredPaymentsViewModel : ViewModel() {

    val screenModel: MutableStateFlow<StoredPaymentsScreenModel> = MutableStateFlow(
        StoredPaymentsScreenModel()
    )

    fun onPaymentOperationChanged(value: PaymentOperation) {
        screenModel.update { it.copy(paymentOperation = value) }
    }

    fun onPaymentMethodIdChanged(value: String) = screenModel.update { it.copy(paymentMethodId = value) }

    fun onPaymentCardSelected(value: PaymentCardModel) {
        screenModel.update {
            it.copy(
                paymentCard = value,
                cardNumber = value.cardNumber,
                expiryMonth = value.expiryMonth,
                expiryYear = value.expiryYear,
                cvn = value.cvnCvv
            )
        }
    }

    fun onTokenUsageModeChanged(value: PaymentMethodUsageMode) {
        screenModel.update { it.copy(tokenUsageMode = value, gpSnippetResponseModel = GPSnippetResponseModel()) }
    }

    fun onCardHolderNameChanged(value: String) = screenModel.update { it.copy(cardHolderName = value) }

    fun enableFingerprint(value: Boolean) {
        screenModel.update { it.copy(fingerprintEnabled = value) }
    }

    fun onFingerPrintUsageMethodChanged(value: FingerprintMethodUsageMode?) = screenModel.update { it.copy(fingerPrintUsageMethod = value) }

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

    fun onSubmitClicked() {
        screenModel.update { it.copy(gpSnippetResponseModel = GPSnippetResponseModel()) }
        when (screenModel.value.paymentOperation) {
            PaymentOperation.TOKENIZE -> tokenize()
            PaymentOperation.EDIT -> edit()
            PaymentOperation.DELETE -> delete()
        }
    }

    private fun tokenize() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = tokenizeCard(screenModel.value)
                val fieldsToDisplay = result.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, fieldsToDisplay)
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

    private fun tokenizeCard(screenModel: StoredPaymentsScreenModel): Transaction {
        val card = CreditCardData().apply {
            number = screenModel.cardNumber.takeIf(String::isNotBlank) ?: throw IllegalArgumentException("invalid card number")
            expMonth = screenModel.expiryMonth.toIntOrNull() ?: throw IllegalArgumentException("invalid expiry month")
            expYear =
                screenModel.expiryYear.takeIf { it.length == 4 }?.toIntOrNull() ?: throw IllegalArgumentException("invalid expiry year")
            cvn = screenModel.cvn.takeIf(String::isNotBlank) ?: throw IllegalArgumentException("invalid cvn")
        }
        val customer = FingerPrintUsageMethod.fingerPrintSelectedOption(screenModel.fingerPrintUsageMethod)
        return card
            .tokenize(true, screenModel.tokenUsageMode)
            .ifSatisfies(screenModel::fingerprintEnabled) { withCustomerData(customer) }
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    private fun edit() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = edit(screenModel.value)
                val fieldsToDisplay = result.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, fieldsToDisplay)
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

    private fun edit(screenModel: StoredPaymentsScreenModel): Transaction {
        return CreditCardData()
            .apply {
                token = screenModel.paymentMethodId
                ifSatisfies(screenModel.cardHolderName::isNotBlank) { cardHolderName = screenModel.cardHolderName; }
                ifSatisfies(screenModel.expiryYear::isNotBlank) { expYear = screenModel.expiryYear.toInt() }
                ifSatisfies(screenModel.expiryMonth::isNotBlank) { expMonth = screenModel.expiryMonth.toInt() }
                ifSatisfies(screenModel.cardNumber::isNotBlank) { number = screenModel.cardNumber }
            }.updateToken()
            .withPaymentMethodUsageMode(screenModel.tokenUsageMode)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    private fun delete() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val deleted = delete(screenModel.value.paymentMethodId)
                val gpSnippetResponseModel = GPSnippetResponseModel(
                    Transaction::class.java.simpleName,
                    listOf("Deleted" to "${screenModel.value.paymentMethodId} was ${if (!deleted) "not" else ""} deleted"),
                    !deleted
                )
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

    private fun delete(paymentMethodId: String): Boolean {
        return CreditCardData()
            .apply {
                token = paymentMethodId
            }
            .deleteToken(Constants.DEFAULT_GPAPI_CONFIG)

    }

    private fun CreditCardData.ifSatisfies(condition: () -> Boolean, block: CreditCardData.() -> Unit): CreditCardData {
        if (condition()) {
            block()
            return this
        }
        return this
    }
}

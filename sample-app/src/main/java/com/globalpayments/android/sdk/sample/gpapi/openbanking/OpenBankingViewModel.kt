package com.globalpayments.android.sdk.sample.gpapi.openbanking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Transaction
import com.global.api.entities.TransactionSummary
import com.global.api.entities.enums.RemittanceReferenceType
import com.global.api.paymentMethods.BankPayment
import com.global.api.services.ReportingService
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.openbanking.models.PaymentDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal


sealed interface OpenBankingScreenState {
    object Product : OpenBankingScreenState
    data class Payment(val paymentType: PaymentType) : OpenBankingScreenState
    data class Error(val message: String) : OpenBankingScreenState
    data class Loading(val transaction: Transaction) : OpenBankingScreenState
    data class Processing(val transactionSummary: TransactionSummary) : OpenBankingScreenState
}

enum class PaymentType {
    Sepa, FasterPayments
}

val SepaAmount = "20.00"
val FasterPaymentsAmount = "17.57"

class OpenBankingViewModel : ViewModel() {

    val screenState: MutableStateFlow<OpenBankingScreenState> = MutableStateFlow(OpenBankingScreenState.Product)
    val redirectUrl: MutableSharedFlow<String> = MutableSharedFlow(replay = 1)

    val loading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun goToProductScreen() {
        screenState.update { OpenBankingScreenState.Product }
    }

    fun onUSDSelected() {
        screenState.update { OpenBankingScreenState.Payment(PaymentType.FasterPayments) }
    }

    fun payForProduct(paymentDetails: PaymentDetails) {
        when (paymentDetails) {
            is PaymentDetails.FasterPaymentDetails -> payWithFasterPayments(paymentDetails)
            is PaymentDetails.SepaDetails -> payWithSepa(paymentDetails)
        }
    }

    fun onEuroSelected() {
        screenState.update { OpenBankingScreenState.Payment(PaymentType.Sepa) }
    }

    private fun payWithSepa(sepaDetails: PaymentDetails.SepaDetails) {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bankPayment = BankPayment().apply {
                    iban = sepaDetails.iban
                    accountName = sepaDetails.accountName
                    returnUrl = "https://7b8e82a17ac00346e91e984f42a2a5fb.m.pipedream.net"
                    statusUpdateUrl = "https://7b8e82a17ac00346e91e984f42a2a5fb.m.pipedream.net"
                }

                val transaction = bankPayment
                    .charge(BigDecimal(SepaAmount))
                    .withCurrency("EUR")
                    .withRemittanceReference(RemittanceReferenceType.TEXT, "Nike Bounce Bag")
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)

                screenState.update { OpenBankingScreenState.Loading(transaction) }
                redirectUrl.emit(transaction.bankPaymentResponse.redirectUrl)
            } catch (exception: Exception) {
                screenState.update { OpenBankingScreenState.Error(exception.message ?: "Error") }
            } finally {
                loading.value = false
            }
        }

    }


    private fun payWithFasterPayments(fasterPaymentDetails: PaymentDetails.FasterPaymentDetails) {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bankPayment = BankPayment().apply {
                    accountNumber = fasterPaymentDetails.accountNumber
                    sortCode = fasterPaymentDetails.sortCode
                    accountName = fasterPaymentDetails.accountName
                    countries = listOf("GB", "IE")
                    returnUrl = "https://7b8e82a17ac00346e91e984f42a2a5fb.m.pipedream.net"
                    statusUpdateUrl = "https://7b8e82a17ac00346e91e984f42a2a5fb.m.pipedream.net"
                }

                val transaction = bankPayment
                    .charge(BigDecimal(FasterPaymentsAmount))
                    .withCurrency("GBP")
                    .withRemittanceReference(RemittanceReferenceType.TEXT, "Nike Bounce Bag")
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)

                screenState.update { OpenBankingScreenState.Loading(transaction) }

                redirectUrl.emit(transaction.bankPaymentResponse.redirectUrl)
            } catch (exception: Exception) {
                screenState.update { OpenBankingScreenState.Error(exception.message ?: "Error") }
            } finally {
                loading.value = false
            }
        }
    }

    fun checkTransaction() {
        val transaction = (screenState.value as? OpenBankingScreenState.Loading)?.transaction ?: return
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ReportingService
                    .transactionDetail(transaction.transactionId)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)
                screenState.update { OpenBankingScreenState.Processing(response) }
            } catch (exception: Exception) {
                screenState.update { OpenBankingScreenState.Error(exception.message ?: "Error") }
            } finally {
                loading.value = false
            }
        }
    }
}

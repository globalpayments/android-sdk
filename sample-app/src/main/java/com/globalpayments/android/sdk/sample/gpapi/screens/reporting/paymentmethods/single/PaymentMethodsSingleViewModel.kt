package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.paymentmethods.single

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.reporting.StoredPaymentMethodSummary
import com.global.api.services.ReportingService
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentMethodsSingleViewModel : ViewModel() {

    val screenModel = MutableStateFlow(PaymentMethodsSingleModel())

    fun onDepositIdChanged(value: String) = screenModel.update { it.copy(paymentMethodId = value) }

    fun getPaymentMethod() {
        val depositId = screenModel.value.paymentMethodId.takeIf { it.isNotBlank() } ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val summary = getPaymentMethodById(depositId)
                val responseToShow = summary.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(StoredPaymentMethodSummary::class.java.simpleName, responseToShow, false)
                screenModel.update { it.copy(gpSnippetResponseModel = gpSnippetResponseModel) }
            } catch (exception: Exception) {
                val gpSnippetResponseModel = GPSnippetResponseModel(
                    StoredPaymentMethodSummary::class.java.simpleName,
                    listOf("Exception" to (exception.message ?: "Error occurred")),
                    true
                )
                screenModel.update {
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    private fun getPaymentMethodById(paymentMethodId: String): StoredPaymentMethodSummary {
        return ReportingService
            .storedPaymentMethodDetail(paymentMethodId)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

}

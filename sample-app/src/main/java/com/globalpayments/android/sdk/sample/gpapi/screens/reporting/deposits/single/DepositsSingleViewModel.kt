package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.deposits.single

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.reporting.DepositSummary
import com.global.api.services.ReportingService
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DepositsSingleViewModel : ViewModel() {

    val screenModel = MutableStateFlow(DepositsSingleModel())

    fun onDepositIdChanged(value: String) = screenModel.update { it.copy(depositId = value) }

    fun getDeposit() {
        val depositId = screenModel.value.depositId.takeIf { it.isNotBlank() } ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val summary = getDepositById(depositId)
                val responseToShow = summary.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(DepositSummary::class.java.simpleName, responseToShow)
                screenModel.update { it.copy(gpSnippetResponseModel = gpSnippetResponseModel) }
            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(DepositSummary::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    private fun getDepositById(depositId: String): DepositSummary {
        return ReportingService
            .depositDetail(depositId)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

}

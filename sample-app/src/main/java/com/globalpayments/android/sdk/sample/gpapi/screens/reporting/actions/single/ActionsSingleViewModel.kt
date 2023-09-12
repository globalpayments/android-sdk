package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.actions.single

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.reporting.ActionSummary
import com.global.api.services.ReportingService
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ActionsSingleViewModel : ViewModel() {

    val screenModel = MutableStateFlow(ActionsSingleModel())

    fun onActionIdChanged(value: String) = screenModel.update { it.copy(actionId = value) }

    fun getDeposit() {
        val depositId = screenModel.value.actionId.takeIf { it.isNotBlank() } ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val summary = getActionById(depositId)
                val responseToShow = summary.mapNotNullFields()
                val gpSnippetResponseModel = GPSnippetResponseModel(ActionSummary::class.java.simpleName, responseToShow)
                screenModel.update { it.copy(gpSnippetResponseModel = gpSnippetResponseModel) }
            } catch (exception: Exception) {
                screenModel.update {
                    val gpSnippetResponseModel =
                        GPSnippetResponseModel(ActionSummary::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
                    it.copy(gpSnippetResponseModel = gpSnippetResponseModel)
                }
            }
        }
    }

    private fun getActionById(actionId: String): ActionSummary {
        return ReportingService
            .actionDetail(actionId)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

}

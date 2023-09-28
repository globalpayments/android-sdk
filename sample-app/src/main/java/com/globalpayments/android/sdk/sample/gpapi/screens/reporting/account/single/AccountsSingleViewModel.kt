package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.account.single

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
import kotlinx.coroutines.withContext


class AccountsSingleViewModel : ViewModel() {

    val screenModel = MutableStateFlow(AccountsSingleModel())

    fun onAccountIdChanged(value: String) = screenModel.update { it.copy(accountId = value) }

    fun getAccount() {
        viewModelScope.launch {
            try {
                val accountId = screenModel.value.accountId
                val accountSummary = getAccount(accountId = accountId)
                val responseToShow = accountSummary.mapNotNullFields()
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

    fun resetScreen() = screenModel.update { AccountsSingleModel() }

    private suspend fun getAccount(accountId: String) = withContext(Dispatchers.IO) {
        return@withContext ReportingService
            .accountDetail(accountId)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

}

package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.merchants.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Transaction
import com.global.api.entities.enums.MerchantAccountType
import com.global.api.entities.enums.UsableBalanceMode
import com.global.api.paymentMethods.AccountFunds
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.getAccountByType
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import com.globalpayments.android.sdk.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.Date
import java.util.UUID


class TransferFundsViewModel : ViewModel() {

    val screenModel: MutableStateFlow<TransferFundsScreenModel> = MutableStateFlow(TransferFundsScreenModel())

    fun onSenderMerchantIdChanged(value: String) = screenModel.update { it.copy(senderMerchantId = value) }
    fun onReceiverMerchantIdChanged(value: String) = screenModel.update { it.copy(receiverMerchantId = value) }
    fun onTransferAmountChanged(value: String) = screenModel.update { it.copy(transferAmount = value) }

    fun transfer() {
        viewModelScope.launch {
            val model = screenModel.value
            try {
                val response = transfer(model.senderMerchantId, model.receiverMerchantId, model.transferAmount.toBigDecimal())
                val resultToShow = response.mapNotNullFields()
                val sampleResponse = GPSampleResponseModel(
                    transactionId = response.transactionId, response = listOf(
                        "Time" to response.timestamp, "Status" to response.responseMessage
                    )
                )
                val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, resultToShow)
                screenModel.update {
                    it.copy(
                        sampleResponseModel = sampleResponse,
                        gpSnippetResponseModel = gpSnippetResponseModel
                    )
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

    private suspend fun transfer(
        senderMerchantId: String,
        receiverMerchantId: String,
        transferAmount: BigDecimal,
    ) = withContext(Dispatchers.IO) {

        val sender =
            getAccountByType(senderMerchantId, MerchantAccountType.FUND_MANAGEMENT, StartDate, EndDate) ?: throw IllegalStateException("No Account")
        val receiver =
            getAccountByType(receiverMerchantId, MerchantAccountType.FUND_MANAGEMENT, StartDate, EndDate) ?: throw IllegalStateException("No Account")
        val funds = AccountFunds().apply {
            accountId = sender.id
            accountName = sender.name
            recipientAccountId = receiver.id
            merchantId = senderMerchantId
            usableBalanceMode = UsableBalanceMode.AVAILABLE_AND_PENDING_BALANCE
        }
        val description = UUID.randomUUID().toString().replace(".", "").substring(0, 11)

        funds
            .transfer(transferAmount)
            .withClientTransactionId("")
            .withDescription(description)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    fun resetScreen() = screenModel.update { TransferFundsScreenModel() }

    companion object {
        private val StartDate = DateUtils.addDays(Date(), -360 * 5)
        private val EndDate = Date()
    }
}

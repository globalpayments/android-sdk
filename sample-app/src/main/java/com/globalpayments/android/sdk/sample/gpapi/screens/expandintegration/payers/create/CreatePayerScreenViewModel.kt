package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.payers.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Customer
import com.global.api.utils.GenerationUtils
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePayerScreenViewModel : ViewModel() {

    val screenModel = MutableStateFlow(CreatePayerScreenModel())

    fun onFirstNameChanged(value: String) = screenModel.update { it.copy(firstName = value) }
    fun onLastNameChanged(value: String) = screenModel.update { it.copy(lastName = value) }
    fun resetScreen() = screenModel.update { CreatePayerScreenModel() }

    fun createPayer() {
        viewModelScope.launch(Dispatchers.IO) {
            val model = screenModel.value
            try {
                val customer = getCustomerFromModel(model)
                val response = createPayer(customer)

                val resultToShow = listOf(
                    "ID" to response.id,
                )
                val sampleResponse = GPSampleResponseModel(
                    transactionId = response.id,
                    response = listOf(
                        "Key" to response.key,
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

    private fun getCustomerFromModel(model: CreatePayerScreenModel): Customer {
        return Customer().apply {
            key = GenerationUtils.generateOrderId()
            firstName = model.firstName
            lastName = model.lastName
        }
    }

    private suspend fun createPayer(payer: Customer) = withContext(Dispatchers.IO) {
        payer.create()
    }
}

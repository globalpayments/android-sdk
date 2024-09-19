package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.payers.edit

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

class EditPayerScreenViewModel : ViewModel() {

    val screenModel = MutableStateFlow(EditPayerScreenModel())

    fun onIdChanged(value: String) = screenModel.update { it.copy(id = value) }
    fun onFirstNameChanged(value: String) = screenModel.update { it.copy(firstName = value) }
    fun onLastNameChanged(value: String) = screenModel.update { it.copy(lastName = value) }
    fun resetScreen() = screenModel.update { EditPayerScreenModel() }

    fun editPayer() {
        viewModelScope.launch(Dispatchers.IO) {
            val model = screenModel.value
            try {
                val customer = newCustomer(model)
                val response = editPayer(customer)

                val resultToShow = listOf(
                    "ID" to response.id,
                )
                val sampleResponse = GPSampleResponseModel(
                    transactionId = response.id,
                    response = listOf(
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

    private fun newCustomer(model: EditPayerScreenModel): Customer {
        val customer = Customer()
        customer.id = model.id
        customer.key = GenerationUtils.generateOrderId()
        customer.firstName = model.firstName
        customer.lastName = model.lastName

        return customer
    }

    private suspend fun editPayer(payer: Customer) = withContext(Dispatchers.IO) {
        payer.saveChanges()
    }
}

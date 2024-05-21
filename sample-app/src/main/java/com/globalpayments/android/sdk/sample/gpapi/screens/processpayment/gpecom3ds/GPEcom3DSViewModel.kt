package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.gpecom3ds

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.Address
import com.global.api.entities.Customer
import com.global.api.entities.Transaction
import com.global.api.paymentMethods.CreditCardData
import com.global.api.paymentMethods.RecurringPaymentMethod
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.netcetera.NetceteraInstanceHolder
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import com.globalpayments.android.sdk.sample.repository.Secure3DSRepository
import com.globalpayments.android.sdk.sample.utils.AppPreferences
import com.globalpayments.android.sdk.sample.utils.configuration.GPEcomConfiguration
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GPEcom3DSViewModel(context: Context) : ViewModel() {

    private val secure3DSRepository = Secure3DSRepository()
    private val calendar: Calendar by lazy { Calendar.getInstance() }
    private val sharedAppPreferences = AppPreferences(context)

    val screenModel: MutableStateFlow<GPEcom3DSScreenModel> = MutableStateFlow(GPEcom3DSScreenModel())

    init {
        val currentConfig = sharedAppPreferences.gpEcomConfiguration ?: GPEcomConfiguration.fromBuildConfig()

        viewModelScope.launch(Dispatchers.IO) {
            NetceteraInstanceHolder.init3DSService(context, currentConfig.apiKey ?: "")
        }
    }

    fun onAmountChanged(value: String) = screenModel.update { it.copy(amount = value) }
    fun onCardNumberChanged(value: String) = screenModel.update { it.copy(cardNumber = value) }
    fun onCvvChanged(value: String) = screenModel.update { it.copy(cardCVV = value) }
    fun updateCardExpirationDate(date: Date) = screenModel.update {
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        it.copy(cardYear = year.toString(), cardMonth = if (month < 10) "0$month" else month.toString())
    }

    fun onMakePaymentRecurring(value: Boolean) = screenModel.update { it.copy(makePaymentRecurring = value) }

    fun onCardHolderName(value: String) = screenModel.update { it.copy(cardHolderName = value) }

    fun makePayment() {
        viewModelScope.launch(Dispatchers.IO) {
            val model = screenModel.value
            makePayment(model)
        }
    }

    private fun customerId(): String =
        java.lang.String.format("%s-Realex", SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault()).format(Date()))

    private fun paymentId(type: String) =
        java.lang.String.format("%s-Realex-%s", SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault()).format(Date()), type)

    private fun newAddress(): Address {
        return Address().apply {
            this.streetAddress1 = "Flat 123"
            this.streetAddress2 = "House 456"
            this.streetAddress3 = "The Cul-De-Sac"
            this.city = "Halifax"
            this.province = "West Yorkshire"
            this.postalCode = "W6 9HR"
            this.country = "United Kingdom"
        }
    }

    private fun newCustomer(): Customer {
        return Customer().apply {
            this.title = "Mr."
            this.firstName = "James"
            this.lastName = "Mason"
            this.company = "Realex Payments"
            this.address = newAddress()
            this.homePhone = "+35312345678"
            this.workPhone = "+3531987654321"
            this.fax = "+124546871258"
            this.mobilePhone = "+25544778544"
            this.email = "text@example.com"
            this.comments = "Campaign Ref E7373G"
            this.key = customerId()
        }
    }

    private suspend fun makePayment(model: GPEcom3DSScreenModel) {
        val amount = model.amount.toBigDecimalOrNull() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val card = CreditCardData().apply {
                    this.number = model.cardNumber
                    this.expMonth = model.cardMonth.toInt()
                    this.expYear = model.cardYear.toInt()
                    this.cardHolderName = model.cardHolderName
                    this.isCardPresent = true
                }

                try {
                    val customer = async { newCustomer() }
                    val paymentId = paymentId("Credit")
                    val customerCreate = async { customer.await().create() }.await()
                    val paymentCreate = async { customer.await().addPaymentMethod(paymentId, card).create() }.await()
                    val recurringPaymentMethod = RecurringPaymentMethod(customer.await().key, paymentId)
                    val getEcomCardEnrolmentCheck = async {
                        secure3DSRepository.getEcomCardEnrolmentCheck(recurringPaymentMethod, amount, Currency)
                    }

                    if (!getEcomCardEnrolmentCheck.await().isEnrolled) {
                        chargeMoney(recurringPaymentMethod, amount)
                        return@launch
                    }

                    val transaction = NetceteraInstanceHolder
                        .createTransactionFromCardNumber(
                            card.number,
                            getEcomCardEnrolmentCheck.await().acsEndVersion
                        )

                    val netceteraAuthenticationParams = transaction.authenticationRequestParameters
                    val authenticationResponse = async {
                        secure3DSRepository.initiateAuthentication(
                            getEcomCardEnrolmentCheck.await(),
                            recurringPaymentMethod,
                            amount,
                            Address(),
                            Address(),
                            netceteraAuthenticationParams,
                            Currency,
                            customer.await().email
                        )
                    }

                    if (authenticationResponse.await().status != CHALLENGE_REQUIRED) {
                        chargeMoney(recurringPaymentMethod, amount)
                        return@launch
                    }

                    val netceteraEcomParams = NetceteraEcomParams(
                        transaction,
                        authenticationResponse.await(),
                        recurringPaymentMethod,
                        amount
                    )

                    screenModel.update { it.copy(netceteraTransactionParams = netceteraEcomParams) }
                } catch (exception: Exception) {
                    onError(exception)
                }
            } catch (exception: Exception) {
                onError(exception)
            }
        }
    }

    fun doChallenge(
        activity: Activity,
        netceteraEcomParams: NetceteraEcomParams
    ) {
        viewModelScope.launch {
            try {
                val (transaction, initAuthenticationResponse, recurringPaymentMethod, amount) = netceteraEcomParams
                val challengeParams = ChallengeParameters().apply {
                    acsRefNumber = initAuthenticationResponse.acsReferenceNumber
                    acsSignedContent = initAuthenticationResponse.payerAuthenticationRequest
                    acsTransactionID = initAuthenticationResponse.acsTransactionId
                    set3DSServerTransactionID(initAuthenticationResponse.serverTransactionId)
                }
                with(NetceteraInstanceHolder) { transaction.startChallenge(activity, challengeParams) }
                chargeMoney(recurringPaymentMethod, amount)
            } catch (exception: Exception) {
                onError(exception)
            }
        }
    }

    private suspend fun chargeMoney(
        paymentMethod: RecurringPaymentMethod,
        amount: BigDecimal,
    ) = withContext(Dispatchers.IO) {

        var response = secure3DSRepository.chargeMoney(paymentMethod, amount, Currency)
        if (screenModel.value.makePaymentRecurring) {
            response = secure3DSRepository.makePaymentRecurring(
                paymentMethod,
                response.cardBrandTransactionId,
                amount,
                Currency
            )
        }

        val resultToShow = response.mapNotNullFields()
        val sampleResponse = GPSampleResponseModel(
            transactionId = response.transactionId, response = listOf(
                "Time" to response.timestamp, "Status" to response.responseMessage
            )
        )
        val gpSnippetResponseModel = GPSnippetResponseModel(Transaction::class.java.simpleName, resultToShow)
        screenModel.update {
            it.copy(
                gpSampleResponseModel = sampleResponse,
                gpSnippetResponseModel = gpSnippetResponseModel,
                netceteraTransactionParams = null
            )
        }
    }

    private fun onError(exception: Exception) {
        screenModel.update {
            val gpSnippetResponseModel =
                GPSnippetResponseModel(Transaction::class.java.simpleName, listOf("Error" to (exception.message ?: "")), true)
            it.copy(
                gpSnippetResponseModel = gpSnippetResponseModel,
                gpSampleResponseModel = null,
                netceteraTransactionParams = null
            )
        }
    }

    fun resetScreen() {
        screenModel.update { GPEcom3DSScreenModel() }
    }

    companion object {
        private const val Currency = "GBP"
        private const val CHALLENGE_REQUIRED = "CHALLENGE_REQUIRED"
    }
}

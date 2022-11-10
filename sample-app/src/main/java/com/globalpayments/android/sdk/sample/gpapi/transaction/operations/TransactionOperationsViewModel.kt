package com.globalpayments.android.sdk.sample.gpapi.transaction.operations

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.global.api.ServicesContainer
import com.global.api.builders.AuthorizationBuilder
import com.global.api.entities.*
import com.global.api.entities.enums.Channel
import com.global.api.entities.enums.LodgingItemType
import com.global.api.entities.enums.ManualEntryMethod
import com.global.api.entities.enums.PaymentMethodProgram
import com.global.api.gateways.GpApiConnector
import com.global.api.paymentMethods.CreditCardData
import com.global.api.utils.DateUtils
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationModel
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationType
import com.globalpayments.android.sdk.sample.utils.FingerPrintUsageMethod
import com.globalpayments.android.sdk.sample.utils.ManualEntryMethodUsageMode
import com.globalpayments.android.sdk.sample.utils.PaymentMethodUsageMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.math.BigDecimal

class TransactionOperationsViewModel : BaseViewModel() {
    val transactionLiveData = MutableLiveData<Transaction>()
    val transactionLiveDataError = MutableLiveData<ThreeDSecure>()
    val transactionMessageError = MutableLiveData<String>()

    val transactionType = MutableLiveData<TransactionOperationType?>()
    private val card: CreditCardData = CreditCardData()

    private var transactionOperationModel: TransactionOperationModel? = null

    private fun setCreditCardData(cardNumber: String, expiryMonth: Int, expiryYear: Int, cvnCvv: String) {
        card.apply {
            number = cardNumber
            expMonth = expiryMonth
            expYear = expiryYear
            cvn = cvnCvv
        }
    }

    fun executeTransactionOperation(transactionOperationModel: TransactionOperationModel) {
        this.transactionOperationModel = transactionOperationModel
        setCreditCardData(
            transactionOperationModel.cardNumber,
            transactionOperationModel.expiryMonth,
            transactionOperationModel.expiryYear,
            transactionOperationModel.cvnCvv
        )
        val cardData = finishTransactionOperation(transactionOperationModel)
        val customer = if (transactionOperationModel.fingerPrintSelection)
            FingerPrintUsageMethod.fingerPrintSelectedOption(transactionOperationModel.fingerprintMethodUsageMode)
        else
            null

        when (transactionOperationModel.transactionOperationType) {
            TransactionOperationType.Authorization -> authorize(transactionOperationModel, cardData, customer)
            TransactionOperationType.Sale -> sale(transactionOperationModel, cardData, customer)
            TransactionOperationType.Refund -> refund(transactionOperationModel, cardData, customer)
            else -> {
                //no-op
            }
        }
    }

    private fun manualEntryMethodOption(transactionOperationModel: TransactionOperationModel): CreditCardData {
        val channel = (ServicesContainer.getInstance().getGateway(Constants.DEFAULT_GPAPI_CONFIG) as? GpApiConnector)?.gpApiConfig?.channel
        val manualEntryMethodUsageMode = transactionOperationModel.manualEntryMethodUsageMode
        if (Channel.CardNotPresent.value == channel) {
            when (manualEntryMethodUsageMode) {
                ManualEntryMethodUsageMode.None -> {
                    card.number = transactionOperationModel.cardNumber
                    card.expMonth = transactionOperationModel.expiryMonth
                    card.expYear = transactionOperationModel.expiryYear
                    card.cvn = transactionOperationModel.cvnCvv
                }
                ManualEntryMethodUsageMode.Moto -> {
                    card.number = transactionOperationModel.cardNumber
                    card.expMonth = transactionOperationModel.expiryMonth
                    card.expYear = transactionOperationModel.expiryYear
                    card.cvn = transactionOperationModel.cvnCvv
                    card.entryMethod = ManualEntryMethod.Moto
                }
                ManualEntryMethodUsageMode.Mail -> {
                    card.number = transactionOperationModel.cardNumber
                    card.expMonth = transactionOperationModel.expiryMonth
                    card.expYear = transactionOperationModel.expiryYear
                    card.cvn = transactionOperationModel.cvnCvv
                    card.entryMethod = ManualEntryMethod.Mail
                }
                ManualEntryMethodUsageMode.Phone -> {
                    card.number = transactionOperationModel.cardNumber
                    card.expMonth = transactionOperationModel.expiryMonth
                    card.expYear = transactionOperationModel.expiryYear
                    card.cvn = transactionOperationModel.cvnCvv
                    card.entryMethod = ManualEntryMethod.Phone
                }
                else -> {}
            }
        } else {
            card.isCardPresent = true
        }
        return card
    }

    private fun finishTransactionOperation(transactionOperationModel: TransactionOperationModel): CreditCardData {
        val token: String
        var tokenizedCard: CreditCardData? = CreditCardData()
        when (transactionOperationModel.paymentMethodUsageMode) {
            PaymentMethodUsageMode.No -> tokenizedCard = manualEntryMethodOption(transactionOperationModel)
            PaymentMethodUsageMode.Single_use_token -> {
                token = card.tokenize(
                    true,
                    Constants.DEFAULT_GPAPI_CONFIG,
                    com.global.api.entities.enums.PaymentMethodUsageMode.SINGLE
                )
                tokenizedCard = CreditCardData()
                tokenizedCard.token = token
                tokenizedCard = manualEntryMethodOption(transactionOperationModel)
            }
            PaymentMethodUsageMode.Multiple_use_token -> {
                token = card.tokenize(
                    true,
                    Constants.DEFAULT_GPAPI_CONFIG,
                    com.global.api.entities.enums.PaymentMethodUsageMode.MULTIPLE
                )
                tokenizedCard = CreditCardData()
                tokenizedCard.token = token
                tokenizedCard = manualEntryMethodOption(transactionOperationModel)
            }
        }
        return tokenizedCard
    }

    private fun authorize(
        transactionOperationModel: TransactionOperationModel,
        creditCardData: CreditCardData,
        customer: Customer?
    ) {
        viewModelScope.launchPrintingError {
            val transaction = creditCardData
                .authorize(transactionOperationModel.amount)
                .withCurrency(transactionOperationModel.currency)
                .withDynamicDescriptor(transactionOperationModel.dynamicDescriptor)
                .withPaymentLinkId(transactionOperationModel.paymentLinkId)
                .withCustomerIfNotNull(customer)
                .withRequestMultiUseToken(transactionOperationModel.requestMultiUseToken)
                .withIdempotencyKey(transactionOperationModel.idempotencyKey)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            transactionLiveData.postValue(transaction)
            transactionType.postValue(TransactionOperationType.Authorization)
        }
    }

    private fun sale(
        transactionOperationModel: TransactionOperationModel,
        creditCardData: CreditCardData,
        customer: Customer?
    ) {
        viewModelScope.launchPrintingError {
            val transaction = creditCardData.charge(transactionOperationModel.amount)
                .withCurrency(transactionOperationModel.currency)
                .withDynamicDescriptor(transactionOperationModel.dynamicDescriptor)
                .withPaymentLinkId(transactionOperationModel.paymentLinkId)
                .withCustomerIfNotNull(customer)
                .withRequestMultiUseToken(transactionOperationModel.requestMultiUseToken)
                .withIdempotencyKey(transactionOperationModel.idempotencyKey)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            transactionLiveData.postValue(transaction)
            transactionType.postValue(TransactionOperationType.Sale)
        }
    }

    private fun refund(
        transactionOperationModel: TransactionOperationModel,
        creditCardData: CreditCardData,
        customer: Customer?
    ) {
        viewModelScope.launchPrintingError {
            val transaction = creditCardData
                .refund(transactionOperationModel.amount)
                .withCurrency(transactionOperationModel.currency)
                .withDynamicDescriptor(transactionOperationModel.dynamicDescriptor)
                .withPaymentLinkId(transactionOperationModel.paymentLinkId)
                .withCustomerIfNotNull(customer)
                .withRequestMultiUseToken(transactionOperationModel.requestMultiUseToken)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            transactionLiveData.postValue(transaction)
            transactionType.postValue(TransactionOperationType.Refund)
        }
    }

    fun captureTransaction() {
        val lastTransaction = transactionLiveData.value ?: return
        val transactionOperationModel = transactionOperationModel ?: return
        viewModelScope.launchPrintingError {
            val transaction = lastTransaction
                .capture(transactionOperationModel.amount)
                .withIdempotencyKey(transactionOperationModel.idempotencyKey)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            transactionLiveData.postValue(transaction)
            transactionType.postValue(TransactionOperationType.Capture)
        }
    }

    fun refundCapture() {
        val lastTransaction = transactionLiveData.value ?: return
        val transactionOperationModel = transactionOperationModel ?: return
        viewModelScope.launchPrintingError {
            val transaction = lastTransaction
                .refund(transactionOperationModel.amount)
                .withIdempotencyKey(transactionOperationModel.idempotencyKey)
                .withCurrency(transactionOperationModel.currency)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            transactionLiveData.postValue(transaction)
            transactionType.postValue(null)
        }
    }

    fun reverseCapture() {
        val lastTransaction = transactionLiveData.value ?: return
        val transactionOperationModel = transactionOperationModel ?: return
        viewModelScope.launchPrintingError {
            val transaction = lastTransaction
                .reverse(transactionOperationModel.amount)
                .withIdempotencyKey(transactionOperationModel.idempotencyKey)
                .withCurrency(transactionOperationModel.currency)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            transactionLiveData.postValue(transaction)
            transactionType.postValue(null)
        }
    }

    fun reverseAuthorization() {
        val lastTransaction = transactionLiveData.value ?: return
        val transactionOperationModel = transactionOperationModel ?: return
        viewModelScope.launchPrintingError {
            val transaction = lastTransaction
                .reverse(transactionOperationModel.amount)
                .withIdempotencyKey(transactionOperationModel.idempotencyKey)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            transactionLiveData.postValue(transaction)
            transactionType.postValue(TransactionOperationType.ReverseAuthorization)
        }
    }

    fun reauthorize() {
        val lastTransaction = transactionLiveData.value ?: return
        val transactionOperationModel = transactionOperationModel ?: return
        viewModelScope.launchPrintingError {
            val transaction = lastTransaction
                .reauthorize(transactionOperationModel.amount)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            transactionLiveData.postValue(transaction)
            transactionType.postValue(null)
        }
    }

    fun increment() {
        val lastTransaction = transactionLiveData.value ?: return
        val transactionOperationModel = transactionOperationModel ?: return
        viewModelScope.launchPrintingError {

            val lodgingInfo = LodgingData()
            lodgingInfo.bookingReference = "s9RpaDwXq1sPRkbP"
            lodgingInfo.stayDuration = 10
            lodgingInfo.checkInDate = DateTime.now()
            lodgingInfo.checkOutDate =
                DateTime(DateUtils.addDays(DateTime.now().toDate(), 7))
            lodgingInfo.rate = BigDecimal("13.49")
            val items: MutableList<LodgingItems> = ArrayList()
            items.add(
                LodgingItems().setTypes(LodgingItemType.NO_SHOW.toString())
                    .setReference("item_1").setTotalAmount("13.49")
                    .setPaymentMethodProgramCodes(arrayOf(PaymentMethodProgram.PURCHASE.toString()))
            )
            lodgingInfo.items = items
            val incrementTransaction = lastTransaction.additionalAuth(10.0)
                .withCurrency(transactionOperationModel.currency)
                .withLodgingData(lodgingInfo)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            transactionLiveData.postValue(incrementTransaction)
            transactionType.postValue(TransactionOperationType.Increment)
        }
    }

    fun captureIncrement() {
        val lastTransaction = transactionLiveData.value ?: return
        val transactionOperationModel = transactionOperationModel ?: return
        viewModelScope.launchPrintingError {
            val transaction = lastTransaction
                .capture()
                .withIdempotencyKey(transactionOperationModel.idempotencyKey)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            transactionLiveData.postValue(transaction)
            transactionType.postValue(null)
        }
    }

    fun reverseIncrement() {
        val lastTransaction = transactionLiveData.value ?: return
        val transactionOperationModel = transactionOperationModel ?: return
        viewModelScope.launchPrintingError {
            val transaction = lastTransaction
                .reverse()
                .withIdempotencyKey(transactionOperationModel.idempotencyKey)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            transactionLiveData.postValue(transaction)
            transactionType.postValue(null)
        }
    }

    fun refundSale() {
        val saleTransaction = transactionLiveData.value ?: return
        val transactionOperationModel = transactionOperationModel ?: return
        viewModelScope.launchPrintingError {
            val transaction = saleTransaction
                .refund(transactionOperationModel.amount)
                .withIdempotencyKey(transactionOperationModel.idempotencyKey)
                .withCurrency(transactionOperationModel.currency)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            transactionLiveData.postValue(transaction)
            transactionType.postValue(null)
        }
    }

    fun reverseSale() {
        val saleTransaction = transactionLiveData.value ?: return
        val transactionOperationModel = transactionOperationModel ?: return
        viewModelScope.launchPrintingError {
            val transaction = saleTransaction
                .reverse(transactionOperationModel.amount)
                .withIdempotencyKey(transactionOperationModel.idempotencyKey)
                .withCurrency(transactionOperationModel.currency)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            transactionLiveData.postValue(transaction)
            transactionType.postValue(null)
        }
    }

    fun reverseRefund() {
        val refundTransaction = transactionLiveData.value ?: return
        val transactionOperationModel = transactionOperationModel ?: return
        viewModelScope.launchPrintingError {
            val transaction = refundTransaction
                .reverse(transactionOperationModel.amount)
                .withIdempotencyKey(transactionOperationModel.idempotencyKey)
                .withCurrency(transactionOperationModel.currency)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            transactionLiveData.postValue(transaction)
            transactionType.postValue(null)
        }
    }

    private fun CoroutineScope.launchPrintingError(block: () -> Unit) {
        launch(Dispatchers.IO) {
            showProgress()
            try {
                block()
                hideProgress()
            } catch (exception: Exception) {
                showError(exception)
            }
        }
    }

    private fun AuthorizationBuilder.withCustomerIfNotNull(customer: Customer?) =
        if (customer != null) this.withCustomerData(customer) else this


}

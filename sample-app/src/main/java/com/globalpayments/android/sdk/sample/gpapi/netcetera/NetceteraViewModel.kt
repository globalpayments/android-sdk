package com.globalpayments.android.sdk.sample.gpapi.netcetera

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.global.api.entities.MobileData
import com.global.api.entities.StoredCredential
import com.global.api.entities.ThreeDSecure
import com.global.api.entities.Transaction
import com.global.api.entities.enums.*
import com.global.api.paymentMethods.CreditCardData
import com.global.api.services.Secure3dService
import com.global.api.utils.JsonDoc
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel
import com.globalpayments.android.sdk.task.TaskExecutor
import com.globalpayments.android.sdk.task.TaskResult
import com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters
import com.netcetera.threeds.sdk.api.utils.DsRidValues
import org.joda.time.DateTime
import java.math.BigDecimal

class NetceteraViewModel : BaseViewModel() {

    private val storedCredential = StoredCredential()
    private var tokenizedCard = CreditCardData()
    private var cardBrand: String? = null

    val startChallengeFlow = MutableLiveData<ThreeDSecure>()
    val paymentCompleted = MutableLiveData<Transaction>()
    val createNetceteraTransaction = MutableLiveData<ThreeDSecure>()
    val dccRatesReceived = MutableLiveData<Transaction?>()

    var shouldUseDccRate: Boolean = false

    var amount: BigDecimal? = null

    fun getDCCRate(creditCardData: CreditCardData) {
        shouldUseDccRate = false
        dccRatesReceived.postValue(null)
        TaskExecutor.executeAsync(
            taskToExecute = {
                creditCardData.getDccRate()
                    .withAmount(amount)
                    .withCurrency(Currency)
                    .execute(Constants.DCC_RATE_GP_API_CONFIG_NAME);
            },
            onFinished = {
                when (it) {
                    is TaskResult.Error -> {
                        Log.d("NetceteraViewModel", "Failed to retrieve dcc rates")
                    }
                    is TaskResult.Success -> dccRatesReceived.postValue(it.data)
                }
            }
        )
    }

    //1. Capture Cardholder Data
    fun tokenizeCard(creditCardData: CreditCardData) {
        showProgress()
        TaskExecutor.executeAsync(
            taskToExecute = {
                val response = creditCardData
                    .tokenize(true, PaymentMethodUsageMode.SINGLE)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)
                cardBrand = response.cardType
                response.token
            },
            onFinished = {
                when (it) {
                    is TaskResult.Error -> showError(it.exception.message)
                    is TaskResult.Success -> checkEnrollment(it.data)
                }
            }
        )
    }

    //2. Send Cardholder data to server
    private fun checkEnrollment(cardToken: String) {
        tokenizedCard = CreditCardData(cardToken)
        TaskExecutor.executeAsync(
            taskToExecute = {
                Secure3dService
                    .checkEnrollment(tokenizedCard)
                    .withCurrency(Currency)
                    .withAmount(amount)
                    .withAuthenticationSource(AuthenticationSource.MobileSDK)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)
            }, onFinished = {
                when (it) {
                    is TaskResult.Error -> showError(it.exception.message)
                    is TaskResult.Success -> do3Auth(it.data)
                }
            }
        )
    }


    //5. If 3DS2 proceed
    private fun do3Auth(secureEcom: ThreeDSecure) {
        if (secureEcom.enrolledStatus != ENROLLED) {
            chargeMoney()
            return
        }
        createNetceteraTransaction.postValue(secureEcom)
    }

    //12. Send Authentication Parameters to server
    fun authenticateTransaction(
        secureEcom: ThreeDSecure,
        netceteraParams: AuthenticationRequestParameters
    ) {
        TaskExecutor.executeAsync(
            taskToExecute = {
                // Initiate authentication
                Secure3dService
                    .initiateAuthentication(tokenizedCard, secureEcom)
                    .withAuthenticationSource(AuthenticationSource.MobileSDK)
                    .withAmount(amount)
                    .withCurrency(Currency)
                    .withStoredCredential(storedCredential)
                    .withOrderCreateDate(DateTime.now())
                    .withMobileData(MobileData().apply {
                        applicationReference = netceteraParams.sdkAppID
                        sdkTransReference = netceteraParams.sdkTransactionID
                        referenceNumber = netceteraParams.sdkReferenceNumber
                        sdkInterface = SdkInterface.Both
                        encodedData = netceteraParams.deviceData
                        maximumTimeout = 15
                        ephemeralPublicKey = JsonDoc.parse(netceteraParams.sdkEphemeralPublicKey)
                        setSdkUiTypes(*SdkUiType.values())
                    })
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)
            },
            onFinished = {
                when (it) {
                    is TaskResult.Error -> showError(it.exception.message)
                    is TaskResult.Success -> startChallengeFlow.postValue(it.data)
                }
            }
        )
    }

    fun getDsRidValuesForCard(): String? {
        return when (cardBrand?.lowercase()) {
            "visa" -> DsRidValues.VISA
            "mastercard" -> DsRidValues.MASTERCARD
            "amex" -> DsRidValues.AMEX
            "dinners" -> DsRidValues.DINERS
            "union" -> DsRidValues.UNION
            "jcb" -> DsRidValues.JCB
            "cb" -> DsRidValues.CB
            else -> null
        }
    }

    //23.
    fun doAuth(serverTransactionId: String) {
        TaskExecutor.executeAsync(
            taskToExecute = {
                Secure3dService
                    .getAuthenticationData()
                    .withServerTransactionId(serverTransactionId)
                    .execute(Secure3dVersion.TWO, Constants.DEFAULT_GPAPI_CONFIG)
            },
            onFinished = {
                when (it) {
                    is TaskResult.Error -> showError(it.exception.message)
                    is TaskResult.Success -> {
                        tokenizedCard.threeDSecure = it.data
                        chargeMoney()
                    }
                }
            }
        )
    }

    private fun chargeMoney() {
        TaskExecutor.executeAsync(
            taskToExecute = {
                tokenizedCard
                    .charge(amount)
                    .withCurrency(Currency)
                    .let {
                        if (shouldUseDccRate) {
                            it.withDccRateData(dccRatesReceived.value?.dccRateData)
                        } else {
                            it
                        }
                    }
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)
            },
            onFinished = {
                when (it) {
                    is TaskResult.Error -> showError(it.exception.message)
                    is TaskResult.Success -> {
                        paymentCompleted.postValue(it.data)
                        hideProgress()
                    }
                }
            }
        )


    }

    companion object {
        private const val Currency = "GBP"
        private const val ENROLLED = "ENROLLED"
    }
}
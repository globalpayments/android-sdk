package com.globalpayments.android.sdk.sample.gpapi.netcetera

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.global.api.entities.MobileData
import com.global.api.entities.StoredCredential
import com.global.api.entities.ThreeDSecure
import com.global.api.entities.Transaction
import com.global.api.entities.enums.AuthenticationSource
import com.global.api.entities.enums.SdkInterface
import com.global.api.entities.enums.SdkUiType
import com.global.api.entities.enums.Secure3dVersion
import com.global.api.entities.enums.StoredCredentialInitiator
import com.global.api.entities.enums.StoredCredentialReason
import com.global.api.entities.enums.StoredCredentialSequence
import com.global.api.entities.enums.StoredCredentialType
import com.global.api.paymentMethods.CreditCardData
import com.global.api.services.GpApiService
import com.global.api.services.Secure3dService
import com.global.api.utils.JsonDoc
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfiguration
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfigurationUtils.buildDefaultGpApiConfig
import com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters
import com.netcetera.threeds.sdk.api.utils.DsRidValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.math.BigDecimal

class NetceteraViewModel : BaseViewModel() {

    private val storedCredential = StoredCredential()
    private var tokenizedCard = CreditCardData()
    private var cardBrand: String? = null

    var makePaymentRecurring: Boolean = false

    val accessToken = MutableLiveData<String>()
    val startChallengeFlow = MutableLiveData<ThreeDSecure>()
    val paymentCompleted = MutableLiveData<Transaction>()
    val createNetceteraTransaction = MutableLiveData<ThreeDSecure>()

    fun getAccessToken() {
        showProgress()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accessToken = GpApiService.generateTransactionKey(buildDefaultGpApiConfig(GPAPIConfiguration.createDefaultConfig())).accessToken
                this@NetceteraViewModel.accessToken.postValue(accessToken)
            } catch (exception: Exception) {
                showError(exception)
            } finally {
                hideProgress()
            }
        }
    }

    //2. Send Cardholder data to server
    fun checkEnrollment(cardToken: String, cardType: String) {
        showProgress()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cardBrand = cardType
                tokenizedCard = CreditCardData(cardToken)
                val transaction = Secure3dService
                    .checkEnrollment(tokenizedCard)
                    .withCurrency(Currency)
                    .withAmount(BigDecimal(Amount))
                    .withAuthenticationSource(AuthenticationSource.MobileSDK)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)
                do3Auth(transaction)
            } catch (exception: Exception) {
                showError(exception)
                hideProgress()
            }
        }
    }


    //5. If 3DS2 proceed
    private fun do3Auth(secureEcom: ThreeDSecure) {
        if (secureEcom.enrolledStatus != ENROLLED) {
            chargeMoney()
            return
        }
        hideProgress()
        createNetceteraTransaction.postValue(secureEcom)
    }

    //12. Send Authentication Parameters to server
    fun authenticateTransaction(
        secureEcom: ThreeDSecure,
        netceteraParams: AuthenticationRequestParameters
    ) {
        showProgress()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Initiate authentication
                val transaction = Secure3dService
                    .initiateAuthentication(tokenizedCard, secureEcom)
                    .withAuthenticationSource(AuthenticationSource.MobileSDK)
                    .withAmount(BigDecimal(Amount))
                    .withCurrency(Currency)
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

                startChallengeFlow.postValue(transaction)
            } catch (exception: Exception) {
                showError(exception)
            } finally {
                hideProgress()
            }
        }
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
        showProgress()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val transaction = Secure3dService
                    .getAuthenticationData()
                    .withServerTransactionId(serverTransactionId)
                    .execute(Secure3dVersion.TWO, Constants.DEFAULT_GPAPI_CONFIG)
                tokenizedCard.threeDSecure = transaction
                chargeMoney()
            } catch (exception: Exception) {
                showError(exception)
                hideProgress()
            }
        }
    }

    private fun chargeMoney() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val transaction = tokenizedCard
                    .charge(BigDecimal(Amount))
                    .withCurrency(Currency)
                    .execute(Constants.DEFAULT_GPAPI_CONFIG)
                if (makePaymentRecurring) {
                    makePaymentRecurring(transaction)
                } else {
                    paymentCompleted.postValue(transaction)
                    hideProgress()
                }
            } catch (exception: Exception) {
                showError(exception)
                hideProgress()
            }
        }
    }

    private fun makePaymentRecurring(chargeResponse: Transaction) {
        try {
            storedCredential.apply {
                initiator = StoredCredentialInitiator.CardHolder
                type = StoredCredentialType.Recurring
                sequence = StoredCredentialSequence.Subsequent
                reason = StoredCredentialReason.Incremental
            }
            val transaction = tokenizedCard
                .charge(BigDecimal(Amount))
                .withCurrency(Currency)
                .withStoredCredential(storedCredential)
                .withCardBrandStorage(StoredCredentialInitiator.Merchant, chargeResponse.cardBrandTransactionId)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            paymentCompleted.postValue(transaction)
        } catch (exception: Exception) {
            showError(exception)
        } finally {
            hideProgress()
        }
    }

    companion object {
        private const val Amount = "100"
        private const val Currency = "GBP"
        private const val ENROLLED = "ENROLLED"
    }
}

package com.globalpayments.android.sdk.sample.repository

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
import com.global.api.services.Secure3dService
import com.global.api.utils.JsonDoc
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.repository.models.CardEnrollmentCheck
import com.globalpayments.android.sdk.sample.repository.models.InitAuthenticationResponse
import com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.math.BigDecimal

class Secure3DSRepository {

    suspend fun tokenizeCard(
        cardNumber: String,
        expMonth: Int,
        expYear: Int,
        cardHolderName: String
    ): String = withContext(Dispatchers.IO) {
        val card = CreditCardData().apply {
            this.number = cardNumber
            this.expMonth = expMonth
            this.expYear = expYear
            this.cardHolderName = cardHolderName
            this.isCardPresent = true
        }
        card.tokenize(true, Constants.DEFAULT_GPAPI_CONFIG)
    }

    suspend fun checkCardEnrollment(
        cardToken: String,
        amount: BigDecimal,
        currency: String
    ): CardEnrollmentCheck = withContext(Dispatchers.IO) {
        coroutineScope {
            val tokenizedCard = CreditCardData(cardToken)

            val cardEnrollmentCheck = async {
                getCardEnrolmentCheck(tokenizedCard, amount, currency)
            }

            val cardDetails = async {
                tokenizedCard
                    .verify()
                    .withCurrency(currency)
                    .execute()
            }

            CardEnrollmentCheck(
                cardEnrollmentCheck.await().enrolledStatus == ENROLLED,
                cardEnrollmentCheck.await().messageVersion,
                cardDetails.await().cardDetails.brand
            )

        }
    }

    suspend fun initiateAuthentication(
        cardToken: String,
        amount: BigDecimal,
        netceteraParams: AuthenticationRequestParameters,
        currency: String
    ): InitAuthenticationResponse = withContext(Dispatchers.IO) {
        val tokenizedCard = CreditCardData(cardToken)
        val secureEcom = getCardEnrolmentCheck(tokenizedCard, amount, currency)
        val authenticationResponse = Secure3dService
            .initiateAuthentication(tokenizedCard, secureEcom)
            .withAuthenticationSource(AuthenticationSource.MobileSDK)
            .withAmount(amount)
            .withCurrency(currency)
            .withStoredCredential(StoredCredential())
            .withOrderCreateDate(DateTime.now())
            .withMobileData(MobileData().apply {
                applicationReference = netceteraParams.sdkAppID
                sdkTransReference = netceteraParams.sdkTransactionID
                referenceNumber = netceteraParams.sdkReferenceNumber
                sdkInterface = SdkInterface.Both
                encodedData = netceteraParams.deviceData
                maximumTimeout = 15
                ephemeralPublicKey = JsonDoc.parse(netceteraParams.sdkEphemeralPublicKey)
                setSdkUiTypes(*SdkUiType.entries.toTypedArray())
            })
            .execute(Constants.DEFAULT_GPAPI_CONFIG)

        InitAuthenticationResponse(
            authenticationResponse.status == CHALLENGE_REQUIRED,
            authenticationResponse.serverTransactionId,
            authenticationResponse.acsReferenceNumber,
            authenticationResponse.payerAuthenticationRequest,
            authenticationResponse.acsTransactionId,
            authenticationResponse.providerServerTransRef
        )
    }

    suspend fun chargeMoney(
        cardToken: String,
        amount: BigDecimal,
        currency: String,
        serverTransactionId: String? = null,
    ): Transaction = withContext(Dispatchers.IO) {
        val tokenizedCard = CreditCardData(cardToken)
        if (!serverTransactionId.isNullOrBlank()) {
            tokenizedCard.threeDSecure = getAuthenticationData(serverTransactionId)
        }
        tokenizedCard
            .charge(amount)
            .withCurrency(currency)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    suspend fun makePaymentRecurring(
        cardToken: String,
        cardBrandTransactionId: String,
        amount: BigDecimal,
        currency: String
    ): Transaction = withContext(Dispatchers.IO) {
        val storedCredential = StoredCredential().apply {
            initiator = StoredCredentialInitiator.CardHolder
            type = StoredCredentialType.Recurring
            sequence = StoredCredentialSequence.Subsequent
            reason = StoredCredentialReason.Incremental
        }
        val tokenizedCard = CreditCardData(cardToken)
        tokenizedCard
            .charge(amount)
            .withCurrency(currency)
            .withStoredCredential(storedCredential)
            .withCardBrandStorage(StoredCredentialInitiator.Merchant, cardBrandTransactionId)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    private suspend fun getAuthenticationData(serverTransactionId: String): ThreeDSecure = withContext(Dispatchers.IO) {
        Secure3dService
            .getAuthenticationData()
            .withServerTransactionId(serverTransactionId)
            .execute(Secure3dVersion.TWO, Constants.DEFAULT_GPAPI_CONFIG)
    }


    private suspend fun getCardEnrolmentCheck(
        tokenizedCard: CreditCardData,
        amount: BigDecimal,
        currency: String
    ): ThreeDSecure = withContext(Dispatchers.IO) {
        Secure3dService
            .checkEnrollment(tokenizedCard)
            .withCurrency(currency)
            .withAmount(amount)
            .withAuthenticationSource(AuthenticationSource.MobileSDK)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
    }

    companion object {
        private const val ENROLLED = "ENROLLED"
        private const val CHALLENGE_REQUIRED = "CHALLENGE_REQUIRED"
    }
}

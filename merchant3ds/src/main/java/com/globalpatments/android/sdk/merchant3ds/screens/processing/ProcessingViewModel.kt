package com.globalpatments.android.sdk.merchant3ds.screens.processing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globalpatments.android.sdk.merchant3ds.*
import com.globalpatments.android.sdk.merchant3ds.navigation.NavigationManager
import com.globalpatments.android.sdk.merchant3ds.netcetera.NetceteraHolder
import com.globalpatments.android.sdk.merchant3ds.networking.Merchant3DSApi
import com.globalpatments.android.sdk.merchant3ds.networking.models.request.*
import com.globalpatments.android.sdk.merchant3ds.networking.models.response.EphemeralPublicKey
import com.globalpatments.android.sdk.merchant3ds.screens.error.ErrorDirection
import com.globalpatments.android.sdk.merchant3ds.screens.purchase.PurchaseScreenDirection
import com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters
import com.netcetera.threeds.sdk.api.transaction.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ProcessingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val navigationManager: NavigationManager,
    private val netceteraHolder: NetceteraHolder,
    private val merchant3DSApi: Merchant3DSApi,
    private val json: Json
) : ViewModel() {

    var state by mutableStateOf(ProcessingScreenModel())
    private val cardToken: String = savedStateHandle.get<String>(ProcessingDirection.CardTokenKey)!!
    private val cardType: String = savedStateHandle.get<String>(ProcessingDirection.CardTypeKey)!!

    private var transaction: Transaction? = null

    init {
        checkEnrollment()
    }

    private fun checkEnrollment() = viewModelScope.launch {
        state = state.copy(isLoading = true)
        withContext(Dispatchers.IO) {
            try {
                val response = merchant3DSApi.checkEnrollment(
                    CheckEnrollmentRequest(cardToken, ProductPrice, currency = ProductCurrency)
                )
                when (EnrolledStatus.valueOf(response.enrolled)) {
                    EnrolledStatus.ENROLLED -> {
                        state = state.copy(
                            transactionModel = state.transactionModel.copy(
                                messageVersion = response.messageVersion,
                                serverTransactionId = response.serverTransactionId
                            )
                        )
                    }
                    EnrolledStatus.NOT_AVAILABLE -> throw  TryAnotherCardException()
                    EnrolledStatus.NOT_ENROLLED -> {
                        if (cardType.lowercase() == "amex") {
                            throw TryAnotherCardException()
                        }
                        doAuth(response.serverTransactionId)
                    }
                }
            } catch (exception: Exception) {
                handleException(exception)
            }
        }
    }


    fun init3DS(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val messageVersion =
                state.transactionModel.messageVersion.takeIf(String::isNotBlank) ?: return@launch
            state = state.copy(transactionModel = state.transactionModel.copy(messageVersion = ""))
            netceteraHolder.init3DS(context)
            transaction = netceteraHolder.createTransaction(cardType, messageVersion)
            val netceteraParams =
                transaction?.authenticationRequestParameters ?: throw  TryAnotherCardException()
            authenticateTransaction(netceteraParams)
        } catch (exception: Exception) {
            handleException(exception)
        }
    }

    private suspend fun authenticateTransaction(netceteraParams: AuthenticationRequestParameters) =
        withContext(Dispatchers.IO) {
            try {
                val serverTransactionId =
                    state.transactionModel.serverTransactionId.takeIf(String::isNotBlank)
                        ?: return@withContext
                val ephemeralPublicKey =
                    json.decodeFromString<EphemeralPublicKey>(netceteraParams.sdkEphemeralPublicKey)
                val response = merchant3DSApi.initiateAuthentication(
                    InitiateAuthenticationParams(
                        cardToken = cardToken,
                        amount = ProductPrice,
                        currency = ProductCurrency,
                        mobileData = MobileDataRequest(
                            ephemeralPublicKeyX = ephemeralPublicKey.x,
                            ephemeralPublicKeyY = ephemeralPublicKey.y,
                            maximumTimeout = 15,
                            sdkTransReference = netceteraParams.sdkTransactionID,
                            applicationReference = netceteraParams.sdkAppID,
                            sdkInterface = SdkInterface,
                            encodedData = netceteraParams.deviceData,
                            sdkUiTypes = SdkUiType,
                            referenceNumber = netceteraParams.sdkReferenceNumber
                        ),
                        threeDsecure = ThreeDsecureRequest(serverTransactionId)
                    )
                )
                when (ChallengeStatus.valueOf(response.status)) {
                    ChallengeStatus.CHALLENGE_REQUIRED -> {
                        state = state.copy(
                            startChallenge = true, transactionModel = state.transactionModel.copy(
                                acsReferenceNumber = response.acsReferenceNumber,
                                payerAuthenticationRequest = response.payerAuthenticationRequest
                                    ?: "",
                                acsTransactionId = response.acsTransactionId,
                                providerServerTransRef = response.serverTransactionId
                            )
                        )
                    }
                    ChallengeStatus.SUCCESS, ChallengeStatus.SUCCESS_ATTEMPT_MADE, ChallengeStatus.SUCCESS_AUTHENTICATED -> doAuth(response.serverTransactionId)
                    ChallengeStatus.FAILED, ChallengeStatus.NOT_AUTHENTICATED -> throw TryAnotherCardException()
                }
            } catch (exception: Exception) {
                handleException(exception)
            }
        }

    fun startChallenge(activity: Activity) = viewModelScope.launch {
        val transaction = this@ProcessingViewModel.transaction ?: return@launch
        state = state.copy(startChallenge = false)
        with(netceteraHolder) {
            try {
                transaction.startChallenge(
                    activity,
                    state.transactionModel.acsTransactionId,
                    state.transactionModel.payerAuthenticationRequest,
                    state.transactionModel.acsTransactionId,
                    state.transactionModel.providerServerTransRef
                )
                doAuth(state.transactionModel.serverTransactionId)
            } catch (exception: Exception) {
                handleException(exception)
            }
        }
    }

    private suspend fun doAuth(serverTransactionId: String) = withContext(Dispatchers.IO) {
        try {
            val authenticationData = merchant3DSApi.getAuthenticationData(
                GetAuthenticationDataRequest(serverTransactionId = serverTransactionId)
            )
            when (AuthenticationStatus.valueOf(authenticationData.status)) {
                AuthenticationStatus.SUCCESS_AUTHENTICATED, AuthenticationStatus.SUCCESS_ATTEMPT_MADE -> chargeMoney(
                    serverTransactionId = authenticationData.serverTransactionId
                )
                AuthenticationStatus.NOT_AUTHENTICATED, AuthenticationStatus.FAILED -> throw TryAnotherCardException()
            }
        } catch (exception: Exception) {
            handleException(exception)
        }
    }

    private suspend fun chargeMoney(serverTransactionId: String) = withContext(Dispatchers.IO) {
        val request = AuthorizationDataRequest(
            cardToken, ProductPrice, ProductCurrency, serverTransactionId
        )
        try {
            val response = merchant3DSApi.authorizationData(authorizationDataRequest = request)
            if (response.status == AuthorizationStatus) {
                navigationManager.navigate(PurchaseScreenDirection)
            } else {
                state = state.copy(error = "Couldn't process payment", isLoading = false)
            }
        } catch (exception: Exception) {
            handleException(exception)
        }
    }

    fun goBack() = viewModelScope.launch {
        navigationManager.navigateBack()
    }

    private fun handleException(exception: Exception) = viewModelScope.launch {
        Log.e("ProcessingScreen", "Error while processing request", exception)
        navigationManager.navigate(ErrorDirection)
    }
}
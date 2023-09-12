package com.globalpayments.android.sdk.merchant3ds.networking.models.request

@kotlinx.serialization.Serializable
data class InitiateAuthenticationParams(
    val cardToken: String = "",
    val amount: String = "",
    val currency: String = "",
    val customerEmail: String = "",
    val mobileData: MobileDataRequest,
    val threeDsecure: ThreeDsecureRequest,
    val preferredDecoupledAuth: Boolean,
    val decoupledFlowTimeout: Int
)

@kotlinx.serialization.Serializable
data class MobileDataRequest(
    val ephemeralPublicKeyX: String,
    val ephemeralPublicKeyY: String,
    val maximumTimeout: Int,
    val sdkTransReference: String,
    val applicationReference: String,
    val sdkInterface: String,
    val encodedData: String,
    val sdkUiTypes: List<String>,
    val referenceNumber: String
)

@kotlinx.serialization.Serializable
data class ThreeDsecureRequest(
    val serverTransactionId: String,
)

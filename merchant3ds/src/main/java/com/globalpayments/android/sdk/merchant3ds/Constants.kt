package com.globalpayments.android.sdk.merchant3ds

const val ProductPrice = "45.99"
const val ProductCurrency = "USD"

enum class EnrolledStatus {
    ENROLLED, NOT_AVAILABLE, NOT_ENROLLED
}

enum class ChallengeStatus {
    CHALLENGE_REQUIRED, SUCCESS, SUCCESS_ATTEMPT_MADE, FAILED, NOT_AUTHENTICATED,SUCCESS_AUTHENTICATED
}

const val SdkInterface = "BOTH"
val SdkUiType = listOf(
    "TEXT",
    "SINGLE_SELECT",
    "MULTI_SELECT",
    "OUT_OF_BAND",
    "HTML_OTHER"
)

enum class AuthenticationStatus {
    SUCCESS_AUTHENTICATED, NOT_AUTHENTICATED, SUCCESS_ATTEMPT_MADE, FAILED
}

const val AuthorizationStatus = "CAPTURED"

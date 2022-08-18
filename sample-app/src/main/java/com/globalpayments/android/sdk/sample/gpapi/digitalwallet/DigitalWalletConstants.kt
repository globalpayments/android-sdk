package com.globalpayments.android.sdk.sample.gpapi.digitalwallet

import com.google.android.gms.wallet.WalletConstants
import java.math.BigDecimal

val PRICE = BigDecimal(10)

val CENTS = BigDecimal(100)

/**
 * Required by the API, but not visible to the user.
 *
 * @value #COUNTRY_CODE Your local country
 */
const val COUNTRY_CODE = "US"

/**
 * Required by the API, but not visible to the user.
 *
 * @value #CURRENCY_CODE Your local currency
 */
const val CURRENCY_CODE = "USD"

/**
 * The name of your payment processor/gateway. Please refer to their documentation for more
 * information.
 *
 * @value #PAYMENT_GATEWAY_TOKENIZATION_NAME
 */
const val PAYMENT_GATEWAY_TOKENIZATION_NAME = "globalpayments"

/**
 * Custom parameters required by the processor/gateway.
 * In many cases, your processor / gateway will only require a gatewayMerchantId.
 * Please refer to your processor's documentation for more information. The number of parameters
 * required and their names vary depending on the processor.
 *
 * @value #PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS
 */
val PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS = mapOf(
    "gateway" to PAYMENT_GATEWAY_TOKENIZATION_NAME,
    "gatewayMerchantId" to "gpapiqa1"
)

/**
 * Changing this to ENVIRONMENT_PRODUCTION will make the API return chargeable card information.
 * Please refer to the documentation to read about the required steps needed to enable
 * ENVIRONMENT_PRODUCTION.
 *
 * @value #PAYMENTS_ENVIRONMENT
 */
const val PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST

/**
 * The allowed networks to be requested from the API. If the user has cards from networks not
 * specified here in their account, these will not be offered for them to choose in the popup.
 *
 * @value #SUPPORTED_NETWORKS
 */
val SUPPORTED_NETWORKS = listOf(
    "AMEX",
    "DISCOVER",
    "JCB",
    "MASTERCARD",
    "VISA"
)

/**
 * The Google Pay API may return cards on file on Google.com (PAN_ONLY) and/or a device token on
 * an Android device authenticated with a 3-D Secure cryptogram (CRYPTOGRAM_3DS).
 *
 * @value #SUPPORTED_METHODS
 */
val SUPPORTED_METHODS = listOf(
    "PAN_ONLY",
    "CRYPTOGRAM_3DS"
)
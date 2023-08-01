<a href="https://github.com/globalpayments" target="_blank">
    <img src="https://developer.globalpay.com/static/media/logo.dab7811d.svg" alt="Global Payments logo" title="Global Payments" align="right" width="225" />
</a>

# GlobalPayments Digital Wallets

## Google Pay Integration

You can find more info on how to add Google Pay in your app please check
the [Official Documentation](https://developers.google.com/pay/api/android/overview)

### Requirements

- Android 4.4+

### Installation

- In your project-level build.gradle file, include Google's Maven repository and Maven central
  repository in both your buildscript and allprojects sections:

```Gradle
buildscript {
  repositories {
    google()
    mavenCentral()
  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
  }
}
```

- Add the Google Play services dependency for the Google Pay API to your module's Gradle build file,
  which is commonly app/build.gradle:

```Gradle
dependencies {
  implementation 'com.google.android.gms:play-services-wallet:<latest_version>'
}
```

- To enable Google Pay in your app, you need to add the following Google Pay API meta-data element
  to the <application> element of your project's AndroidManifest.xml file.

```Gradle
<meta-data
  android:name="com.google.android.gms.wallet.api.enabled"
  android:value="true" />
```

### How to Use

- Define your Google Pay API version

```kotlin
  private val baseRequest = JSONObject().apply {
    put("apiVersion", 2)
    put("apiVersionMinor", 0)
}
```

- Create a `PaymentClient`

```kotlin
const val PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST
fun createPaymentsClient(context: Context): PaymentsClient {
    val walletOptions = Wallet.WalletOptions.Builder()
        .setEnvironment(PAYMENTS_ENVIRONMENT)
        .build()

    return Wallet.getPaymentsClient(context, walletOptions)
}
```

- Determine the user's ability to pay with a payment method supported by your app

```kotlin
 val isReadyToPayJson = baseRequest.apply {
    put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
}
val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
val task = paymentsClient.isReadyToPay(request)
task.addOnCompleteListener { completedTask ->
    try {
        val isGooglePayAvailable = completedTask.getResult(ApiException::class.java)
        //Display button
    } catch (exception: ApiException) {
        Log.e("isReadyToPay failed", exception)
    }
}
```

- When the user taps the pay button, create the request and pass it to the payment client, the
  client returns a task on which you can subscribe for events

```kotlin
val SUPPORTED_NETWORKS = listOf(
    "AMEX",
    "DISCOVER",
    "JCB",
    "MASTERCARD",
    "VISA"
)
val SUPPORTED_METHODS = listOf(
    "PAN_ONLY",
    "CRYPTOGRAM_3DS"
)

val allowedCardNetworks = JSONArray(SUPPORTED_NETWORKS)
val allowedCardAuthMethods = JSONArray(SUPPORTED_METHODS)

fun baseCardPaymentMethod(): JSONObject {
    return JSONObject().apply {

        val parameters = JSONObject().apply {
            put("allowedAuthMethods", allowedCardAuthMethods)
            put("allowedCardNetworks", allowedCardNetworks)
        }

        put("type", "CARD")
        put("parameters", parameters)
    }
}

fun cardPaymentMethod(): JSONObject {
    val cardPaymentMethod = baseCardPaymentMethod()
    cardPaymentMethod.put("tokenizationSpecification", gatewayTokenizationSpecification())
    return cardPaymentMethod
}

fun getTransactionInfo(price: String): JSONObject {
    return JSONObject().apply {
        put("totalPrice", price)
        put("totalPriceStatus", "FINAL")
        put("countryCode", COUNTRY_CODE)
        put("currencyCode", CURRENCY_CODE)
    }
}

val merchantInfo: JSONObject =
    JSONObject().put("merchantId", "10061620723801032773")

val paymentDataRequest = baseRequest.apply {
    put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod()))
    put("transactionInfo", getTransactionInfo(price))
    put("merchantInfo", merchantInfo)
}
val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
val paymentTask = paymentsClient.loadPaymentData(request)
```

- Subscribe to the task to receive updates for the ongoing update

```kotlin
//ActivityResultLauncher for opening the Wallet
val resolvePaymentForResult =
    registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
        when (result.resultCode) {
            AppCompatActivity.RESULT_OK -> result.data?.let { intent ->
                PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
            }

            AppCompatActivity.RESULT_CANCELED -> {
                // The user cancelled the payment attempt
            }
        }
    }
//If the payment pass by without needs to display the Wallet UI handle the response, otherwise display the Wallet UI using StartActivityForResult mechanism the get the result
paymentTask.addOnCompleteListener { completedTask ->
    if (completedTask.isSuccessful) {
        completedTask.result.let(::handlePaymentSuccess)
    } else {
        when (val exception = completedTask.exception) {
            is ResolvableApiException -> {
                //Launch Google Pay Native app
                resolvePaymentForResult.launch(
                    IntentSenderRequest.Builder(exception.resolution).build()
                )
            }
            else -> {
                //Display error
            }
        }
    }
}
```

- Extract the token from the Wallet response

```kotlin
fun handlePaymentSuccess(paymentData: PaymentData) {
    val paymentInformation = paymentData.toJson()

    try {
        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")

        val token = paymentMethodData.getJSONObject("tokenizationData").getString("token")

    } catch (error: JSONException) {
        Log.e("handlePaymentSuccess", "Error: $error")
    }
}
```

- Send the token to the GP-API using the JAVA SDK for payment processing

```kotlin
fun makePaymentWithGooglePay(token: String, amount: BigDecimal): Transaction {
    val card = CreditCardData().apply {
        this.token = token
        this.mobileType = MobilePaymentMethodType.GOOGLEPAY
    }
    return card
        .charge(amount)
        .withCurrency(CURRENCY_CODE)
        .withModifier(TransactionModifier.EncryptedMobile)
        .withMaskedDataResponse(false)
        .execute(DEFAULT_GPAPI_CONFIG)
}
```
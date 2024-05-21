<a href="https://github.com/globalpayments" target="_blank">
    <img src="https://developer.globalpay.com/static/media/logo.dab7811d.svg" alt="Global Payments logo" title="Global Payments" align="right" width="225" />
</a>

# Netcetera 3DS SDK GP-ECOM integration

Global Payments Ecom cam be configured to use the Netcetera SDK for 3DS

## Requirements

- Netcetera 3DS sdk - in the form of an `.aar` android library you can get this from the Global
  Payments Support Team
- API key - can be retrieved from Global Payments support team
- acs certificate - in the form of `.pem` can be retrieved from Global Payments support team

## Installation

- place the `.aar` file in the libs directory of your `app` module and add it as a dependency in
  your app `build.gradle`

```groovy
  implementation files("libs/3ds-sdk.aar")
```

- place certificate file in the `assets` directory of your app

## Integration

The GP Ecom calls can be done from the app.

- instantiate the Netcetera SDK; because this is a heavy CPU operation we recommend to do it on a
  background thread

```kotlin
fun initiateNetceteraSDK(context: Context, apiKey: String) {
    ThreeDS2ServiceInstance.get().initialize(
        context,
        getThreeDS2ConfigParams(context, apiKey),
        Locale.getDefault().language,
        getThreeDS2UICustomizationMap()
    )
}


fun getThreeDS2ConfigParams(context: Context, apiKey: String): ConfigParameters {
    val assetManager = context.assets
    return ConfigurationBuilder()
        .apiKey(apiKey)
        .configureScheme(
            SchemeConfiguration.visaSchemeConfiguration()
                .encryptionPublicKeyFromAssetCertificate(assetManager, "acs.pem")
                .rootPublicKeyFromAssetCertificate(assetManager, "acs.pem")
                .build()
        )
        .build()
}

fun getThreeDS2UICustomizationMap(): Map<UiCustomizationType, UiCustomization> {
    return hashMapOf<UiCustomizationType, UiCustomization>().apply {
        put(UiCustomizationType.DEFAULT, getThreeDS2UICustomization())
        put(UiCustomizationType.DARK, getThreeDS2UICustomization())
    }
}

fun getThreeDS2UICustomization(): UiCustomization {
    return UiCustomization()
}
```

- capture card data

```kotlin
val card = CreditCardData().apply {
    this.number = model.cardNumber
    this.expMonth = model.cardMonth.toInt()
    this.expYear = model.cardYear.toInt()
    this.cardHolderName = model.cardHolderName
    this.isCardPresent = true
}
```

- add the card as a payment method, using the card data, to the customer

```kotlin
val addPaymentMethod = customer.addPaymentMethod(paymentId, card).create()
```

- create a recurring payment method

```kotlin
val recurringPaymentMethod = RecurringPaymentMethod(customer.key, paymentId)

```

- check if the credit card from the recurring payment method is enrolled in the 3ds process

```kotlin
val secureEcom = Secure3dService
    .checkEnrollment(recurringPaymentMethod)
    .withCurrency(Currency)
    .withAmount(BigDecimal(Amount))
    .withAuthenticationSource(AuthenticationSource.MobileSDK)
    .execute()
response.enrolledStatus == "ENROLLED"  
```

- create a Netcetera transaction using the Netcetera SDK and the response from the previous GP
  call
  You will need to determine the card brand from the card number and map it to the `DsRidValues`

```kotlin
val netceteraTransaction = threeDS2Service.createTransaction(
    getDsRidValuesForCardNumber(card.number),
    secureEcom.acsEndVersion
)
val netceteraParams = transaction?.authenticationRequestParameters
```

- initiate the authentication on GP Ecom side using the netceteraParams created at the previous
  step

```kotlin
val threeDSecure = Secure3dService
    .initiateAuthentication(recurringPaymentMethod, secureEcom)
    .withAmount(BigDecimal())
    .withCurrency("Currency")
    .withAuthenticationSource(AuthenticationSource.MobileSDK)
    .withOrderCreateDate(DateTime.now())
    .withOrderId(secureEcom.orderId)
    .withAddress(billingAddress, AddressType.Billing)
    .withAddress(shippingAddress, AddressType.Shipping)
    .withAddressMatchIndicator(false)
    .withMethodUrlCompletion(MethodUrlCompletion.No)
    .withMessageCategory(MessageCategory.PaymentAuthentication)
    .withCustomerEmail(customerEmail)
    .withApplicationId(netceteraParams.sdkAppID)
    .withSdkInterface(SdkInterface.Both)
    .withSdkUiTypes(*SdkUiType.entries.toTypedArray())
    .withReferenceNumber(netceteraParams.sdkReferenceNumber)
    .withSdkTransactionId(netceteraParams.sdkTransactionID)
    .withEncodedData(netceteraParams.deviceData)
    .withMaximumTimeout(5)
    .withEphemeralPublicKey(netceteraParams.sdkEphemeralPublicKey)
    .withChallengeRequestIndicator(ChallengeRequestIndicator.NoPreference)
    .execute()
```

- check if the challenge is required from the previous call

```kotlin
threeDSecure.status == "CHALLENGE_REQUIRED"
```

- create Netcetera challenge parameters using the response from the previous call

```kotlin
 val challengeParams = ChallengeParameters().apply {
    acsRefNumber = threeDSecure.acsReferenceNumber
    acsSignedContent = threeDSecure.payerAuthenticationRequest
    acsTransactionID = threeDSecure.acsTransactionId
    set3DSServerTransactionID(threeDSecure.serverTransactionId)
}
```

- start the challenge using the `netceteraTransaction` and the `challengeParams` created at the
  previous steps  
  You will receive the challenge result on the callback you pass in the `doChallenge` method

```kotlin
 netceteraTransaction.doChallenge(
    requireActivity(),
    challengeParams,
    object : ChallengeStatusReceiver {
        override fun completed(completionEvent: CompletionEvent?) {
        }

        override fun cancelled() {
        }

        override fun timedout() {
        }

        override fun protocolError(p0: ProtocolErrorEvent?) {
        }

        override fun runtimeError(p0: RuntimeErrorEvent?) {
        }
    }
)
```

- check if the challenge was successful

```kotlin
completionEvent?.transactionStatus != "Y" //the challenge was completed successfully
```

- charge the card with the amount authorized

```kotlin
val transaction = recurringPaymentMethod
    .charge(BigDecimal(Amount))
    .withCurrency(Currency)
    .execute()
```
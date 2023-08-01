<a href="https://github.com/globalpayments" target="_blank">
    <img src="https://developer.globalpay.com/static/media/logo.dab7811d.svg" alt="Global Payments logo" title="Global Payments" align="right" width="225" />
</a>

# Netcetera 3DS SDK integration

Global Payments uses the Netcetera SDK for 3DS

## Requirements

- Netcetera 3DS sdk - in the form of an `.aar` android library you can get this from the Global
  Payments Support Team
- license - both sandbox and production can be retrieved from Global Payments support team
- acs certificate - in the form of `.pem` can be retrieved from Global Payments support team

## Installation

- place the `.aar` file in the libs directory of your `app` module and add it as a dependency in
  your app `build.gradle`

```groovy
  implementation files("libs/3ds-sdk.aar")
```

- place the license and certificate files in the `assets` directory of your app

## Integration

The GP calls can be done either from the app or from your server integration.

- instantiate the Netcetera SDK; because this is a heavy CPU operation we recommend to do it on a
  background thread

```kotlin
fun initiateNetceteraSDK(context: Context) {
  ThreeDS2ServiceInstance.get().initialize(
    context,
    getThreeDS2ConfigParams(context),
    Locale.getDefault().language,
    getThreeDS2UICustomization()
  )
}


fun getThreeDS2ConfigParams(context: Context): ConfigParameters {
  val assetManager = context.assets
  return ConfigurationBuilder()
    .license(assetManager.readLicense())
    .configureScheme(
      SchemeConfiguration.visaSchemeConfiguration()
        .encryptionPublicKeyFromAssetCertificate(assetManager, "acs.pem")
        .rootPublicKeyFromAssetCertificate(assetManager, "acs.pem")
        .build()
    )
    .build()
}

fun getThreeDS2UICustomization(): UiCustomization {
  return UiCustomization()
}

fun AssetManager.readLicense() =
  this.open("license").bufferedReader().use(BufferedReader::readText)
```

- check if the credit card is enrolled in the 3ds process

```kotlin
val card = CreditCardData()
val secureEcom = Secure3dService
  .checkEnrollment(card)
  .withCurrency(Currency)
  .withAmount(BigDecimal(Amount))
  .withAuthenticationSource(AuthenticationSource.MobileSDK)
  .execute(Constants.DEFAULT_GPAPI_CONFIG)
response.enrolledStatus == "ENROLLED"  
```

- create a Netcetera transaction using the Netcetera SDK and the response from the previous GP
  call   
  You will need to map the card brand to the `DsRidValues`

```kotlin
val netceteraTransaction = threeDS2Service.createTransaction(
  getDsRidValuesForCard(card),
  secureEcom.messageVersion
)
val netceteraParams = transaction?.authenticationRequestParameters
```

- initiate the authentication on GP side using the netceteraParams created at the previous step

```kotlin
val threeDSecure = Secure3dService
  .initiateAuthentication(card, secureEcom)
  .withAuthenticationSource(AuthenticationSource.MobileSDK)
  .withAmount(BigDecimal())
  .withCurrency("Currency")
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
  set3DSServerTransactionID(threeDSecure.providerServerTransRef)
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

- authenticate transaction on the GP and then set the response on the card used for this transaction

```kotlin
val transaction = Secure3dService
  .getAuthenticationData()
  .withServerTransactionId(threeDSecure.serverTransactionId)
  .execute(Secure3dVersion.TWO, Constants.DEFAULT_GPAPI_CONFIG)
card.threeDSecure = transaction
```

- charge the card with the amount authorized

```kotlin
val transaction = card
  .charge(BigDecimal(Amount))
  .withCurrency(Currency)
  .execute(Constants.DEFAULT_GPAPI_CONFIG)
```
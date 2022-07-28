package com.globalpayments.android.sdk.sample.gpapi.netcetera

import android.content.res.AssetManager
import java.io.BufferedReader


fun AssetManager.readLicense() =
    this.open("license").bufferedReader().use(BufferedReader::readText)

object ThreeDsAppUrlProvider {
    /**
     * Provides the app url required for the 3DS challenge.
     *
     * @param sdkTransactionId [String] the sdk transaction id
     * @return [String] the app url
     */
    fun forTransactionId(sdkTransactionId: String): String {
        return MERCHANT_SCHEME + sdkTransactionId
    }

    private const val MERCHANT_SCHEME = "https://requestor.netcetera.com?transID="

}
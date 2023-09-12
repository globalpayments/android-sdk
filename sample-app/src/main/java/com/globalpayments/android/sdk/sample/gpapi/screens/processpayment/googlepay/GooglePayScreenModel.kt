package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.googlepay

import android.app.PendingIntent
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import org.json.JSONArray

data class GooglePayScreenModel(
    val amount: String = "",
    val isGooglePayAvailable: Boolean = false,
    val allowedPaymentMethods: String = GooglePayUtils.baseCardPaymentMethod().let { JSONArray().put(it) }.toString(),
    val pendingIntent: PendingIntent? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

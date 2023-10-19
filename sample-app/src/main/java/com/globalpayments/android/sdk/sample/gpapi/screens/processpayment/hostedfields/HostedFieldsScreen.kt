package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.hostedfields

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.webkit.WebViewAssetLoader
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPScreenTitle
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.utils.PaymentAmountVisualTransformation
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HostedFieldsScreen(
    context: Context = LocalContext.current,
    hfViewModel: HostedFieldsViewModel = viewModel()
) {

    val screenModel by hfViewModel.screenModel.collectAsState()

    LaunchedEffect(key1 = screenModel.netceteraTransactionParams) {
        val params = screenModel.netceteraTransactionParams
        if (params != null) {
            hfViewModel.doChallenge(context as Activity, params)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GPScreenTitle(
            modifier = Modifier,
            title = stringResource(id = R.string.hosted_fields),
        )

        Text(
            modifier = Modifier.padding(top = 11.dp),
            text = stringResource(id = R.string.hosted_fields_long_description),
            fontSize = 14.sp,
            color = Color(0xFF5A5E6D)
        )

        Divider(
            modifier = Modifier
                .padding(top = 22.dp)
                .fillMaxWidth(),
            color = Color(0xFFD7DCE1)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            val gpSampleResponse = screenModel.gpSampleResponse

            if (gpSampleResponse == null) {
                if (screenModel.accessToken.isNotBlank()) {
                    GPInputField(
                        modifier = Modifier.padding(top = 17.dp),
                        title = R.string.payment_amount,
                        value = screenModel.paymentAmount,
                        onValueChanged = hfViewModel::onPaymentAmountChanged,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal),
                        visualTransformation = PaymentAmountVisualTransformation("$")
                    )
                    val assetLoader = WebViewAssetLoader.Builder().setDomain("example.com")
                        .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context)).build()
                    val state = rememberWebViewState(url = "https://example.com/assets/index.html")
                    WebView(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth(),
                        state = state,
                        onCreated = { webView ->
                            webView.settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                            }
                            webView.addJavascriptInterface(
                                JSBridge(
                                    onSuccess = hfViewModel::onCardTokenReceived,
                                    onError = hfViewModel::onError
                                ), "JSBridge"
                            )
                        },
                        client = remember { LocalContentWebViewClient(assetLoader, screenModel.accessToken) })
                }
            } else {
                GPSampleResponse(model = gpSampleResponse)
                GPActionButton(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 15.dp)
                        .fillMaxWidth(),
                    title = "Reset",
                    onClick = hfViewModel::reset
                )
            }

            Divider(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                color = Color(0xFFD7DCE1)
            )

            GPSnippetResponse(
                modifier = Modifier.padding(top = 20.dp),
                codeSampleLocation = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp
                        )
                    ) {
                        append("Find the code below in this files: sample/gpapi/screens/processpayment/hostedfields/")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("\nHostedFieldsViewModel.kt\nHostedFieldsScreen.kt")
                    }
                },
                codeSampleSnippet = R.drawable.snippet_hosted_fields,
                model = screenModel.gpSnippetResponseModel
            )
        }
    }
}


private class LocalContentWebViewClient(
    private val assetLoader: WebViewAssetLoader,
    private val accessToken: String
) : AccompanistWebViewClient() {
    override fun shouldInterceptRequest(
        view: WebView, request: WebResourceRequest
    ): WebResourceResponse? {
        return assetLoader.shouldInterceptRequest(request.url)
    }

    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        view.evaluateJavascript("initGlobalPayments('$accessToken')", null)
    }
}

class JSBridge(
    private val onLoading: () -> Unit = {},
    private val onError: (Exception) -> Unit = {},
    private val onSuccess: (String, String) -> Unit = { _, _ -> }
) {
    @JavascriptInterface
    fun onLoadingStarted() {
        onLoading.invoke()
    }

    @JavascriptInterface
    fun onTokenizationError(tag: String, error: String) {
        Log.e(tag, error)
        onError.invoke(Exception(error))
    }

    @JavascriptInterface
    fun onTokenizationSuccess(token: String, cardType: String) {
        Log.d("JSNative", "$token:$cardType")
        onSuccess.invoke(token, cardType)
    }
}

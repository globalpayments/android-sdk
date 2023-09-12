package com.globalpayments.android.sdk.merchant3ds.screens.card

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.webkit.WebViewAssetLoader
import com.globalpayments.android.sdk.merchant3ds.R
import com.globalpayments.android.sdk.merchant3ds.ui.theme.Background
import com.globalpayments.android.sdk.merchant3ds.ui.theme.Black
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@Composable
fun HostedFieldsScreen(viewModel: HostedFieldsViewModel = hiltViewModel()) {

    val screenState = viewModel.state

    Column(
        modifier = Modifier
            .background(color = Background)
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) {

        IconButton(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(12.dp),
            onClick = viewModel::goBack,
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, null, tint = Black)
        }

        val context = LocalContext.current
        val assetLoader = WebViewAssetLoader.Builder().setDomain("example.com")
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context)).build()
        val state = rememberWebViewState(url = "https://example.com/assets/index.html")
        WebView(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            state = state,
            onCreated = { webView ->
                webView.settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                }
                webView.addJavascriptInterface(
                    JSBridge(
                        onSuccess = viewModel::onCardTokenReceived,
                        onLoading = viewModel::onLoading,
                        onError = viewModel::onError
                    ), "JSBridge"
                )
            },
            client = remember { LocalContentWebViewClient(assetLoader, viewModel.accessToken) })

        if (screenState.error.isNotBlank()) {
            AlertDialog(
                title = { Text(text = stringResource(id = R.string.error)) },
                text = { Text(text = screenState.error, color = Black) },
                onDismissRequest = viewModel::dismissError,
                confirmButton = {
                    TextButton(onClick = viewModel::dismissError) {
                        Text(text = stringResource(id = R.string.ok), color = Black)
                    }
                }
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
    private val onError: (String) -> Unit = {},
    private val onSuccess: (String, String) -> Unit = { _, _ -> }
) {
    @JavascriptInterface
    fun onLoadingStarted() {
        onLoading.invoke()
    }

    @JavascriptInterface
    fun onTokenizationError(tag: String, error: String) {
        Log.e(tag, error)
        onError.invoke(error)
    }

    @JavascriptInterface
    fun onTokenizationSuccess(token: String, cardType: String) {
        Log.d("JSNative", "$token:$cardType")
        onSuccess.invoke(token, cardType)
    }
}

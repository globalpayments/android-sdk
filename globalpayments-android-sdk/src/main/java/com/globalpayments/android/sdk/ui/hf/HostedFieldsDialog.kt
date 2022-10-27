package com.globalpayments.android.sdk.ui.hf

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat

class HostedFieldsDialog : AppCompatDialogFragment() {

    private lateinit var webView: WebView
    private lateinit var progressDialog: ProgressDialog

    var onTokenReceived: ((String, String) -> Unit)? = null

    override fun getTheme(): Int {
        return android.R.style.ThemeOverlay_Material_Light
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        webView = WebView(inflater.context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
        return webView
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        progressDialog = ProgressDialog(requireContext()).apply {
            setTitle("Loading")
        }

        val accessToken = arguments?.getString(AccessToken)
            ?: throw IllegalArgumentException("We need an access token")

        val assetLoader = WebViewAssetLoader.Builder()
            .setDomain("example.com")
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(requireContext()))
            .build()

        webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = LocalContentWebViewClient(assetLoader, accessToken)
            addJavascriptInterface(JSBridge(requireContext()), JSBridgeName)
            loadUrl(HtmlLocalAddress)
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.apply {
            val params = attributes.apply {
                width = MATCH_PARENT
                height = MATCH_PARENT
            }
            attributes = params
        }
    }

    companion object {
        private const val JSBridgeName = "JSBridge"
        private const val HtmlLocalAddress = "https://example.com/assets/index.html"

        private const val AccessToken = "access_token"
        fun newInstance(accessToken: String): HostedFieldsDialog {
            val args = bundleOf(AccessToken to accessToken)
            val fragment = HostedFieldsDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private class LocalContentWebViewClient(
        private val assetLoader: WebViewAssetLoader,
        private val accessToken: String,
    ) :
        WebViewClientCompat() {
        override fun shouldInterceptRequest(
            view: WebView,
            request: WebResourceRequest
        ): WebResourceResponse? {
            return assetLoader.shouldInterceptRequest(request.url)
        }

        override fun onPageFinished(view: WebView, url: String?) {
            super.onPageFinished(view, url)
            view.evaluateJavascript("initGlobalPayments('$accessToken')", null)
        }
    }

    inner class JSBridge(private val context: Context) {

        @JavascriptInterface
        fun onLoadingStarted() {
            progressDialog.show()
        }

        @JavascriptInterface
        fun onTokenizationError(tag: String, error: String) {
            Log.e(tag, error)
            progressDialog.hide()
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }

        @JavascriptInterface
        fun onTokenizationSuccess(token: String, cardType: String) {
            Log.d("JSNative", "$token:$cardType")
            progressDialog.hide()
            onTokenReceived?.invoke(token, cardType)
            dismissAllowingStateLoss()
        }
    }
}
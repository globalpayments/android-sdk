package com.globalpayments.android.sdk.ui.clicktopay

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.*
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import com.globalpayments.android.sdk.R

class ClickToPayFragment : DialogFragment(R.layout.dialog_click_to_play) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val clickToPayModel = arguments?.getParcelable<ClickToPayModel>(ClickToPayKey) ?: return
        createWebView(clickToPayModel)
    }

    override fun onStart() {
        super.onStart()
        requireDialog().window?.apply {
            setLayout(MATCH_PARENT, MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView(clickToPayModel: ClickToPayModel) {
        val assetLoader = WebViewAssetLoader.Builder()
            .setDomain(clickToPayModel.domain)
            .addPathHandler(clickToPayModel.pathHandler, WebViewAssetLoader.AssetsPathHandler(requireContext()))
            .build()

        val webView = WebView(requireContext())
            .apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                settings.apply {
                    javaScriptEnabled = true
                    javaScriptCanOpenWindowsAutomatically = true
                    domStorageEnabled = true
                    setSupportMultipleWindows(true)
                }
                webViewClient = createWebClient(assetLoader, clickToPayModel.javaScriptToEvaluate)
                webChromeClient = createChromeClient(this@ClickToPayFragment::createNewWebView)
                addJavascriptInterface(
                    JSBridge(
                        onTokenReceived = this@ClickToPayFragment::onTokenSuccess,
                        onTokenError = this@ClickToPayFragment::onTokenError
                    ), JSBridge
                )
            }
        (requireView() as ViewGroup).addView(webView)
        webView.loadUrl(clickToPayModel.htmlLocalAddress)
    }

    private fun onTokenError(error: String) {
        setFragmentResult(ClickToPayResultKey, bundleOf(ClickToPayErrorKey to error))
        dismiss()
    }

    private fun onTokenSuccess(token: String) {
        setFragmentResult(ClickToPayResultKey, bundleOf(ClickToPayTokenKey to token))
        dismiss()
    }

    private fun createWebClient(
        assetLoader: WebViewAssetLoader,
        javaScriptToEvaluate: String,
    ): WebViewClientCompat = object : WebViewClientCompat() {
        override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
            return assetLoader.shouldInterceptRequest(request.url)
        }

        override fun onPageFinished(view: WebView, url: String) {
            view.evaluateJavascript(javaScriptToEvaluate, null)
        }
    }

    private fun createChromeClient(
        createNewWebView: (Message) -> Unit
    ): WebChromeClient = object : WebChromeClient() {
        override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message): Boolean {
            createNewWebView(resultMsg)
            return true
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createNewWebView(message: Message) {
        val webView = WebView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            webViewClient = object : WebViewClientCompat() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    view.loadUrl(request.url.toString())
                    return true
                }
            }
            settings.javaScriptEnabled = true
        }
        (requireView() as ViewGroup).addView(webView)
        val transport = message.obj as WebView.WebViewTransport
        transport.webView = webView
        message.sendToTarget()
    }

    companion object {
        private const val ClickToPayKey = "argument.key"
        private const val JSBridge = "JSBridge"

        const val ClickToPayResultKey = "click.to.pay.result.key"
        const val ClickToPayTokenKey = "click.to.pay.token.key"
        const val ClickToPayErrorKey = "click.to.pay.error.key"

        fun newInstance(model: ClickToPayModel): ClickToPayFragment {
            val fragment = ClickToPayFragment()
            fragment.arguments = bundleOf(ClickToPayKey to model)
            return fragment
        }
    }
}

private class JSBridge(
    private val onTokenReceived: (String) -> Unit,
    private val onTokenError: (String) -> Unit
) {

    @JavascriptInterface
    fun onTokenizationError(error: String) {
        onTokenError(error)
    }

    @JavascriptInterface
    fun onTokenizationSuccess(token: String, cardType: String) {
        onTokenReceived(token)
    }

}

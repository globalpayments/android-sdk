<a href="https://github.com/globalpayments" target="_blank">
    <img src="https://developer.globalpay.com/static/media/logo.dab7811d.svg" alt="Global Payments logo" title="Global Payments" align="right" width="225" />
</a>

# Global Payments Hosted Fields JS library integration

## Requirements

- [HTML](globalpayments-android-sdk/src/main/assets/index.html) page for displaying the Hosted
  Fields page

## Installation

- Add the HTML file in your `src/main/assets` directory

## How to use

- Add a WebView to your screen using your preferred UI framework
- Define the JSBridge for communicating the html page that we will load. For mor information
  check: https://developer.android.com/develop/ui/views/layout/webapps/webview#BindingJavaScript

```kotlin
class JSBridge(private val context: Context) {

    @JavascriptInterface
    fun onLoadingStarted() {
        //show progress
    }

    @JavascriptInterface
    fun onTokenizationError(tag: String, error: String) {
        //show error    
    }

    @JavascriptInterface
    fun onTokenizationSuccess(token: String, cardType: String) {
        //handle received token
    }
}
```

- Tell the WebView from where to load our html. For mor information
  check: https://developer.android.com/develop/ui/views/layout/webapps/load-local-content

```kotlin
 class LocalContentWebViewClient(
    private val assetLoader: WebViewAssetLoader,
    private val accessToken: String,
) :
    WebViewClientCompat() {
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse {
        return assetLoader.shouldInterceptRequest(request.url)
    }

    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        view.evaluateJavascript("initGlobalPayments('$accessToken')", null)
    }
}
```

- Configure the WebView to load our html file

```kotlin
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
```


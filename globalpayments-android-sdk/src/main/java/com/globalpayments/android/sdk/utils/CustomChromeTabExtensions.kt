package com.globalpayments.android.sdk.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import com.globalpayments.android.sdk.utils.BrowserPackages.SERVICE_ACTION


object BrowserPackages {
    const val SERVICE_ACTION = "android.support.customtabs.action.CustomTabsService"
    const val CHROME_PACKAGE = "com.android.chrome"
    const val SAMSUNG_BROWSER = "com.sec.android.app.sbrowser"
}

fun PackageManager.isCustomTabsSupported(packageName: String): Boolean {
    val serviceIntent = Intent(SERVICE_ACTION)
    serviceIntent.setPackage(packageName)
    val resolveInfos = queryIntentServices(serviceIntent, 0)
    return resolveInfos.isNotEmpty()
}

fun Context.openChromeCustomTabs(uriString: String) {
    val uri = Uri.parse(uriString)

    val customTabsIntent = CustomTabsIntent
        .Builder()
        .setDefaultColorSchemeParams(
            CustomTabColorSchemeParams
                .Builder()
                .setToolbarColor(Color.parseColor("#0070ba"))
                .build()
        )
        .build()
        .apply {
            intent.apply {
                setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    if (packageManager.isCustomTabsSupported(BrowserPackages.CHROME_PACKAGE)) {
        // if there is chrome on the system open the url in chrome so that we can link back to the app.
        customTabsIntent.intent.setPackage(BrowserPackages.CHROME_PACKAGE)
    } else if (packageManager.isCustomTabsSupported(BrowserPackages.SAMSUNG_BROWSER)) {
        /* if there is no chrome but it has samsung use that because samsung does not come with stock AOSP
         * This does not work if Chrome is installed but disabled */
        customTabsIntent.intent.setPackage(BrowserPackages.SAMSUNG_BROWSER)
    }

    // if none of those pass this defaults to the stock ASOP
    customTabsIntent.launchUrl(this, uri)

}
package com.globalpatments.android.sdk.merchant3ds.screens.product

import android.os.Bundle
import androidx.navigation.NavOptionsBuilder
import com.globalpatments.android.sdk.merchant3ds.navigation.NavigationDirection

object ProductDirection : NavigationDirection {
    override val arguments: Bundle? = null
    override val navBuilder: NavOptionsBuilder.() -> Unit = { launchSingleTop = true }
}
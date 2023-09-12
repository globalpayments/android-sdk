package com.globalpayments.android.sdk.merchant3ds.navigation

import android.os.Bundle
import androidx.navigation.NavOptionsBuilder

interface NavigationDirection {
    val arguments: Bundle?
    val navBuilder: NavOptionsBuilder.() -> Unit
}

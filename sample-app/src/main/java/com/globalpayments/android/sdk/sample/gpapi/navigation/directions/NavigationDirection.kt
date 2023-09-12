package com.globalpayments.android.sdk.sample.gpapi.navigation.directions

import android.os.Bundle
import androidx.navigation.NavOptionsBuilder

interface NavigationDirection {
    val arguments: Bundle?
    val navBuilder: NavOptionsBuilder.() -> Unit
}

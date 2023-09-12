package com.globalpayments.android.sdk.sample.gpapi.navigation.directions

import android.os.Bundle
import androidx.navigation.NavOptionsBuilder

object DirectionBack : NavigationDirection {
    override val arguments: Bundle? = null
    override val navBuilder: NavOptionsBuilder.() -> Unit = {}
}

package com.globalpayments.android.sdk.merchant3ds.screens.error

import android.os.Bundle
import androidx.navigation.NavOptionsBuilder
import com.globalpayments.android.sdk.merchant3ds.navigation.NavigationDirection
import com.globalpayments.android.sdk.merchant3ds.screens.product.ProductDirection

object ErrorDirection : NavigationDirection {
    override val arguments: Bundle? = null
    override val navBuilder: NavOptionsBuilder.() -> Unit = {
        launchSingleTop = true
        popUpTo(ProductDirection::class.toString())
    }
}

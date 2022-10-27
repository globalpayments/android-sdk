package com.globalpatments.android.sdk.merchant3ds.screens.purchase

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavOptionsBuilder
import com.globalpatments.android.sdk.merchant3ds.navigation.NavigationDirection
import com.globalpatments.android.sdk.merchant3ds.screens.product.ProductDirection

object PurchaseScreenDirection : NavigationDirection {
    override val arguments: Bundle = bundleOf()
    override val navBuilder: NavOptionsBuilder.() -> Unit = {
        launchSingleTop = true
        popUpTo(ProductDirection::class.toString())
    }

}
package com.globalpayments.android.sdk.merchant3ds.screens.processing

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavOptionsBuilder
import com.globalpayments.android.sdk.merchant3ds.navigation.NavigationDirection
import com.globalpayments.android.sdk.merchant3ds.screens.product.ProductDirection

class ProcessingDirection(
    cardToken: String,
    cardType: String,
) : NavigationDirection {
    override val arguments: Bundle = bundleOf(CardTokenKey to cardToken, CardTypeKey to cardType)
    override val navBuilder: NavOptionsBuilder.() -> Unit = {
        launchSingleTop = true
        popUpTo(ProductDirection::class.toString())
    }

    companion object {
        const val CardTokenKey = "card.token.key"
        const val CardTypeKey = "card.type.key"
    }
}

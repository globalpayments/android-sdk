package com.globalpatments.android.sdk.merchant3ds.screens.card

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavOptionsBuilder
import com.globalpatments.android.sdk.merchant3ds.navigation.NavigationDirection

class HostedFieldDirection(
    accessToken: String
) : NavigationDirection {
    override val arguments: Bundle = bundleOf(AccessToken to accessToken)
    override val navBuilder: NavOptionsBuilder.() -> Unit = {
        launchSingleTop = true
    }

    companion object {
        const val AccessToken = "access.token"
    }
}
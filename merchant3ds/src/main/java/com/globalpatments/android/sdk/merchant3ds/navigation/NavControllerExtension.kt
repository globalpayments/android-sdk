package com.globalpatments.android.sdk.merchant3ds.navigation

import android.os.Bundle
import androidx.core.net.toUri
import androidx.navigation.*

fun NavController.navigate(
    route: String,
    args: Bundle?,
    navOptions: NavOptions? = null,
    navigatorsExtras: Navigator.Extras? = null
) {
    val routeLink = NavDeepLinkRequest
        .Builder
        .fromUri(NavDestination.createRoute(route).toUri())
        .build()

    val deepLinkMatch = graph.matchDeepLink(routeLink)
    if (deepLinkMatch != null) {
        val destination = deepLinkMatch.destination
        val id = destination.id
        navigate(id, args, navOptions, navigatorsExtras)
    } else {
        navigate(route, navOptions, navigatorsExtras)
    }
}
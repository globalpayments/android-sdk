package com.globalpayments.android.sdk.merchant3ds

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.globalpayments.android.sdk.merchant3ds.navigation.DirectionBack
import com.globalpayments.android.sdk.merchant3ds.navigation.NavigationManager
import com.globalpayments.android.sdk.merchant3ds.navigation.navigate

@Composable
fun AppScreen(navigationManager: NavigationManager) {
    val navController = rememberNavController()

    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    LaunchedEffect(true) {
        navigationManager.commands.collect { command ->
            when (command) {
                DirectionBack -> backPressedDispatcher?.onBackPressed()
                else -> navController.navigate(
                    route = command::class.toString(),
                    args = command.arguments,
                    navOptions = navOptions(command.navBuilder)
                )
            }
        }
    }
    MerchantNavHost(navController = navController)
}

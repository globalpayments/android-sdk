package com.globalpatments.android.sdk.merchant3ds

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.navOptions
import com.globalpatments.android.sdk.merchant3ds.navigation.DirectionBack
import com.globalpatments.android.sdk.merchant3ds.navigation.NavigationManager
import com.globalpatments.android.sdk.merchant3ds.navigation.navigate
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppScreen(navigationManager: NavigationManager) {
    val navController = rememberAnimatedNavController()

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
    NavHost(navController = navController)
}
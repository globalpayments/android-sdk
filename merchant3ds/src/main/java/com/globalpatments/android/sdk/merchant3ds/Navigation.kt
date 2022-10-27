package com.globalpatments.android.sdk.merchant3ds

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.globalpatments.android.sdk.merchant3ds.screens.card.HostedFieldDirection
import com.globalpatments.android.sdk.merchant3ds.screens.card.HostedFieldsScreen
import com.globalpatments.android.sdk.merchant3ds.screens.error.ErrorDirection
import com.globalpatments.android.sdk.merchant3ds.screens.error.ErrorScreen
import com.globalpatments.android.sdk.merchant3ds.screens.processing.ProcessingDirection
import com.globalpatments.android.sdk.merchant3ds.screens.processing.ProcessingScreen
import com.globalpatments.android.sdk.merchant3ds.screens.product.ProductDirection
import com.globalpatments.android.sdk.merchant3ds.screens.product.ProductScreen
import com.globalpatments.android.sdk.merchant3ds.screens.purchase.PurchaseScreen
import com.globalpatments.android.sdk.merchant3ds.screens.purchase.PurchaseScreenDirection
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavHost(navController: NavHostController) {
    AnimatedNavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = ProductDirection::class.toString(),
        enterTransition = { slideInHorizontally { fullWidth -> fullWidth } },
        exitTransition = { slideOutHorizontally { fullWidth -> -fullWidth / 2 } },
        popEnterTransition = { slideInHorizontally { fullWidth -> -fullWidth } },
        popExitTransition = { slideOutHorizontally { fullWidth -> fullWidth } }
    ) {
        composable(ProductDirection::class.toString()) { ProductScreen() }
        composable(HostedFieldDirection::class.toString()) { HostedFieldsScreen() }
        composable(PurchaseScreenDirection::class.toString()) { PurchaseScreen() }
        composable(ProcessingDirection::class.toString()) { ProcessingScreen() }
        composable(ErrorDirection::class.toString()) { ErrorScreen() }
    }
}
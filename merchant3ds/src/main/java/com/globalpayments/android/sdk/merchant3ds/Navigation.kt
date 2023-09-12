package com.globalpayments.android.sdk.merchant3ds

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.globalpayments.android.sdk.merchant3ds.screens.card.HostedFieldDirection
import com.globalpayments.android.sdk.merchant3ds.screens.card.HostedFieldsScreen
import com.globalpayments.android.sdk.merchant3ds.screens.error.ErrorDirection
import com.globalpayments.android.sdk.merchant3ds.screens.error.ErrorScreen
import com.globalpayments.android.sdk.merchant3ds.screens.processing.ProcessingDirection
import com.globalpayments.android.sdk.merchant3ds.screens.processing.ProcessingScreen
import com.globalpayments.android.sdk.merchant3ds.screens.product.ProductDirection
import com.globalpayments.android.sdk.merchant3ds.screens.product.ProductScreen
import com.globalpayments.android.sdk.merchant3ds.screens.purchase.PurchaseScreen
import com.globalpayments.android.sdk.merchant3ds.screens.purchase.PurchaseScreenDirection

@Composable
fun MerchantNavHost(navController: NavHostController) {
    NavHost(
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

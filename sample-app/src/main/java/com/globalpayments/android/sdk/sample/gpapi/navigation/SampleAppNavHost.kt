package com.globalpayments.android.sdk.sample.gpapi.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.AchDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.ActionsReportingDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.BNPLDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.BatchesDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.CTPDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.ConfigDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.CreateAccessTokenDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.DepositsReportingDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.DisputesDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.DisputesReportingDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.EbtDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.ExpandYourIntegrationDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.GooglePayDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.HomeDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.HostedFieldsDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.PayLinkDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.PayPalDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.PaymentMethodsReportingDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.ProcessAPaymentDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.ReportingMenuDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.StoredPaymentsMethodsDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.TransactionsReportingDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.UnifiedPaymentsAPIDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.VerificationsDirection
import com.globalpayments.android.sdk.sample.gpapi.screens.accesstoken.CreateAccessTokenScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.config.ConfigScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.ExpandIntegrationMenuScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.batches.BatchesScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.disputes.DisputesScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.storedpayments.StoredPaymentsScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.verifications.VerificationsScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.home.HomeScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.ProcessAPaymentMenuScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.ach.AchScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.bnpl.BnplScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.ctp.CtpScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.ebt.EbtScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.googlepay.GooglePayScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.hostedfields.HostedFieldsScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.paybylink.PayByLinkScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.paypal.PayPalScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.unifiedpaymentsapi.UnifiedPaymentAPIScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.reporting.ReportingMenuScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.reporting.actions.ActionsScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.reporting.deposits.DepositsScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.reporting.disputes.DisputesReportingScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.reporting.paymentmethods.PaymentMethodsScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.reporting.transactions.TransactionsScreen

@Composable
fun SampleAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = HomeDirection::class.toString(),
        enterTransition = { slideInHorizontally { fullWidth -> fullWidth } },
        exitTransition = { slideOutHorizontally { fullWidth -> -fullWidth / 2 } },
        popEnterTransition = { slideInHorizontally { fullWidth -> -fullWidth } },
        popExitTransition = { slideOutHorizontally { fullWidth -> fullWidth } }
    ) {
        composable(HomeDirection::class.toString()) { HomeScreen() }
        composable(ConfigDirection::class.toString()) { ConfigScreen() }
        composable(ProcessAPaymentDirection::class.toString()) { ProcessAPaymentMenuScreen() }
        composable(ExpandYourIntegrationDirection::class.toString()) { ExpandIntegrationMenuScreen() }
        composable(ReportingMenuDirection::class.toString()) { ReportingMenuScreen() }
        composable(CreateAccessTokenDirection::class.toString()) { CreateAccessTokenScreen() }
        composable(HostedFieldsDirection::class.toString()) { HostedFieldsScreen() }
        composable(UnifiedPaymentsAPIDirection::class.toString()) { UnifiedPaymentAPIScreen() }
        composable(GooglePayDirection::class.toString()) { GooglePayScreen() }
        composable(TransactionsReportingDirection::class.toString()) { TransactionsScreen() }
        composable(DepositsReportingDirection::class.toString()) { DepositsScreen() }
        composable(StoredPaymentsMethodsDirection::class.toString()) { StoredPaymentsScreen() }
        composable(VerificationsDirection::class.toString()) { VerificationsScreen() }
        composable(BatchesDirection::class.toString()) { BatchesScreen() }
        composable(DisputesDirection::class.toString()) { DisputesScreen() }
        composable(PaymentMethodsReportingDirection::class.toString()) { PaymentMethodsScreen() }
        composable(ActionsReportingDirection::class.toString()) { ActionsScreen() }
        composable(DisputesReportingDirection::class.toString()) { DisputesReportingScreen() }
        composable(PayPalDirection::class.toString()) { PayPalScreen() }
        composable(PayLinkDirection::class.toString()) { PayByLinkScreen() }
        composable(AchDirection::class.toString()) { AchScreen() }
        composable(EbtDirection::class.toString()) { EbtScreen() }
        composable(BNPLDirection::class.toString()) { BnplScreen() }
        composable(CTPDirection::class.toString()) { CtpScreen() }
    }

}

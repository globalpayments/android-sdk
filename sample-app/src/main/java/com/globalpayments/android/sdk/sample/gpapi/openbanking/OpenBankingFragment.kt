package com.globalpayments.android.sdk.sample.gpapi.openbanking

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.globalpayments.android.sdk.sample.common.compose.BaseComposeFragment
import com.globalpayments.android.sdk.sample.common.compose.LoadingDialog
import com.globalpayments.android.sdk.sample.gpapi.openbanking.models.PaymentDetails
import com.globalpayments.android.sdk.sample.gpapi.openbanking.screens.*
import com.globalpayments.android.sdk.sample.utils.rememberLifecycleEvent

class OpenBankingFragment : BaseComposeFragment() {

    private val viewModel: OpenBankingViewModel by viewModels()

    @Composable
    override fun ComposeFragmentScreen(showFragment: (containerViewId: Int, fragment: Fragment) -> Unit, closeFragment: () -> Unit) {

        val backPressDispatcher = LocalOnBackPressedDispatcherOwner.current

        val state by viewModel.screenState.collectAsState()
        val loading by viewModel.loading.collectAsState()

        val lifecycleEvent = rememberLifecycleEvent()

        LaunchedEffect(lifecycleEvent) {
            if (lifecycleEvent == Lifecycle.Event.ON_RESUME) {
                viewModel.checkTransaction()
            }
        }

        BackHandler {
            when (state) {
                is OpenBankingScreenState.Error,
                is OpenBankingScreenState.Payment,
                is OpenBankingScreenState.Processing,
                OpenBankingScreenState.Product -> backPressDispatcher?.onBackPressedDispatcher?.onBackPressed()
                else -> Unit
            }
        }

        when (val screenState = state) {
            is OpenBankingScreenState.Error -> ErrorScreen(viewModel::goToProductScreen, screenState.message)
            is OpenBankingScreenState.Payment -> PaymentScreen(
                onBackPressed = viewModel::goToProductScreen,
                paymentDetails = when (screenState.paymentType) {
                    PaymentType.Sepa -> PaymentDetails.SepaDetails()
                    PaymentType.FasterPayments -> PaymentDetails.FasterPaymentDetails()
                },
                onPayClick = viewModel::payForProduct
            )
            OpenBankingScreenState.Product -> ProductScreen(this::close, viewModel::onUSDSelected, viewModel::onEuroSelected)
            is OpenBankingScreenState.Processing -> ProcessingScreen(viewModel::goToProductScreen, screenState.transactionSummary)
            is OpenBankingScreenState.Loading -> RedirectScreen(viewModel = viewModel, onBackPressed = viewModel::goToProductScreen)
        }

        if (loading) {
            LoadingDialog(text = "Processing")
        }
    }
}

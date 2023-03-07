package com.globalpayments.android.sdk.sample.gpapi.bnpl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.NO_ID
import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalUriHandler
import androidx.fragment.app.viewModels
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.common.compose.AddressView
import com.globalpayments.android.sdk.sample.common.compose.LoadingDialog
import com.globalpayments.android.sdk.sample.common.compose.TransactionErrorDialog
import com.globalpayments.android.sdk.sample.common.theme.GlobalPaymentsTheme

class BnplFragment : BaseFragment() {

    private val viewModel: BnplViewModel by viewModels()

    override fun getLayoutId(): Int {
        return NO_ID
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                GlobalPaymentsTheme {
                    BnplScreen(viewModel = viewModel, onBackClick = {
                        if (viewModel.screenModel.value.currentScreen.parent == null) {
                            close()
                            return@BnplScreen
                        }
                        viewModel.goToPreviousScreen()
                    })
                }
            }
            lifecycle.addObserver(viewModel)
        }
    }
}

@Composable
fun BnplScreen(
    viewModel: BnplViewModel,
    onBackClick: () -> Unit,
) {
    val screenModel by viewModel.screenModel.collectAsState()
    Scaffold(modifier = Modifier
        .fillMaxSize()
        .imePadding(), topBar = {
        TopAppBar(modifier = Modifier.fillMaxWidth(), title = {
            Text(text = "Buy now pay later")
        }, navigationIcon = {
            IconButton(onClick = onBackClick) {
                Image(imageVector = Icons.Default.ArrowBack, contentDescription = null, colorFilter = ColorFilter.tint(Color.White))
            }
        })
    }) { padding ->
        Crossfade(
            modifier = Modifier.padding(padding),
            targetState = screenModel.currentScreen,
        ) { screenState ->
            when (screenState) {
                ScreenState.General -> BnplGeneralScreen(viewModel)
                ScreenState.Products -> ProductsScreen(selectedProducts = screenModel.bnplGeneralScreenModel.products, onProductsSelected = {
                    viewModel.onProductsSelected(it)
                    onBackClick()
                })
                ScreenState.ShippingAddress -> AddressView(onAddressCreated = {
                    viewModel.onShippingAddressChanged(it)
                    onBackClick()
                })
                ScreenState.BillingAddress -> AddressView(onAddressCreated = {
                    viewModel.onBillingAddressChanged(it)
                    onBackClick()
                })
                ScreenState.CustomerData -> CustomerDataScreen(onCustomerCreate = {
                    viewModel.onCustomerDataChanged(it)
                    onBackClick()
                })
                ScreenState.CaptureRefundReverse -> BnplCaptureReverseScreen(bnplViewModel = viewModel)
                ScreenState.Refund -> BnplRefundScreen(bnplViewModel = viewModel)
            }
        }
    }
    if (screenModel.urlToOpen.isNotBlank()) {
        val localUriHandler = LocalUriHandler.current
        localUriHandler.openUri(screenModel.urlToOpen)
        viewModel.urlOpened()
    }
    if (screenModel.isLoading) {
        LoadingDialog(text = "Transaction in progress")
    }
    if (screenModel.error.isNotBlank()) {
        TransactionErrorDialog(errorMessage = screenModel.error, onDismissRequest = viewModel::resetState)
    }
}

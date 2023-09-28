package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.bnpl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPAddressDialog
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPScreenTitle
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.utils.rememberLifecycleEvent

@Composable
fun BnplScreen(bnplViewModel: BnplViewModel = viewModel()) {

    val screenModel by bnplViewModel.screenModel.collectAsState()

    val localUriHandler = LocalUriHandler.current
    val lifecycleEvent = rememberLifecycleEvent()

    LaunchedEffect(key1 = screenModel.urlToOpen, block = {
        val urlToOpen = screenModel.urlToOpen
        if (urlToOpen.isNotBlank()) {
            localUriHandler.openUri(urlToOpen)
        }
    })

    LaunchedEffect(key1 = lifecycleEvent) {
        if (lifecycleEvent == Lifecycle.Event.ON_RESUME) {
            bnplViewModel.goToCapture()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 10.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        GPScreenTitle(
            modifier = Modifier,
            title = stringResource(id = R.string.bnpl),
        )

        Divider(
            modifier = Modifier
                .padding(top = 22.dp)
                .fillMaxWidth(),
            color = Color(0xFFD7DCE1)
        )

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            when (screenModel.screenState) {
                ScreenState.CaptureRefund -> BnplCaptureReverse(
                    gpSampleResponseModel = screenModel.gpSampleResponseModel!!,
                    onCaptureClick = bnplViewModel::captureTransaction,
                    onReverseClick = bnplViewModel::reverseTransaction,
                    onResetClick = bnplViewModel::resetState
                )

                ScreenState.Request -> BNPLRequest(screenModel.bnplRequestModel, bnplViewModel)
                ScreenState.Refund -> BnplRefund(
                    screenModel.gpSampleResponseModel!!,
                    screenModel.bnplRefundScreenModel.amount,
                    bnplViewModel::refundAmountChanged,
                    bnplViewModel::onRefundClick,
                    bnplViewModel::resetState
                )

                ScreenState.Reset -> {

                    GPSampleResponse(model = screenModel.gpSampleResponseModel!!)

                    GPActionButton(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 15.dp)
                            .fillMaxWidth(),
                        title = "Reset",
                        onClick = bnplViewModel::resetState
                    )
                }
            }

            Divider(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                color = Color(0xFFD7DCE1)
            )

            GPSnippetResponse(
                modifier = Modifier.padding(top = 20.dp),
                codeSampleLocation = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp
                        )
                    ) {
                        append("Find the code below in this files: sample/gpapi/screens/processpayment/bnpl/")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("\nBNPLViewModel.kt")
                    }
                },
                codeSampleSnippet = R.drawable.snippet_bnpl,
                model = screenModel.gpSnippetResponseModel
            )

        }
    }

    if (screenModel.showCustomerDataDialog) {
        CustomerDataDialog(
            onDismissRequest = { bnplViewModel.customerDataDialogVisibility(false) },
            onCustomerCreate = bnplViewModel::onCustomerDataChanged
        )
    }
    if (screenModel.showProductsDialog) {
        ProductsDialog(
            onDismissRequest = { bnplViewModel.productsDialogVisibility(false) },
            onProductsSelected = bnplViewModel::onProductsSelected,
            selectedProducts = screenModel.bnplRequestModel.products
        )
    }

    if (screenModel.showBillingAddressDialog) {
        GPAddressDialog(
            address = screenModel.bnplRequestModel.billingAddress,
            onDismissClicked = { bnplViewModel.billingAddressDialogVisibility(false) },
            onAddressCreated = bnplViewModel::onBillingAddressChanged
        )
    }

    if (screenModel.showShippingAddressDialog) {
        GPAddressDialog(
            address = screenModel.bnplRequestModel.shippingAddress,
            onDismissClicked = { bnplViewModel.shippingAddressDialogVisibility(false) },
            onAddressCreated = bnplViewModel::onShippingAddressChanged
        )
    }
}

@Preview
@Composable
fun BnplGeneralScreenPreview() {
    Box(modifier = Modifier.background(Color.White)) {
        BnplScreen(BnplViewModel())
    }
}




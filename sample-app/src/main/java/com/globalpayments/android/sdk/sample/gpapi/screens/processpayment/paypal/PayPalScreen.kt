package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.paypal

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPScreenTitle
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.utils.rememberLifecycleEvent

@Composable
fun PayPalScreen(
    context: Context = LocalContext.current,
    vm: PayPalScreenViewModel = viewModel(initializer = { PayPalScreenViewModel(context) })
) {

    val screenModel by vm.screenModel.collectAsState()
    val uriHandler = LocalUriHandler.current
    val lifecycleEvent = rememberLifecycleEvent()

    val uriToOpen = screenModel.pendingTransaction?.alternativePaymentResponse?.redirectUrl
    LaunchedEffect(key1 = uriToOpen, block = {
        if (!uriToOpen.isNullOrBlank()) {
            uriHandler.openUri(uriToOpen)
        }
    })

    LaunchedEffect(key1 = lifecycleEvent) {
        if (lifecycleEvent == Lifecycle.Event.ON_RESUME) {
            vm.checkTransaction()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GPScreenTitle(
            modifier = Modifier,
            title = stringResource(id = R.string.paypal),
        )

        Divider(
            modifier = Modifier
                .padding(top = 22.dp)
                .fillMaxWidth(),
            color = Color(0xFFD7DCE1)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            val gpSampleResponseModel = screenModel.gpSampleResponseModel
            if (screenModel.pendingTransaction != null && gpSampleResponseModel != null) {
                PayPalRefundReverseView(
                    responseModel = gpSampleResponseModel,
                    status = screenModel.transactionStatus,
                    onRefundClick = vm::refundTransaction,
                    onReverseClick = vm::reverseTransaction,
                    onCancelClick = vm::resetScreen,
                    onCaptureClick = vm::captureTransaction
                )
            } else if (gpSampleResponseModel != null) {
                GPSampleResponse(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
                    model = gpSampleResponseModel
                )
                GPActionButton(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
                    title = "Return",
                    onClick = vm::resetScreen
                )
            } else {
                PaypalRequest(
                    amount = screenModel.amount,
                    onAmountChanged = vm::onAmountChanged,
                    makePayment = vm::makePayment
                )
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
                        append("Find the code below in this files: sample/gpapi/screens/processpayment/paypal/")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("\nPayPalViewModel.kt")
                    }
                },
                codeSampleSnippet = R.drawable.snippet_paypal,
                model = screenModel.gpSnippetResponseModel
            )
        }
    }
}

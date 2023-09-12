package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.paybylink

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.utils.rememberLifecycleEvent

@Composable
fun PayByLinkScreen(vm: PayByLinkViewModel = viewModel()) {

    val screenModel by vm.screenModel.collectAsState()
    val localUriHandler = LocalUriHandler.current

    val lifecycleEvent = rememberLifecycleEvent()
    LaunchedEffect(key1 = lifecycleEvent) {
        if (lifecycleEvent == Lifecycle.Event.ON_RESUME) {
            vm.checkPayLinkStatus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.paylink),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color(0xFF003C71),
            fontWeight = FontWeight.Medium
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

            val sampleResponse = screenModel.gpSampleResponseModel
            if (sampleResponse != null) {
                PayByLinkResponse(
                    gpSampleResponseModel = sampleResponse,
                    onOpenPayLinkClicked = {
                        val uriToOpen = screenModel.uriToOpen
                        if (!uriToOpen.isNullOrBlank()) {
                            localUriHandler.openUri(uriToOpen)
                        }
                    },
                    onResetClicked = vm::resetScreen
                )
            } else {
                PayByLinkRequest(
                    screenModel.amount,
                    screenModel.description,
                    screenModel.expirationDate,
                    screenModel.usageMode,
                    screenModel.usageLimit,
                    vm::onAmountChanged,
                    vm::onDescriptionChanged,
                    vm::onExpirationDateChanged,
                    vm::onPaymentUsageModeChanged,
                    vm::onUsageLimitChanged,
                    vm::getPayLink
                )
            }

            GPSnippetResponse(
                modifier = Modifier.padding(top = 20.dp),
                codeSampleLocation = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp
                        )
                    ) {
                        append("Find the code below in this files: sample/gpapi/screens/processpayment/paybylink/")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("\nPayByLinkViewModel.kt")
                    }
                },
                codeSampleSnippet = R.drawable.snippet_paylink,
                model = screenModel.gpSnippetResponseModel
            )
        }
    }
}

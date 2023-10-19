package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.merchants.transfer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TransferFundsScreen(transferViewModel: TransferFundsViewModel = viewModel()) {

    val screenModel by transferViewModel.screenModel.collectAsState()
    val softInput = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val sampleModel = screenModel.sampleResponseModel
        if (sampleModel == null) {

            GPInputField(
                modifier = Modifier.padding(top = 12.dp),
                title = "Sender Merchant ID",
                value = screenModel.senderMerchantId,
                onValueChanged = transferViewModel::onSenderMerchantIdChanged,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            GPInputField(
                modifier = Modifier.padding(top = 12.dp),
                title = "Receiver Merchant ID",
                value = screenModel.receiverMerchantId,
                onValueChanged = transferViewModel::onReceiverMerchantIdChanged,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            GPInputField(
                modifier = Modifier.padding(top = 12.dp),
                title = "Amount to Transfer",
                value = screenModel.transferAmount,
                onValueChanged = transferViewModel::onTransferAmountChanged,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
            )

            GPSubmitButton(
                modifier = Modifier.padding(top = 12.dp),
                title = "Transfer",
                onClick = {
                    softInput?.hide()
                    transferViewModel.transfer()
                }
            )

        } else {
            GPSampleResponse(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                model = sampleModel
            )

            GPActionButton(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                title = "Reset",
                onClick = transferViewModel::resetScreen
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
                    append("Find the code below in this files: sample/gpapi/screens/expandintegration/merchants/transfer/")
                }
                withStyle(
                    SpanStyle(
                        color = Color(0xFF5A5E6D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("TransferFundsViewModel.kt")
                }
            },
            codeSampleSnippet = R.drawable.snippet_transfer_funds,
            model = screenModel.gpSnippetResponseModel
        )
    }

}

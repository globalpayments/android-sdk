package com.globalpayments.android.sdk.sample.gpapi.bnpl

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.common.compose.TransactionSuccessDialog

@Composable
fun BnplCaptureReverseScreen(
    bnplViewModel: BnplViewModel
) {
    val screenModel by bnplViewModel.screenModel.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            value = screenModel.transaction?.transactionId ?: "",
            onValueChange = {},
            label = { Text("Transaction ID") }
        )


        Button(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 15.dp)
                .fillMaxWidth(),
            onClick = { bnplViewModel.captureTransaction() }) {
            Text("Capture")
        }

        Button(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 15.dp)
                .fillMaxWidth(),
            onClick = { bnplViewModel.reverseTransaction() }) {
            Text("Reverse")
        }
    }

    val transaction = screenModel.transaction
    if (screenModel.showTransactionSuccessDialog && transaction != null) {
        TransactionSuccessDialog(
            transaction = transaction,
            onDismissRequest = {
                if (screenModel.transaction?.responseMessage == "REVERSED") {
                    bnplViewModel.resetState()
                } else {
                    bnplViewModel.goToScreen(ScreenState.Refund)
                }
            })
    }
}

@Preview
@Composable
fun BnplReverseRefundPreview() {
    BnplCaptureReverseScreen(bnplViewModel = BnplViewModel())
}

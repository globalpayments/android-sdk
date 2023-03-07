package com.globalpayments.android.sdk.sample.gpapi.bnpl

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.common.compose.TransactionSuccessDialog

@Composable
fun BnplRefundScreen(bnplViewModel: BnplViewModel) {
    val screenModel by bnplViewModel.screenModel.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = screenModel.bnplRefundScreenModel.amount,
            onValueChange = { bnplViewModel.refundAmountChanged(it) },
            label = { Text("Refund Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(0.5f),
            onClick = { bnplViewModel.onRefundClick() }) {
            Text("Refund")
        }
    }

    val transaction = screenModel.transaction
    if (screenModel.showTransactionSuccessDialog && transaction != null) {
        TransactionSuccessDialog(transaction = transaction, onDismissRequest = { bnplViewModel.resetState() })
    }
}

@Preview
@Composable
fun BnplRefundScreenPreview() {
    BnplRefundScreen(BnplViewModel())
}

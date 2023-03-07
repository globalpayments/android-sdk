package com.globalpayments.android.sdk.sample.common.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.global.api.entities.Transaction

@Composable
fun TransactionSuccessDialog(
    transaction: Transaction,
    onDismissRequest: () -> Unit,
    dialogProperties: DialogProperties = DialogProperties()
) {
    Dialog(
        properties = dialogProperties,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.Start),
                text = "Transaction Response:"
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                value = transaction.transactionId ?: "",
                onValueChange = {},
                label = { Text("Transaction ID") },
                readOnly = true
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                value = transaction.responseCode ?: "",
                onValueChange = {},
                label = { Text("ResultCode") },
                readOnly = true
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                value = transaction.timestamp ?: "",
                label = { Text("Time created") },
                onValueChange = {},
                readOnly = true
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                value = transaction.responseMessage ?: "",
                onValueChange = {},
                label = { Text("Status") },
                readOnly = true
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                value = transaction.balanceAmount?.toString() ?: "",
                onValueChange = {},
                label = { Text("Amount") },
                readOnly = true
            )
            Button(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(0.5f),
                onClick = onDismissRequest
            ) {
                Text("Ok")
            }
        }
    }
}

@Preview
@Composable
fun SuccessTransactionDialogPreview() {
    TransactionSuccessDialog(transaction = Transaction(), onDismissRequest = { /*TODO*/ })
}

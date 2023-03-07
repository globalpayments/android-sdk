package com.globalpayments.android.sdk.sample.common.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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

@Composable
fun TransactionErrorDialog(
    errorMessage: String,
    onDismissRequest: () -> Unit,
    dialogProperties: DialogProperties = DialogProperties()
) {

    Dialog(
        properties = dialogProperties,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                value = errorMessage,
                onValueChange = {},
                readOnly = true,
                label = { Text("Error Message") }
            )

            Button(
                modifier = Modifier
                    .padding(10.dp)
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
fun TransactionErrorDialogPreview() {
    TransactionErrorDialog(errorMessage = "GeneralError", onDismissRequest = { /*TODO*/ })
}

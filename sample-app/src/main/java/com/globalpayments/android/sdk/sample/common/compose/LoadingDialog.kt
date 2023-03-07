package com.globalpayments.android.sdk.sample.common.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
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
fun LoadingDialog(
    text: String,
    onDismiss: () -> Unit = {},
    dialogProperties: DialogProperties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
) {

    Dialog(onDismissRequest = onDismiss, properties = dialogProperties) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator()
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = text
            )
        }
    }
}

@Preview
@Composable
fun LoadingDialogPreview() {
    LoadingDialog(text = "TransactionInProgress")
}

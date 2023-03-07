package com.globalpayments.android.sdk.sample.gpapi.bnpl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.globalpayments.android.sdk.sample.common.compose.TransactionSuccessDialog

@Composable
fun BnplReverseScreen(bnplViewModel: BnplViewModel) {

    LaunchedEffect(key1 = true, block = { bnplViewModel.reverseTransaction() })

    val screenModel by bnplViewModel.screenModel.collectAsState()
    val transaction = screenModel.transaction
    if (screenModel.showTransactionSuccessDialog && transaction != null) {
        TransactionSuccessDialog(transaction = transaction, onDismissRequest = { bnplViewModel.resetState() })
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier, text = "Reversing transaction", fontSize = 18.sp
        )
    }
}

@Preview
@Composable
fun BnplReverseScreenPreview() {
    BnplReverseScreen(bnplViewModel = BnplViewModel())
}

package com.globalpayments.android.sdk.sample.gpapi.openbanking.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.openbanking.models.PaymentDetails

@Composable
fun PaymentScreen(
    paymentDetails: PaymentDetails,
    onBackPressed: () -> Unit,
    onPayClick: (PaymentDetails) -> Unit
) {

    var state: PaymentDetails by remember { mutableStateOf(paymentDetails) }

    Column(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
    ) {

        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            },
            title = {
                Text(modifier = Modifier, text = "Pay")
            },
            backgroundColor = colorResource(id = R.color.colorAccent),
            contentColor = Color.White
        )

        Spacer(modifier = Modifier.weight(1f))

        when (val paymentState = state) {
            is PaymentDetails.FasterPaymentDetails -> FasterPaymentsDetails(Modifier.align(CenterHorizontally), paymentState) { state = it }
            is PaymentDetails.SepaDetails -> SepaPaymentDetails(Modifier.align(CenterHorizontally), paymentState) { state = it }
        }
        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth(0.4f)
                .align(CenterHorizontally),
            onClick = { onPayClick(state) },
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.colorAccent), contentColor = Color.White)
        ) {
            Text("Pay")
        }
    }
}

@Composable
private fun SepaPaymentDetails(
    modifier: Modifier = Modifier,
    paymentDetails: PaymentDetails.SepaDetails,
    onPaymentDetailsChanged: (PaymentDetails) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = paymentDetails.iban,
            onValueChange = { onPaymentDetailsChanged(paymentDetails.copy(iban = it)) },
            label = { Text(text = "IBAN") }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = paymentDetails.accountName,
            onValueChange = { onPaymentDetailsChanged(paymentDetails.copy(accountName = it)) },
            label = { Text(text = "Account Name") }
        )

    }
}

@Composable
private fun FasterPaymentsDetails(
    modifier: Modifier = Modifier,
    paymentDetails: PaymentDetails.FasterPaymentDetails,
    onPaymentDetailsChanged: (PaymentDetails) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = paymentDetails.accountNumber,
            onValueChange = { onPaymentDetailsChanged(paymentDetails.copy(accountNumber = it)) },
            label = { Text(text = "Account number") }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = paymentDetails.sortCode,
            onValueChange = { onPaymentDetailsChanged(paymentDetails.copy(sortCode = it)) },
            label = { Text(text = "Sort code") }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = paymentDetails.accountName,
            onValueChange = { onPaymentDetailsChanged(paymentDetails.copy(accountName = it)) },
            label = { Text(text = "Account name") }
        )

    }
}

package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.paypal

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton
import com.globalpayments.android.sdk.sample.gpapi.utils.PaymentAmountVisualTransformation

@Composable
fun PaypalRequest(
    amount: String,
    onAmountChanged: (String) -> Unit,
    makePayment: (PaymentType) -> Unit,
) {
    GPInputField(
        modifier = Modifier.padding(top = 17.dp),
        title = R.string.payment_amount,
        value = amount,
        onValueChanged = onAmountChanged,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal),
        visualTransformation = PaymentAmountVisualTransformation("$")
    )

    GPSubmitButton(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        title = "Charge",
        onClick = { makePayment(PaymentType.Charge) }
    )

    GPActionButton(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        title = "Authorize",
        onClick = { makePayment(PaymentType.Authorize) }
    )
}

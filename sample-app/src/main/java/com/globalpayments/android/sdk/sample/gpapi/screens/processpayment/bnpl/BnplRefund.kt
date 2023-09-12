package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.bnpl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel

@Composable
fun BnplRefund(
    gpSampleResponseModel: GPSampleResponseModel,
    refundAmount: String,
    onRefundAmountChanged: (String) -> Unit,
    onRefundClicked: () -> Unit,
    onResetClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        GPSampleResponse(model = gpSampleResponseModel)

        GPInputField(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            value = refundAmount,
            onValueChanged = onRefundAmountChanged,
            title = "Refund Amount",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        GPActionButton(
            modifier = Modifier
                .padding(top = 20.dp)
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            onClick = onRefundClicked,
            title = "Refund"
        )

        GPActionButton(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 15.dp)
                .fillMaxWidth(),
            title = "Reset",
            onClick = onResetClicked
        )
    }
}

@Preview
@Composable
fun BnplRefundPreview() {
    BnplRefund(GPSampleResponseModel("", emptyList()), "", {}, {}, {})
}

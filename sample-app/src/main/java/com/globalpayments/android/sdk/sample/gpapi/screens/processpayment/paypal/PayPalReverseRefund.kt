package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.paypal

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel

@Composable
fun PayPalRefundReverseView(
    responseModel: GPSampleResponseModel,
    status: PayPalTransactionStatus,
    onRefundClick: () -> Unit,
    onReverseClick: () -> Unit,
    onCaptureClick: () -> Unit,
    onCancelClick: () -> Unit
) {

    GPSampleResponse(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        model = responseModel
    )

    if (status == PayPalTransactionStatus.Charged) {
        GPActionButton(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            title = "Refund",
            onClick = onRefundClick
        )

        GPActionButton(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            title = "Reverse",
            onClick = onReverseClick
        )
    } else if (status == PayPalTransactionStatus.Authorized) {
        GPActionButton(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            title = "Capture",
            onClick = onCaptureClick
        )
    }

    GPActionButton(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        title = "Return",
        onClick = onCancelClick
    )
}

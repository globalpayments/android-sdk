package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.ebt

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel

@Composable
fun EbtResponse(
    responseModel: GPSampleResponseModel,
    showReverseRefundButtons: Boolean,
    onResetClick: () -> Unit,
    onRefundClick: () -> Unit,
    onReverseClick: () -> Unit
) {
    GPSampleResponse(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        model = responseModel
    )

    if (showReverseRefundButtons) {
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
    }
    GPActionButton(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        title = "Reset",
        onClick = onResetClick
    )
}

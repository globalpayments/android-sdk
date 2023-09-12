package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.bnpl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel

@Composable
fun BnplCaptureReverse(
    gpSampleResponseModel: GPSampleResponseModel,
    onCaptureClick: () -> Unit,
    onReverseClick: () -> Unit,
    onResetClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GPSampleResponse(model = gpSampleResponseModel)


        GPActionButton(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 15.dp)
                .fillMaxWidth(),
            onClick = onCaptureClick,
            title = "Capture"
        )

        GPActionButton(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 15.dp)
                .fillMaxWidth(),
            onClick = onReverseClick,
            title = "Reverse"
        )

        GPActionButton(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 15.dp)
                .fillMaxWidth(),
            title = "Reset",
            onClick = onResetClick
        )
    }
}

@Preview
@Composable
fun BnplCaptureReversePreview() {
    BnplCaptureReverse(GPSampleResponseModel("", emptyList()), {}, {}, {})
}

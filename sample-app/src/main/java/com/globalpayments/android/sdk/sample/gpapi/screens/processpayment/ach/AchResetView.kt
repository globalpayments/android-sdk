package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.ach

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel

@Composable
fun AchResetView(
    responseModel: GPSampleResponseModel,
    onResetClick: () -> Unit
) {
    GPSampleResponse(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        model = responseModel
    )

    GPActionButton(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        title = "Reset",
        onClick = onResetClick
    )
}

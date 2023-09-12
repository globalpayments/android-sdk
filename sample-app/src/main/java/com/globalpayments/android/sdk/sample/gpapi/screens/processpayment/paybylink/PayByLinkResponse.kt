package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.paybylink

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton

@Composable
fun PayByLinkResponse(
    gpSampleResponseModel: GPSampleResponseModel,
    onOpenPayLinkClicked: () -> Unit,
    onResetClicked: () -> Unit
) {

    GPSampleResponse(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        model = gpSampleResponseModel
    )

    GPSubmitButton(
        modifier = Modifier.padding(top = 12.dp),
        title = "Open Link to Pay",
        onClick = onOpenPayLinkClicked
    )

    GPActionButton(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        title = "Reset",
        onClick = onResetClicked
    )


}

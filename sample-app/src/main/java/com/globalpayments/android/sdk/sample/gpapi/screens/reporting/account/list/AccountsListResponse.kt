package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.account.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel

@Composable
fun AccountsListResponse(
    responses: List<GPSampleResponseModel>,
    loadMore: () -> Unit,
    reset: () -> Unit
) {

    GPActionButton(
        modifier = Modifier
            .padding(top = 25.dp, bottom = 10.dp)
            .fillMaxWidth(),
        title = "Run New Report",
        onClick = reset
    )

    for (response in responses) {
        GPSampleResponse(
            modifier = Modifier.padding(top = 15.dp),
            model = response
        )
    }

    GPActionButton(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        title = "Load More",
        onClick = loadMore
    )
}

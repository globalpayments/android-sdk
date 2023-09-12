package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.actions.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse

@Composable
fun ActionsListResponse(
    viewModel: ActionsListViewModel
) {
    val model by viewModel.screenModel.collectAsState()

    GPActionButton(
        modifier = Modifier
            .padding(top = 25.dp, bottom = 10.dp)
            .fillMaxWidth(),
        title = "Run New Report",
        onClick = viewModel::resetListTransaction
    )

    for (response in model.responses) {
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
        onClick = viewModel::loadMore
    )
}

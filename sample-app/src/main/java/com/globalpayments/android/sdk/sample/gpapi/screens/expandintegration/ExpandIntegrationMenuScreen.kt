package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPButton
import com.globalpayments.android.sdk.sample.gpapi.navigation.NavigationManager
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.BatchesDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.DisputesDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.StoredPaymentsMethodsDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.VerificationsDirection
import kotlinx.coroutines.launch

@Composable
fun ExpandIntegrationMenuScreen() {

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = stringResource(id = R.string.expand_your_integration),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color(0xFF003C71),
            fontWeight = FontWeight.Medium
        )

        GPButton(
            modifier = Modifier.padding(top = 20.dp),
            title = stringResource(id = R.string.stored_payments_methods),
            description = "Initiate a payment method operation.",
            onClick = { coroutineScope.launch { NavigationManager.navigate(StoredPaymentsMethodsDirection) } }
        )
        GPButton(
            modifier = Modifier.padding(top = 20.dp),
            title = "Verifications",
            description = "Initiate a verification.",
            onClick = { coroutineScope.launch { NavigationManager.navigate(VerificationsDirection) } }
        )
        GPButton(
            modifier = Modifier.padding(top = 20.dp),
            title = "Dispute Operations",
            description = "Accept or Challenge a dispute.",
            onClick = { coroutineScope.launch { NavigationManager.navigate(DisputesDirection) } }
        )
        GPButton(
            modifier = Modifier.padding(top = 20.dp, bottom = 30.dp),
            title = "Batches",
            description = "Close a batch.",
            onClick = { coroutineScope.launch { NavigationManager.navigate(BatchesDirection) } }
        )
    }
}

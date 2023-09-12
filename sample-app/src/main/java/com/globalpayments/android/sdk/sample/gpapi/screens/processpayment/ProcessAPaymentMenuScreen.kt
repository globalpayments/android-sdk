package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment

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
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.AchDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.BNPLDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.CTPDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.EbtDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.GooglePayDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.HostedFieldsDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.PayLinkDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.PayPalDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.UnifiedPaymentsAPIDirection
import kotlinx.coroutines.launch

@Composable
fun ProcessAPaymentMenuScreen() {

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = stringResource(id = R.string.process_a_payment),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color(0xFF003C71),
            fontWeight = FontWeight.Medium
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GPButton(
                modifier = Modifier.padding(top = 20.dp),
                title = "Hosted Fields",
                description = "Embed a hosted payment form.",
                onClick = { coroutineScope.launch { NavigationManager.navigate(HostedFieldsDirection) } }
            )
            GPButton(
                modifier = Modifier.padding(top = 20.dp),
                title = "Unified Payments API",
                description = "Process payments through our API.",
                onClick = { coroutineScope.launch { NavigationManager.navigate(UnifiedPaymentsAPIDirection) } }
            )
            GPButton(
                modifier = Modifier.padding(top = 20.dp),
                title = "Google Pay",
                description = "Process payments via Google Pay.",
                onClick = { coroutineScope.launch { NavigationManager.navigate(GooglePayDirection) } }
            )
            GPButton(
                modifier = Modifier.padding(top = 20.dp),
                title = "PayPal",
                description = "Process payments via PayPal",
                onClick = { coroutineScope.launch { NavigationManager.navigate(PayPalDirection) } }
            )
            GPButton(
                modifier = Modifier.padding(top = 20.dp),
                title = "PayByLink",
                description = "Process payments via PayByLink",
                onClick = { coroutineScope.launch { NavigationManager.navigate(PayLinkDirection) } }
            )
            GPButton(
                modifier = Modifier.padding(top = 20.dp),
                title = "ACH",
                description = "Send an electronic funds transfer.",
                onClick = { coroutineScope.launch { NavigationManager.navigate(AchDirection) } }
            )
            GPButton(
                modifier = Modifier.padding(top = 20.dp),
                title = "EBT",
                description = "Send an electronic benefits transfer.",
                onClick = { coroutineScope.launch { NavigationManager.navigate(EbtDirection) } }
            )
            GPButton(
                modifier = Modifier.padding(top = 20.dp),
                title = "Buy Now Pay Later",
                description = "",
                onClick = { coroutineScope.launch { NavigationManager.navigate(BNPLDirection) } }
            )
            GPButton(
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
                title = "Click to Pay",
                description = "",
                onClick = { coroutineScope.launch { NavigationManager.navigate(CTPDirection) } }
            )
        }
    }
}

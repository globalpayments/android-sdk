package com.globalpayments.android.sdk.sample.gpapi.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPButton

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = stringResource(id = R.string.home_screen_title),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = Color(0xFF003C71),
            fontWeight = FontWeight.Medium
        )

        Text(
            modifier = Modifier.padding(top = 13.dp),
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = Color(0xFF5A5E6D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(stringResource(R.string.welcome))
                }
                withStyle(
                    SpanStyle(
                        color = Color(0xFF5A5E6D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                    )
                ) {
                    append(stringResource(R.string.browse_rest_of_the_app))
                }
            }
        )

        GPButton(
            modifier = Modifier.padding(top = 20.dp),
            title = stringResource(id = R.string.create_access_token),
            description = stringResource(R.string.create_access_token_description),
            icon = R.drawable.ic_access_token,
            onClick = homeViewModel::goToAccessToken
        )
        GPButton(
            modifier = Modifier.padding(top = 15.dp),
            title = stringResource(id = R.string.process_a_payment),
            description = stringResource(R.string.process_payment_description),
            icon = R.drawable.ic_credit_sale,
            onClick = homeViewModel::goToProcessAPayment
        )
        GPButton(
            modifier = Modifier.padding(top = 15.dp),
            title = stringResource(id = R.string.expand_your_integration),
            description = stringResource(R.string.expand_integration_description),
            icon = R.drawable.ic_multi_purchase,
            onClick = homeViewModel::goToExpandIntegration
        )
        GPButton(
            modifier = Modifier.padding(top = 15.dp),
            title = stringResource(id = R.string.reporting),
            description = stringResource(R.string.reporting_description),
            icon = R.drawable.ic_reporting,
            onClick = homeViewModel::goToReporting
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

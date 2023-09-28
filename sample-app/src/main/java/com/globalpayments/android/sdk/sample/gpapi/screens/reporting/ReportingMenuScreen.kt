package com.globalpayments.android.sdk.sample.gpapi.screens.reporting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPScreenTitle
import com.globalpayments.android.sdk.sample.gpapi.navigation.NavigationManager
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.AccountsReportingDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.ActionsReportingDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.DepositsReportingDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.DisputesReportingDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.PaymentMethodsReportingDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.TransactionsReportingDirection
import kotlinx.coroutines.launch

@Composable
fun ReportingMenuScreen() {

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GPScreenTitle(
            modifier = Modifier.padding(top = 20.dp),
            title = stringResource(id = R.string.reporting),
        )

        GPButton(
            modifier = Modifier.padding(top = 20.dp),
            title = "Transactions",
            description = "Get a single or list of transactions.",
            onClick = { coroutineScope.launch { NavigationManager.navigate(TransactionsReportingDirection) } }
        )
        GPButton(
            modifier = Modifier.padding(top = 20.dp),
            title = "Deposits",
            description = "Get a single or list of deposits.",
            onClick = { coroutineScope.launch { NavigationManager.navigate(DepositsReportingDirection) } }
        )
        GPButton(
            modifier = Modifier.padding(top = 20.dp),
            title = "Payment Methods",
            description = "Get a single or list of payment methods.",
            onClick = { coroutineScope.launch { NavigationManager.navigate(PaymentMethodsReportingDirection) } }
        )
        GPButton(
            modifier = Modifier.padding(top = 20.dp),
            title = "Actions",
            description = "Get a single or list of actions.",
            onClick = { coroutineScope.launch { NavigationManager.navigate(ActionsReportingDirection) } }
        )
        GPButton(
            modifier = Modifier.padding(top = 20.dp),
            title = "Disputes",
            description = "Get a single or list of disputes or documents.",
            onClick = { coroutineScope.launch { NavigationManager.navigate(DisputesReportingDirection) } }
        )
        GPButton(
            modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
            title = "Accounts",
            description = "Get a single or list of accounts.",
            onClick = { coroutineScope.launch { NavigationManager.navigate(AccountsReportingDirection) } }
        )
    }
}

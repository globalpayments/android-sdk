package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.transactions

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPScreenTitle
import com.globalpayments.android.sdk.sample.gpapi.components.GPTab
import com.globalpayments.android.sdk.sample.gpapi.screens.reporting.transactions.list.TransactionsListScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.reporting.transactions.single.TransactionSingleScreen

enum class TransactionType {
    Single, List
}

@Composable
fun TransactionsScreen() {

    var currentTab: TransactionType by remember { mutableStateOf(TransactionType.Single) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GPScreenTitle(
            modifier = Modifier,
            title = stringResource(id = R.string.get_transactions),
        )

        GPTab(
            modifier = Modifier.padding(top = 25.dp),
            tabs = TransactionType.entries,
            currentTab
        ) { currentTab = it }

        AnimatedContent(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxSize(),
            targetState = currentTab,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally(animationSpec = tween()) { fullWidth -> 2 * fullWidth } togetherWith slideOutHorizontally(animationSpec = tween()) { fullWidth -> -fullWidth }
                } else {
                    slideInHorizontally(animationSpec = tween()) { fullWidth -> -fullWidth } togetherWith slideOutHorizontally(animationSpec = tween()) { fullWidth -> 2 * fullWidth }
                }
            }, label = ""
        ) { targetPage ->
            when (targetPage) {
                TransactionType.Single -> TransactionSingleScreen(modifier = Modifier.fillMaxSize())
                TransactionType.List -> TransactionsListScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

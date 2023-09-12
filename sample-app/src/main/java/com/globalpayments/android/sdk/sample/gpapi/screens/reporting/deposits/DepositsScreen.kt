package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.deposits

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPTab
import com.globalpayments.android.sdk.sample.gpapi.screens.reporting.deposits.list.DepositsListScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.reporting.deposits.single.DepositsSingleScreen

enum class DepositsScreenTab {
    Single, List
}

@Composable
fun DepositsScreen() {

    var currentTab: DepositsScreenTab by remember { mutableStateOf(DepositsScreenTab.Single) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.get_deposits),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color(0xFF003C71)
        )

        GPTab(
            modifier = Modifier.padding(top = 25.dp),
            tabs = DepositsScreenTab.entries,
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
            },
            label = "DepositsPages"
        ) { targetPage ->
            when (targetPage) {
                DepositsScreenTab.Single -> DepositsSingleScreen(modifier = Modifier.fillMaxSize())
                DepositsScreenTab.List -> DepositsListScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

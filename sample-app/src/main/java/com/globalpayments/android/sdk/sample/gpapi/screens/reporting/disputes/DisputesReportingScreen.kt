package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.disputes

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
import com.globalpayments.android.sdk.sample.gpapi.screens.reporting.disputes.list.DisputesListScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.reporting.disputes.single.DisputesSingleScreen

enum class DisputesScreenTab {
    Single, List
}

@Composable
fun DisputesReportingScreen() {

    var currentTab: DisputesScreenTab by remember { mutableStateOf(DisputesScreenTab.Single) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.get_disputes),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color(0xFF003C71)
        )

        GPTab(
            modifier = Modifier.padding(top = 25.dp),
            tabs = DisputesScreenTab.entries,
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
            label = "DisputesPages"
        ) { targetPage ->
            when (targetPage) {
                DisputesScreenTab.Single -> DisputesSingleScreen(modifier = Modifier.fillMaxSize())
                DisputesScreenTab.List -> DisputesListScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

package com.globalpayments.android.sdk.sample.gpapi.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun <T> GPTab(
    modifier: Modifier = Modifier,
    tabs: List<T>,
    selectedTab: T,
    onTabSelected: (T) -> Unit
) {

    TabRow(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFD2D8E1), RoundedCornerShape(4.dp)),
        selectedTabIndex = tabs.indexOf(selectedTab),
        contentColor = Color(0xFF5A5E6D),
        containerColor = Color(0xFFF9F9F9),
        divider = {},
        indicator = {
            GPTabIndicator(
                modifier = Modifier.tabIndicatorOffset(it[tabs.indexOf(selectedTab)], 5.dp),
                textToDisplay = selectedTab.toString()
            )
        }
    ) {
        for (tab in tabs) {
            Tab(
                modifier = Modifier,
                selected = false,
                text = { Text(text = tab.toString(), fontSize = 16.sp) },
                onClick = { onTabSelected(tab) }
            )
        }
    }

}

fun Modifier.tabIndicatorOffset(
    currentTabPosition: TabPosition,
    horizontalPadding: Dp,
): Modifier = this.then(composed(
    inspectorInfo = debugInspectorInfo {
        name = "tabIndicatorOffset"
        value = currentTabPosition
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = currentTabPosition.width,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "currentTabWidth"
    )
    val indicatorOffset by animateDpAsState(
        targetValue = currentTabPosition.left,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "indicatorOffset"
    )
    padding(start = horizontalPadding)
        .padding(vertical = 3.dp)
        .fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth - horizontalPadding * 2)
})

@Composable
fun GPTabIndicator(
    modifier: Modifier,
    textToDisplay: String
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
            .border(1.dp, Color(0xFF0074C7), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = textToDisplay,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2E3038)
        )
    }
}

package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.fileprocessing

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
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPScreenTitle
import com.globalpayments.android.sdk.sample.gpapi.components.GPTab
import com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.fileprocessing.retrieve.RetrieveFileUploadedStatusScreen
import com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.fileprocessing.upload.UploadFileForProcessingScreen

enum class FileProcessingScreenTab {
    Upload, Retrieve
}

@Composable
fun FileProcessingScreen() {
    var currentTab: FileProcessingScreenTab by remember { mutableStateOf(FileProcessingScreenTab.Upload) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GPScreenTitle(
            modifier = Modifier,
            title = "File Processing",
        )

        GPTab(
            modifier = Modifier.padding(top = 25.dp),
            tabs = FileProcessingScreenTab.entries,
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
                FileProcessingScreenTab.Upload -> UploadFileForProcessingScreen()
                FileProcessingScreenTab.Retrieve -> RetrieveFileUploadedStatusScreen()
            }
        }
    }
}

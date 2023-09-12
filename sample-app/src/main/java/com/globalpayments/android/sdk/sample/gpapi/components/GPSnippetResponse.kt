package com.globalpayments.android.sdk.sample.gpapi.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private enum class Tab(val title: String) {
    Response("Response"), CodeExample("Code Example");

    override fun toString(): String {
        return title
    }
}

data class GPSnippetResponseModel(
    val responseName: String = "",
    val response: List<Pair<String, String>> = emptyList(),
    val isResponseWithError: Boolean = false
)

@Composable
fun GPSnippetResponse(
    modifier: Modifier = Modifier,
    codeSampleLocation: AnnotatedString,
    @DrawableRes codeSampleSnippet: Int,
    model: GPSnippetResponseModel,
) {
    var currentTab: Tab by remember { mutableStateOf(Tab.Response) }

    Column(modifier = modifier.fillMaxWidth()) {
        GPTab(
            modifier = Modifier,
            tabs = Tab.entries,
            selectedTab = currentTab,
            onTabSelected = { currentTab = it }
        )

        AnimatedContent(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            targetState = currentTab,
            transitionSpec = {
                if (targetState.ordinal > initialState.ordinal) {
                    slideInHorizontally(animationSpec = tween()) { fullWidth -> 2 * fullWidth } togetherWith slideOutHorizontally(animationSpec = tween()) { fullWidth -> -fullWidth }
                } else {
                    slideInHorizontally(animationSpec = tween()) { fullWidth -> -fullWidth } togetherWith slideOutHorizontally(animationSpec = tween()) { fullWidth -> 2 * fullWidth }
                }
            }, label = ""
        ) {

            when (it) {
                Tab.CodeExample -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = codeSampleLocation
                        )
                        GPCodeSnippet(
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .fillMaxWidth(),
                            codeSnippet = codeSampleSnippet
                        )
                    }
                }

                Tab.Response -> {
                    if (model.response.isNotEmpty()) {
                        GPResponse(
                            modifier = Modifier
                                .padding(bottom = 26.dp)
                                .fillMaxWidth(),
                            fields = model.response,
                            titleText = model.responseName,
                            isSuccess = !model.isResponseWithError
                        )
                    } else {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Make a request to see the response",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

}

package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.ach

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPScreenTitle
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse

@Composable
fun AchScreen(vm: AchViewModel = viewModel()) {

    val screenModel by vm.screenModel.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GPScreenTitle(
            modifier = Modifier,
            title = stringResource(id = R.string.configuration)
        )
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.ach),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color(0xFF003C71),
            fontWeight = FontWeight.Medium
        )

        Divider(
            modifier = Modifier
                .padding(top = 22.dp)
                .fillMaxWidth(),
            color = Color(0xFFD7DCE1)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            val gpSampleResponseModel = screenModel.gpSampleResponseModel
            if (gpSampleResponseModel != null) {
                AchResetView(gpSampleResponseModel, vm::reset)
            } else {
                AchRequest(achViewModel = vm)
            }

            Divider(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                color = Color(0xFFD7DCE1)
            )

            GPSnippetResponse(
                modifier = Modifier.padding(top = 20.dp),
                codeSampleLocation = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp
                        )
                    ) {
                        append("Find the code below in this files: sample/gpapi/screens/processpayment/ach/")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("\nAchViewModel.kt")
                    }
                },
                codeSampleSnippet = R.drawable.snippet_ach,
                model = screenModel.gpSnippetResponseModel
            )
        }
    }
}

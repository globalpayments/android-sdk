package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.deposits.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse

@Composable
fun DepositsListScreen(
    modifier: Modifier = Modifier,
    vm: DepositsListViewModel = viewModel()
) {

    val screenModel by vm.screenModel.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {

        if (screenModel.responses.isEmpty()) {
            DepositsListParams(viewModel = vm)
        } else {
            DepositsListResponse(viewModel = vm)
        }

        Divider(
            modifier = Modifier
                .padding(top = 17.dp)
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
                    append("Find the code below in this files: sample/gpapi/screens/reporting/deposits/list/")
                }
                withStyle(
                    SpanStyle(
                        color = Color(0xFF5A5E6D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("DepositsListViewModel.kt")
                }
            },
            codeSampleSnippet = R.drawable.snippet_report_deposits_list,
            model = screenModel.gpSnippetResponseModel
        )
    }

}

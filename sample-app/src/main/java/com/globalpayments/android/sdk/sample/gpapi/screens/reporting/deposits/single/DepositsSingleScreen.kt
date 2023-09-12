package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.deposits.single

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DepositsSingleScreen(modifier: Modifier = Modifier, vm: DepositsSingleViewModel = viewModel()) {
    Column(modifier = modifier.fillMaxWidth()) {

        val inputHandler = LocalSoftwareKeyboardController.current
        val screenModel by vm.screenModel.collectAsState()

        GPInputField(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            title = R.string.deposit_id,
            value = screenModel.depositId,
            onValueChanged = vm::onDepositIdChanged,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(onGo = { vm.getDeposit();inputHandler?.hide() })
        )

        GPSubmitButton(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            title = stringResource(R.string.get_deposit_by_id),
            onClick = vm::getDeposit
        )

        Divider(
            modifier = Modifier
                .padding(top = 17.dp)
                .fillMaxWidth(),
            color = Color(0xFFD7DCE1)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            GPSnippetResponse(
                modifier = Modifier.padding(top = 20.dp),
                codeSampleLocation = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp
                        )
                    ) {
                        append("Find the code below in this files: sample/gpapi/screens/reporting/deposits/single/")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("DepositsSingleViewModel.kt")
                    }
                },

                codeSampleSnippet = R.drawable.snippet_report_deposits_single,
                model = screenModel.gpSnippetResponseModel
            )
        }
    }
}

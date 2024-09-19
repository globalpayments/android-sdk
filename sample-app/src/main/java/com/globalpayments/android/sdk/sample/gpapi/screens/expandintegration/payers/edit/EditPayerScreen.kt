package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.payers.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton

@Composable
fun EditPayerScreen(editPayerScreenViewModel: EditPayerScreenViewModel = viewModel()) {
    val softInput = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val screenModel by editPayerScreenViewModel.screenModel.collectAsState()
        val sampleModel = screenModel.sampleResponseModel
        if (sampleModel == null) {

            GPInputField(
                modifier = Modifier.padding(top = 12.dp),
                title = "Id",
                value = screenModel.id,
                onValueChanged = editPayerScreenViewModel::onIdChanged,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )

            GPInputField(
                modifier = Modifier.padding(top = 12.dp),
                title = "First Name",
                value = screenModel.firstName,
                onValueChanged = editPayerScreenViewModel::onFirstNameChanged,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            GPInputField(
                modifier = Modifier.padding(top = 12.dp),
                title = "Last Name",
                value = screenModel.lastName,
                onValueChanged = editPayerScreenViewModel::onLastNameChanged,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            GPSubmitButton(
                modifier = Modifier.padding(top = 12.dp),
                title = "Edit Payer",
                onClick = {
                    softInput?.hide()
                    editPayerScreenViewModel.editPayer()
                }
            )
        } else {
            GPSampleResponse(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                model = sampleModel
            )

            GPActionButton(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                title = "Reset",
                onClick = editPayerScreenViewModel::resetScreen
            )
        }

        GPSnippetResponse(
            modifier = Modifier.padding(top = 20.dp),
            codeSampleLocation = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = Color(0xFF5A5E6D),
                        fontSize = 14.sp
                    )
                ) {
                    append("Find the code below in this files: sample/gpapi/screens/expandintegration/payers/edit/")
                }
                withStyle(
                    SpanStyle(
                        color = Color(0xFF5A5E6D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("EditPayerScreenViewModel.kt")
                }
            },
            codeSampleSnippet = R.drawable.snippet_payer_edit,
            model = screenModel.gpSnippetResponseModel
        )
    }
}

package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.disputes

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.globalpayments.android.sdk.model.DisputeDocumentMimeType
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPDropdown
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPScreenTitle
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DisputesScreen(disputesVM: DisputesScreenViewModel = viewModel()) {

    val context = LocalContext.current
    val selectFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { fileUri ->
            if (fileUri != null) {
                disputesVM.onFileSelected(fileUri, context)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GPScreenTitle(
            modifier = Modifier,
            title = stringResource(id = R.string.disputes),
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

            val screenModel by disputesVM.screenModel.collectAsState()
            val softInput = LocalSoftwareKeyboardController.current

            GPDropdown(
                modifier = Modifier.padding(top = 12.dp),
                title = R.string.operation_type,
                selectedValue = screenModel.operationType,
                values = DisputeOperationType.entries,
                onValueSelected = disputesVM::onOperationTypeChanged
            )

            GPInputField(
                modifier = Modifier.padding(top = 12.dp),
                title = R.string.dispute_id,
                value = screenModel.disputeId,
                onValueChanged = disputesVM::onDisputeIdChanged,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done)
            )

            GPInputField(
                modifier = Modifier
                    .padding(top = 12.dp),
                title = R.string.idempotency_key,
                value = screenModel.idempotencyKey,
                hint = stringResource(id = R.string.optional),
                onValueChanged = disputesVM::onIdempotencyKeyChanged,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            if (screenModel.operationType == DisputeOperationType.CHALLENGE) {
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = stringResource(id = R.string.dispute_documents),
                    fontSize = 14.sp,
                    color = Color(0xFF2E3038),
                    maxLines = 1
                )
                screenModel.files.forEach { file ->
                    Row(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .height(80.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GPInputField(
                            modifier = Modifier.weight(1f),
                            value = file.fileName,
                            onValueChanged = { },
                            title = "File Name",
                            enabled = false
                        )
                        GPDropdown(
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .weight(1f),
                            title = "Type",
                            values = DisputeDocumentType.entries,
                            selectedValue = file.fileType,
                            onValueSelected = {
                                disputesVM.onFileTypeChanged(file, it)
                            }
                        )

                        IconButton(
                            modifier = Modifier
                                .padding(top = 20.dp)
                                .weight(0.2f),
                            onClick = { disputesVM.removeFile(file) }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    }
                }

                GPActionButton(
                    modifier = Modifier.padding(top = 12.dp),
                    title = "Add Document"
                ) {
                    selectFileLauncher.launch(DisputeDocumentMimeType.getMimeTypes())
                }
            }

            GPSubmitButton(
                modifier = Modifier.padding(top = 12.dp),
                title = stringResource(id = R.string.initiate_dispute_operation),
                onClick = {
                    softInput?.hide()
                    disputesVM.onSubmitClicked()
                }
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
                        append("Find the code below in this files: sample/gpapi/screens/disputes/")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("DisputesScreenViewModel.kt")
                    }
                },
                codeSampleSnippet = R.drawable.snippet_batches,
                model = screenModel.gpSnippetResponseModel
            )
        }
    }

}

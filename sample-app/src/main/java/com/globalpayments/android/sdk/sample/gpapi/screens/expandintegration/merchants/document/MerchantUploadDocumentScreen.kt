package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.merchants.document

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.ExperimentalComposeUiApi
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
import com.global.api.entities.enums.DocumentCategory
import com.global.api.entities.enums.FileType
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPDropdown
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MerchantUploadDocumentScreen(vm: MerchantUploadDocumentViewModel = viewModel()) {

    val screenModel by vm.screenModel.collectAsState()
    val softInput = LocalSoftwareKeyboardController.current

    val documentPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                vm.loadDocument(uri)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val sampleModel = screenModel.sampleResponseModel
        if (sampleModel == null) {

            GPInputField(
                modifier = Modifier.padding(top = 12.dp),
                title = "Merchant ID",
                value = screenModel.merchantId,
                onValueChanged = vm::onMerchantIdChanged,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            GPDropdown(
                modifier = Modifier.padding(top = 12.dp),
                title = "Document type",
                selectedValue = screenModel.documentType,
                values = FileType.entries,
                onValueSelected = vm::onDocumentTypeChanged
            )

            GPDropdown(
                modifier = Modifier.padding(top = 12.dp),
                title = "Document category",
                selectedValue = screenModel.documentCategory,
                values = DocumentCategory.entries,
                onValueSelected = vm::onDocumentCategoryChanged
            )

            GPActionButton(
                modifier = Modifier.padding(top = 12.dp),
                title = "Select document"
            ) {
                documentPicker.launch(arrayOf("*/*"))
            }

            val document = screenModel.document
            if (document != null) {
                GPInputField(
                    modifier = Modifier.padding(top = 12.dp),
                    title = "Document name",
                    value = document.name,
                    onValueChanged = {},
                    enabled = false
                )
            }

            GPSubmitButton(
                modifier = Modifier.padding(top = 12.dp),
                title = "Upload document",
                onClick = {
                    softInput?.hide()
                    vm.uploadDocument()
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
                onClick = vm::resetScreen
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
                    append("Find the code below in this files: sample/gpapi/screens/expandintegration/merchants/document/")
                }
                withStyle(
                    SpanStyle(
                        color = Color(0xFF5A5E6D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("MerchantUploadDocumentViewModel.kt")
                }
            },
            codeSampleSnippet = R.drawable.snippet_upload_document,
            model = screenModel.gpSnippetResponseModel
        )
    }


}

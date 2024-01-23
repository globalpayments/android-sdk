package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.fileprocessing.upload

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton

@Composable
fun UploadFileForProcessingScreen(fpViewModel: FileProcessingViewModel = viewModel()) {

    val screenModel by fpViewModel.screenModel.collectAsState()

    val selectFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { fileUri ->
            if (fileUri != null) {
                fpViewModel.onFileSelected(fileUri)
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val sampleModel = screenModel.sampleResponseModel
        if (sampleModel == null) {

            GPActionButton(
                modifier = Modifier.padding(top = 12.dp),
                title = "Add Document"
            ) {
                selectFileLauncher.launch(arrayOf("text/*"))
            }

            GPSubmitButton(
                modifier = Modifier.padding(top = 12.dp),
                title = "Initiate File Processing",
                enabled = screenModel.fileToUpload != null,
                onClick = {
                    fpViewModel.onSubmitClicked()
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
                onClick = fpViewModel::resetScreen
            )
        }

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
                        append("Find the code below in this files: sample/gpapi/screens/expandintegration/fileprocessing/")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("FileProcessingViewModel.kt")
                    }
                },

                codeSampleSnippet = R.drawable.snippet_file_processing,
                model = screenModel.gpSnippetResponseModel
            )
        }
    }
}

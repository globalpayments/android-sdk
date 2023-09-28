package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.ctp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPScreenTitle
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton
import com.globalpayments.android.sdk.sample.gpapi.utils.PaymentAmountVisualTransformation
import com.globalpayments.android.sdk.ui.clicktopay.ClickToPayFragment
import com.globalpayments.android.sdk.ui.clicktopay.ClickToPayModel

@Composable
fun CtpScreen(ctpVM: CtpViewModel = viewModel()) {

    val context = LocalContext.current
    val screenModel by ctpVM.screenModel.collectAsState()

    LaunchedEffect(key1 = screenModel.accessToken) {
        if (screenModel.accessToken.isNotBlank()) {
            showClickToPayDialog(
                context as FragmentActivity,
                screenModel.amount,
                screenModel.accessToken,
                ctpVM::captureTransaction,
                ctpVM::onErrorReceived
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GPScreenTitle(
            modifier = Modifier,
            title = "Click to Pay",
        )

        Divider(
            modifier = Modifier
                .padding(top = 22.dp)
                .fillMaxWidth(),
            color = Color(0xFFD7DCE1)
        )

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            val sampleResponse = screenModel.gpSampleResponseModel
            if (sampleResponse != null) {

                GPSampleResponse(model = sampleResponse)
                GPActionButton(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 15.dp)
                        .fillMaxWidth(),
                    title = "Reset",
                    onClick = ctpVM::reset
                )
            } else {
                GPInputField(
                    modifier = Modifier.padding(top = 17.dp),
                    title = "Amount",
                    value = screenModel.amount,
                    onValueChanged = ctpVM::onAmountChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal),
                    visualTransformation = PaymentAmountVisualTransformation("€")
                )

                GPSubmitButton(
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .align(Alignment.CenterHorizontally),
                    onClick = ctpVM::requestAccessToken,
                    title = "Start Click to Pay flow"
                )
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
                        append("Find the code below in this files: sample/gpapi/screens/processpayment/ctp/")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("\nCtpViewModel.kt")
                    }
                },
                codeSampleSnippet = R.drawable.snippet_ctp,
                model = screenModel.gpSnippetResponseModel
            )
        }
    }
}

private fun showClickToPayDialog(
    activity: FragmentActivity,
    amount: String,
    token: String,
    captureTransaction: (String, String) -> Unit,
    onErrorReceived: (Exception) -> Unit
) {
    val clickToPayModel =
        ClickToPayModel(
            amount = amount,
            accessKey = token,
            javaScriptToEvaluate = "initGlobalPayments('sandbox','$token', '$amount', 'd83e8615-9d0a-46fe-9677-8040887e27fa')"
        )
    val clickToPayDialog = ClickToPayFragment.newInstance(clickToPayModel)
    activity.supportFragmentManager.setFragmentResultListener(ClickToPayFragment.ClickToPayResultKey, activity) { _, bundle ->
        val cardToken = bundle.getString(ClickToPayFragment.ClickToPayTokenKey)
        if (cardToken != null) {
            captureTransaction(cardToken, amount)
            return@setFragmentResultListener
        }
        val error = bundle.getString(ClickToPayFragment.ClickToPayErrorKey) ?: "Error"
        onErrorReceived(Exception(error))
    }
    clickToPayDialog.show(activity.supportFragmentManager, "ClickToPayDialog")
}

package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.googlepay

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.utils.PaymentAmountVisualTransformation
import com.google.android.gms.wallet.PaymentData
import com.google.pay.button.ButtonType
import com.google.pay.button.PayButton

@Composable
fun GooglePayScreen(
    context: Context = LocalContext.current,
    vm: GooglePayViewModel = viewModel(initializer = { GooglePayViewModel(context) })
) {

    val screenModel by vm.screenModel.collectAsState()

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(), onResult = { result ->
        when (result.resultCode) {
            AppCompatActivity.RESULT_OK -> result.data?.let { intent ->
                PaymentData.getFromIntent(intent)?.let(vm::handlePaymentSuccess)
            }

            AppCompatActivity.RESULT_CANCELED -> {
                // The user cancelled the payment attempt
            }
        }
    })


    val pendingIntent = screenModel.pendingIntent
    LaunchedEffect(key1 = pendingIntent) {
        if (pendingIntent == null) return@LaunchedEffect
        launcher.launch(IntentSenderRequest.Builder(pendingIntent).build())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.google_pay),
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

            GPInputField(
                modifier = Modifier.padding(top = 17.dp),
                title = R.string.payment_amount,
                value = screenModel.amount,
                onValueChanged = vm::onAmountChanged,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal),
                visualTransformation = PaymentAmountVisualTransformation("$")
            )

            if (screenModel.isGooglePayAvailable) {
                PayButton(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .align(CenterHorizontally),
                    onClick = vm::makePayment,
                    allowedPaymentMethods = screenModel.allowedPaymentMethods,
                    type = ButtonType.Pay
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
                        append("Find the code below in this files: sample/gpapi/screens/processpayment/googlepay/")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("\nGooglePayViewModel.kt")
                    }
                },
                codeSampleSnippet = R.drawable.snippet_google_pay,
                model = screenModel.gpSnippetResponseModel
            )
        }
    }
}

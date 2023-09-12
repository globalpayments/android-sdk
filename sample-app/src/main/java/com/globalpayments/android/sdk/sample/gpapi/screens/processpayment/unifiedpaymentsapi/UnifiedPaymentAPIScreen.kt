package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.unifiedpaymentsapi

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSelectableField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSwitch
import com.globalpayments.android.sdk.sample.gpapi.utils.PaymentAmountVisualTransformation
import com.globalpayments.android.sdk.sample.gpapi.utils.showDatePicker

@Composable
fun UnifiedPaymentAPIScreen(
    context: Context = LocalContext.current,
    vm: UnifiedPaymentAPIViewModel = viewModel(initializer = { UnifiedPaymentAPIViewModel(context) })
) {

    val screenModel by vm.screenModel.collectAsState()

    LaunchedEffect(key1 = screenModel.netceteraTransactionParams) {
        val params = screenModel.netceteraTransactionParams
        if (params != null) {
            vm.doChallenge(context as Activity, params)
        }
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
            text = stringResource(id = R.string.unified_payments_api),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color(0xFF003C71),
            fontWeight = FontWeight.Medium
        )

        Text(
            modifier = Modifier.padding(top = 11.dp),
            text = stringResource(id = R.string.unified_payments_api_long_description),
            fontSize = 14.sp,
            color = Color(0xFF5A5E6D)
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

            val responseModel = screenModel.gpSampleResponseModel
            if (responseModel == null) {
                GPInputField(
                    modifier = Modifier.padding(top = 17.dp),
                    title = R.string.payment_amount,
                    value = screenModel.amount,
                    onValueChanged = vm::onAmountChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal),
                    visualTransformation = PaymentAmountVisualTransformation("£")
                )

                GPSwitch(
                    modifier = Modifier
                        .padding(top = 17.dp)
                        .fillMaxWidth(),
                    title = "Make payment recurring",
                    isChecked = screenModel.makePaymentRecurring,
                    onCheckedChanged = vm::onMakePaymentRecurring
                )

                GPInputField(
                    modifier = Modifier.padding(top = 17.dp),
                    title = R.string.card_number,
                    value = screenModel.cardNumber,
                    onValueChanged = vm::onCardNumberChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal),
                )

                Row(modifier = Modifier.padding(top = 10.dp)) {
                    GPSelectableField(
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .weight(1f),
                        title = stringResource(id = R.string.card_expiration_date),
                        textToDisplay = with(screenModel) { if (cardYear.isBlank() && cardMonth.isBlank()) "" else "${screenModel.cardMonth}/${screenModel.cardYear}" },
                        onButtonClick = {
                            showDatePicker(context = context, onDateSelected = vm::updateCardExpirationDate)
                        }
                    )

                    GPInputField(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .weight(1f),
                        title = R.string.card_cvv,
                        value = screenModel.cardCVV,
                        onValueChanged = vm::onCvvChanged,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal),
                    )
                }

                GPInputField(
                    modifier = Modifier.padding(top = 17.dp),
                    title = R.string.card_holder_name,
                    value = screenModel.cardHolderName,
                    onValueChanged = vm::onCardHolderName,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                )


                GPActionButton(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
                    title = "Charge",
                    onClick = vm::makePayment
                )
            } else {
                GPSampleResponse(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
                    model = responseModel
                )

                GPActionButton(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
                    title = "Reset",
                    onClick = vm::resetScreen
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
                        append("Find the code below in this files: sample/gpapi/screens/processpayment/unifiedpaymentsapi/")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("\nUnifiedPaymentAPIViewModel.kt")
                    }
                },
                codeSampleSnippet = R.drawable.snippet_unified_payments_api,
                model = screenModel.gpSnippetResponseModel
            )
        }
    }
}

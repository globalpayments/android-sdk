package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.verifications

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
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
import com.globalpayments.android.sdk.model.PaymentCardModel
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPDropdown
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPSwitch
import com.globalpayments.android.sdk.sample.utils.FingerprintMethodUsageMode

@Composable
fun VerificationsScreen(verificationsVM: VerificationsScreenViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.verifications),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color(0xFF003C71)
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

            val screenModel by verificationsVM.screenModel.collectAsState()

            GPDropdown(
                modifier = Modifier.padding(top = 12.dp),
                title = R.string.payment_card,
                selectedValue = screenModel.paymentCard,
                values = PaymentCardModel.entries,
                onValueSelected = verificationsVM::onPaymentCardSelected
            )


            GPInputField(
                modifier = Modifier.padding(top = 12.dp),
                title = R.string.card_number,
                value = screenModel.cardNumber,
                onValueChanged = verificationsVM::onCardNumberChanged,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
            )

            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            ) {
                GPInputField(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .weight(1f),
                    title = R.string.expiry_month,
                    value = screenModel.expiryMonth,
                    onValueChanged = verificationsVM::onExpiryMonthChanged,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                )
                GPInputField(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .weight(1f),
                    title = R.string.expiry_year,
                    value = screenModel.expiryYear,
                    onValueChanged = verificationsVM::onExpiryYearChanged,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            ) {
                GPInputField(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .weight(1f),
                    title = R.string.cvn_cvv,
                    value = screenModel.cvn,
                    onValueChanged = verificationsVM::onCvnChanged,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                )
                val currencies = stringArrayResource(id = R.array.currencies).toList()
                GPDropdown(
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .weight(1f),
                    title = R.string.currency,
                    selectedValue = screenModel.currency,
                    values = currencies,
                    onValueSelected = verificationsVM::onCurrencyChanged
                )
            }

            GPSwitch(
                modifier = Modifier.padding(top = 12.dp),
                title = stringResource(id = R.string.enable_fingerprint),
                isChecked = screenModel.fingerPrintEnabled,
                onCheckedChanged = verificationsVM::enableFingerprint
            )

            if (screenModel.fingerPrintEnabled) {
                GPDropdown(
                    modifier = Modifier
                        .padding(top = 12.dp),
                    title = R.string.finger_print_usage_mode,
                    selectedValue = screenModel.fingerPrintUsageMethod,
                    values = FingerprintMethodUsageMode.entries,
                    onValueSelected = verificationsVM::onFingerPrintUsageMethodChanged,
                    onEmptyClicked = { verificationsVM.onFingerPrintUsageMethodChanged(null) }
                )
            }

            GPInputField(
                modifier = Modifier
                    .padding(top = 12.dp),
                title = R.string.idempotency_key,
                value = screenModel.idempotencyKey,
                hint = stringResource(id = R.string.optional),
                onValueChanged = verificationsVM::onIdempotencyKey,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            GPSubmitButton(
                modifier = Modifier.padding(top = 12.dp),
                title = stringResource(id = R.string.verifications),
                onClick = verificationsVM::onSubmitClicked
            )

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
                        append("Find the code below in this files: sample/gpapi/screens/verifications/")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("VerificationsScreenViewModel.kt")
                    }
                },

                codeSampleSnippet = R.drawable.snippet_verifications,
                model = screenModel.gpSnippetResponseModel
            )
        }
    }
}

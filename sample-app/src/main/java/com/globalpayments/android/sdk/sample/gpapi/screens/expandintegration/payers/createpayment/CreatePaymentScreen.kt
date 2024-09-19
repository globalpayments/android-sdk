package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.payers.createpayment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPDropdown
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton

@Composable
fun CreatePaymentScreen(createPaymentScreenViewModel: CreatePaymentScreenViewModel = viewModel()) {

    val columnSpacing = 3.dp
    val softInput = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val screenModel by createPaymentScreenViewModel.screenModel.collectAsState()
        val sampleModel = screenModel.sampleResponseModel

        if (sampleModel == null) {

            //region Customer Details
            Text(
                modifier = Modifier.padding(top = 17.dp),
                text = stringResource(id = R.string.customer_details),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = Color(0xFF003C71),
                fontWeight = FontWeight.Medium
            )

            GPInputField(
                modifier = Modifier.padding(top = 12.dp),
                title = "Id",
                value = screenModel.customerId,
                onValueChanged = createPaymentScreenViewModel::onIdChanged,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )

            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
            ) {
                GPInputField(
                    modifier = Modifier
                        .padding(end = columnSpacing)
                        .weight(1f),
                    title = "First Name",
                    value = screenModel.customerFirstName,
                    onValueChanged = createPaymentScreenViewModel::onFirstNameChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                GPInputField(
                    modifier = Modifier
                        .padding(start = columnSpacing)
                        .weight(1f),
                    title = "Last Name",
                    value = screenModel.customerLastName,
                    onValueChanged = createPaymentScreenViewModel::onLastNameChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }
            //end region

            //region Payment Card
            Text(
                modifier = Modifier.padding(top = 17.dp),
                text = stringResource(id = R.string.header_payment_method),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = Color(0xFF003C71),
                fontWeight = FontWeight.Medium
            )

            GPDropdown(
                modifier = Modifier.padding(top = 12.dp),
                title = R.string.payment_card,
                selectedValue = screenModel.paymentCard,
                values = PaymentCardModel.entries,
                onValueSelected = createPaymentScreenViewModel::onPaymentCardSelected
            )


            GPInputField(
                modifier = Modifier.padding(top = 12.dp),
                title = R.string.card_number,
                value = screenModel.cardNumber,
                onValueChanged = createPaymentScreenViewModel::onCardNumberChanged,
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
                    onValueChanged = createPaymentScreenViewModel::onExpiryMonthChanged,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                )
                GPInputField(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .weight(1f),
                    title = R.string.expiry_year,
                    value = screenModel.expiryYear,
                    onValueChanged = createPaymentScreenViewModel::onExpiryYearChanged,
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
                    onValueChanged = createPaymentScreenViewModel::onCvnChanged,
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
                    onValueSelected = createPaymentScreenViewModel::onCurrencyChanged
                )
            }
            //end region

            GPSubmitButton(
                modifier = Modifier.padding(top = 12.dp),
                title = "Create Payment",
                onClick = {
                    softInput?.hide()
                    createPaymentScreenViewModel.createPayment()
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
                onClick = createPaymentScreenViewModel::resetScreen
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
                    append("Find the code below in this files: sample/gpapi/screens/expandintegration/merchants/add/")
                }
                withStyle(
                    SpanStyle(
                        color = Color(0xFF5A5E6D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("CreatePayerScreenViewModel.kt")
                }
            },
            codeSampleSnippet = R.drawable.snippet_funds_add,
            model = screenModel.gpSnippetResponseModel
        )
    }
}

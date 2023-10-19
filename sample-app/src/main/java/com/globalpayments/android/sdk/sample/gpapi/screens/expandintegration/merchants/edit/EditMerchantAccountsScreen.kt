package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.merchants.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPAddressDialog
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSelectableField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton
import com.globalpayments.android.sdk.sample.gpapi.utils.DateFormatter
import com.globalpayments.android.sdk.sample.gpapi.utils.showDatePicker

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditMerchantAccountsScreen(
    accountsVm: EditMerchantAccountsScreenViewModel = viewModel()
) {

    val screenModel by accountsVm.screenModel.collectAsState()
    val softInput = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val sampleModel = screenModel.sampleResponseModel
        if (sampleModel == null) {

            GPInputField(
                modifier = Modifier.padding(top = 12.dp),
                title = R.string.card_number,
                value = screenModel.cardNumber,
                onValueChanged = accountsVm::onCardNumberChanged,
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
                    onValueChanged = accountsVm::onExpiryMonthChanged,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                )
                GPInputField(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .weight(1f),
                    title = R.string.expiry_year,
                    value = screenModel.expiryYear,
                    onValueChanged = accountsVm::onExpiryYearChanged,
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
                    onValueChanged = accountsVm::onCvnChanged,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                )
                GPInputField(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .weight(1f),
                    title = R.string.card_holder_name,
                    value = screenModel.cardHolderName,
                    onValueChanged = accountsVm::onCardHolderNameChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
            ) {
                val columnSpacing = 3.dp

                GPSelectableField(
                    modifier = Modifier
                        .padding(end = columnSpacing)
                        .weight(1f),
                    title = stringResource(id = R.string.from_time_created),
                    textToDisplay = screenModel.fromTimeCreated?.let { DateFormatter.format(it) } ?: "",
                    onButtonClick = {
                        showDatePicker(context = context, onDateSelected = accountsVm::updateFromTimeCreated)
                    }
                )
                GPSelectableField(
                    modifier = Modifier
                        .padding(start = columnSpacing)
                        .weight(1f),
                    title = stringResource(id = R.string.to_time_created),
                    textToDisplay = screenModel.toTimeCreated?.let { DateFormatter.format(it) } ?: "",
                    onButtonClick = {
                        showDatePicker(context = context, onDateSelected = accountsVm::updateToTimeCreated)
                    }
                )
            }

            GPSelectableField(
                modifier = Modifier
                    .padding(top = 17.dp),
                title = "Billing Address",
                textToDisplay = screenModel.billingAddress.streetAddress1.take(10),
                onButtonClick = { accountsVm.billingAddressDialogVisibility(true) }
            )

            GPSubmitButton(
                modifier = Modifier.padding(top = 12.dp),
                title = "Edit Account",
                onClick = {
                    softInput?.hide()
                    accountsVm.editAccount()
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
                onClick = accountsVm::resetScreen
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
                    append("Find the code below in this files: sample/gpapi/screens/expandintegration/merchants/edit/")
                }
                withStyle(
                    SpanStyle(
                        color = Color(0xFF5A5E6D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("EditMerchantAccountsScreenViewModel.kt")
                }
            },
            codeSampleSnippet = R.drawable.snippet_edit_account,
            model = screenModel.gpSnippetResponseModel
        )
    }

    if (screenModel.showBillingAddressDialog) {
        GPAddressDialog(
            address = screenModel.billingAddress,
            onDismissClicked = { accountsVm.billingAddressDialogVisibility(false) },
            onAddressCreated = accountsVm::onBillingAddressChanged
        )
    }
}

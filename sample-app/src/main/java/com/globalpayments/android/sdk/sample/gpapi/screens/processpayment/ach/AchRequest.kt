package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.ach

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.global.api.entities.enums.AccountType
import com.global.api.entities.enums.SecCode
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPDropdown
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSelectableField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPSwitch
import com.globalpayments.android.sdk.sample.gpapi.utils.PaymentAmountVisualTransformation
import com.globalpayments.android.sdk.sample.gpapi.utils.showDatePicker

@Composable
fun AchRequest(
    achViewModel: AchViewModel
) {

    val columnSpacing = 3.dp
    val screenModel by achViewModel.screenModel.collectAsState()
    val context = LocalContext.current

    GPInputField(
        modifier = Modifier.padding(top = 17.dp),
        title = R.string.payment_amount,
        value = screenModel.amount,
        onValueChanged = achViewModel::onAmountChanged,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal),
        visualTransformation = PaymentAmountVisualTransformation("$")
    )

    GPSwitch(
        modifier = Modifier.padding(top = 17.dp),
        title = "Split",
        isChecked = screenModel.splitTransaction,
        onCheckedChanged = achViewModel::onSplitCheckedChange
    )

    if (screenModel.splitTransaction) {

        GPInputField(
            modifier = Modifier.padding(top = 17.dp),
            title = "Split Amount",
            value = screenModel.splitAmount,
            onValueChanged = achViewModel::onSplitAmountChanged,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal),
            visualTransformation = PaymentAmountVisualTransformation("$")
        )

        GPInputField(
            modifier = Modifier.padding(top = 17.dp),
            title = "MerchantId for Split",
            value = screenModel.splitMerchantId,
            onValueChanged = achViewModel::onSplitMerchantIdChanged,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
    }

    //region BankDetails
    Text(
        modifier = Modifier.padding(top = 17.dp),
        text = stringResource(id = R.string.bank_details),
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        color = Color(0xFF003C71),
        fontWeight = FontWeight.Medium
    )


    GPInputField(
        modifier = Modifier.padding(top = 10.dp),
        title = R.string.account_holder_name,
        value = screenModel.accountHolderName,
        onValueChanged = achViewModel::onAccountHolderNameChanged,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    )

    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        GPDropdown(
            modifier = Modifier
                .padding(end = columnSpacing)
                .weight(1f),
            title = R.string.account_type,
            selectedValue = screenModel.accountType,
            values = AccountType.entries,
            onValueSelected = achViewModel::onAccountTypeChanged
        )
        GPDropdown(
            modifier = Modifier
                .padding(start = columnSpacing)
                .weight(1f),
            title = R.string.sec_code,
            selectedValue = screenModel.secCode,
            values = SecCode.entries,
            onValueSelected = achViewModel::onSecCodeChanged
        )
    }

    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        GPInputField(
            modifier = Modifier
                .padding(end = columnSpacing)
                .weight(1f),
            title = R.string.routing_number,
            value = screenModel.routingNumber,
            onValueChanged = achViewModel::updateRoutingNumber,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
        )
        GPInputField(
            modifier = Modifier
                .padding(start = columnSpacing)
                .weight(1f),
            title = R.string.account_number,
            value = screenModel.accountNumber,
            onValueChanged = achViewModel::updateAccountNumber,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
        )
    }
    //endregion

    //region Customer Details
    Text(
        modifier = Modifier.padding(top = 17.dp),
        text = stringResource(id = R.string.customer_details),
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        color = Color(0xFF003C71),
        fontWeight = FontWeight.Medium
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
            title = R.string.first_name,
            value = screenModel.customerFirstName,
            onValueChanged = achViewModel::updateCustomerFirstName,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        GPInputField(
            modifier = Modifier
                .padding(start = columnSpacing)
                .weight(1f),
            title = R.string.last_name,
            value = screenModel.customerLastName,
            onValueChanged = achViewModel::updateCustomerLastName,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
    }

    GPSelectableField(
        modifier = Modifier
            .padding(top = 10.dp),
        title = stringResource(id = R.string.date_of_birth),
        textToDisplay = screenModel.customerBirthDate ?: "",
        onButtonClick = {
            showDatePicker(context = context, onDateSelected = achViewModel::updateCustomerBirthDate)
        }
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
            title = R.string.mobile_phone,
            value = screenModel.customerMobilePhone,
            onValueChanged = achViewModel::updatePhoneNumber,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
        )
        GPInputField(
            modifier = Modifier
                .padding(start = columnSpacing)
                .weight(1f),
            title = R.string.home_phone,
            value = screenModel.customerHomePhone,
            onValueChanged = achViewModel::updateHomeNumber,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
        )
    }
    //endregion

    //region Billing Address
    Text(
        modifier = Modifier.padding(top = 17.dp),
        text = stringResource(id = R.string.billing_address),
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        color = Color(0xFF003C71),
        fontWeight = FontWeight.Medium
    )
    GPInputField(
        modifier = Modifier.padding(top = 10.dp),
        title = R.string.line1,
        value = screenModel.billingAddressLine1,
        onValueChanged = achViewModel::updateBillingAddressLine1,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
    GPInputField(
        modifier = Modifier.padding(top = 10.dp),
        title = R.string.line2,
        value = screenModel.billingAddressLine2,
        onValueChanged = achViewModel::updateBillingAddressLine2,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
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
            title = R.string.city,
            value = screenModel.billingAddressCity,
            onValueChanged = achViewModel::updateBillingCity,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        GPInputField(
            modifier = Modifier
                .padding(start = columnSpacing)
                .weight(1f),
            title = R.string.state,
            value = screenModel.billingAddressState,
            onValueChanged = achViewModel::updateBillingState,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
    }

    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        GPInputField(
            modifier = Modifier
                .padding(end = columnSpacing)
                .weight(1f),
            title = R.string.postal_code,
            value = screenModel.billingAddressPostalCode,
            onValueChanged = achViewModel::updateBillingPostalCode,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        GPInputField(
            modifier = Modifier
                .padding(start = columnSpacing)
                .weight(1f),
            title = R.string.country,
            value = screenModel.billingAddressCountry,
            onValueChanged = achViewModel::updateBillingAddressCountry,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
    }
    //endregion


    GPSubmitButton(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        title = "Charge",
        onClick = { achViewModel.makePayment(PaymentType.Charge) }
    )

    GPActionButton(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        title = "Refund",
        onClick = { achViewModel.makePayment(PaymentType.Refund) }
    )


}

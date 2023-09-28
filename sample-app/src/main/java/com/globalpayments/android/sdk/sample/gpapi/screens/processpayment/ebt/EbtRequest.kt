package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.ebt

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSelectableField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton
import com.globalpayments.android.sdk.sample.gpapi.utils.PaymentAmountVisualTransformation
import com.globalpayments.android.sdk.sample.gpapi.utils.showDatePicker

@Composable
fun EbtRequest(vm: EbtViewModel) {

    val context = LocalContext.current
    val screenModel by vm.screenModel.collectAsState()

    GPInputField(
        modifier = Modifier.padding(top = 17.dp),
        title = R.string.payment_amount,
        value = screenModel.amount,
        onValueChanged = vm::onAmountChanged,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal),
        visualTransformation = PaymentAmountVisualTransformation("$")
    )

    GPInputField(
        modifier = Modifier.padding(top = 17.dp),
        title = R.string.card_number,
        value = screenModel.cardNumber,
        onValueChanged = vm::onCardNumberChanged,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal),
    )

    GPSelectableField(
        modifier = Modifier
            .padding(top = 10.dp),
        title = stringResource(id = R.string.card_expiration_date),
        textToDisplay = with(screenModel) { if (cardYear.isBlank() && cardMonth.isBlank()) "" else "${screenModel.cardMonth}/${screenModel.cardYear}" },
        onButtonClick = {
            showDatePicker(context = context, onDateSelected = vm::updateCardExpirationDate)
        }
    )

    GPInputField(
        modifier = Modifier.padding(top = 17.dp),
        title = R.string.pin_block,
        value = screenModel.pinBlock,
        onValueChanged = vm::onPinBlockChanged,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    )

    GPInputField(
        modifier = Modifier.padding(top = 17.dp),
        title = R.string.card_holder_name,
        value = screenModel.cardHolderName,
        onValueChanged = vm::onCardHolderName,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    )


    GPSubmitButton(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        title = "Charge",
        onClick = { vm.makePayment(PaymentType.Charge) }
    )

    GPActionButton(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        title = "Refund",
        onClick = { vm.makePayment(PaymentType.Refund) }
    )

}

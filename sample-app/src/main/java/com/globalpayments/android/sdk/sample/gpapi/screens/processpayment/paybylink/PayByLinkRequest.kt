package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.paybylink

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.global.api.entities.enums.PaymentMethodUsageMode
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.components.GPDropdown
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSelectableField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton
import com.globalpayments.android.sdk.sample.gpapi.utils.DateFormatter
import com.globalpayments.android.sdk.sample.gpapi.utils.PaymentAmountVisualTransformation
import com.globalpayments.android.sdk.sample.gpapi.utils.showDatePicker
import java.util.Date


@Composable
fun PayByLinkRequest(
    amount: String,
    description: String,
    expirationDate: Date?,
    usageMode: PaymentMethodUsageMode,
    usageLimit: String,
    onAmountChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onExpirationDateChanged: (Date) -> Unit,
    onPaymentUsageModeChanged: (PaymentMethodUsageMode) -> Unit,
    onUsageLimitChanged: (String) -> Unit,
    onGetPayLinkRequested: () -> Unit
) {

    val context = LocalContext.current
    val columnSpacing = 3.dp

    GPInputField(
        modifier = Modifier.padding(top = 17.dp),
        title = R.string.payment_amount,
        value = amount,
        onValueChanged = onAmountChanged,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal),
        visualTransformation = PaymentAmountVisualTransformation("£")
    )

    GPInputField(
        modifier = Modifier.padding(top = 17.dp),
        title = "Description",
        value = description,
        onValueChanged = onDescriptionChanged,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    )

    GPSelectableField(
        modifier = Modifier
            .padding(top = 10.dp),
        title = "Expiration Date",
        textToDisplay = expirationDate?.let { DateFormatter.format(it) } ?: "",
        onButtonClick = {
            showDatePicker(context = context, onDateSelected = onExpirationDateChanged)
        }
    )

    Row(
        modifier = Modifier
            .padding(top = 17.dp)
            .fillMaxWidth()
    ) {

        GPDropdown(
            modifier = Modifier
                .padding(end = columnSpacing)
                .weight(1f),
            title = "Usage Mode",
            selectedValue = usageMode,
            values = PaymentMethodUsageMode.entries,
            onValueSelected = onPaymentUsageModeChanged
        )

        GPInputField(
            modifier = Modifier
                .padding(start = columnSpacing)
                .weight(1f),
            title = "Usage limit",
            value = usageLimit,
            onValueChanged = onUsageLimitChanged,
            enabled = usageMode == PaymentMethodUsageMode.MULTIPLE,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Decimal),
        )
    }

    GPSubmitButton(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        title = "Get Link to Pay",
        onClick = onGetPayLinkRequested
    )


}

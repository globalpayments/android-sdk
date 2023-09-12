package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.bnpl

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.global.api.entities.Customer
import com.global.api.entities.enums.BNPLType
import com.global.api.entities.enums.PhoneNumberType
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.components.GPDropdown
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSelectableField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton
import com.globalpayments.android.sdk.sample.gpapi.utils.PaymentAmountVisualTransformation

@Composable
fun ColumnScope.BNPLRequest(
    screenModel: BnplRequestModel,
    bnplViewModel: BnplViewModel
) {
    GPInputField(
        modifier = Modifier.padding(top = 17.dp),
        title = "Amount to authorize",
        value = screenModel.authorize,
        onValueChanged = bnplViewModel::onAmountChanged,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal),
        visualTransformation = PaymentAmountVisualTransformation("$")
    )

    GPDropdown(
        modifier = Modifier.padding(top = 17.dp),
        title = R.string.account_type,
        selectedValue = screenModel.bnplType,
        values = BNPLType.entries,
        onValueSelected = bnplViewModel::onBnplTypeChanged
    )

    GPSelectableField(
        modifier = Modifier
            .padding(top = 17.dp),
        title = "Products",
        textToDisplay = "${screenModel.products.size} products selected",
        onButtonClick = { bnplViewModel.productsDialogVisibility(true) }
    )

    GPSelectableField(
        modifier = Modifier
            .padding(top = 17.dp),
        title = "Billing Address",
        textToDisplay = screenModel.billingAddress.streetAddress1.take(10),
        onButtonClick = { bnplViewModel.billingAddressDialogVisibility(true) }
    )

    GPSelectableField(
        modifier = Modifier
            .padding(top = 17.dp),
        title = "Shipping address",
        textToDisplay = screenModel.shippingAddress.streetAddress1.take(10),
        onButtonClick = { bnplViewModel.shippingAddressDialogVisibility(true) }
    )

    ConstraintLayout(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .height(80.dp)
    ) {
        val (countryCodeRef, phoneNumberRef, phoneTypeRef) = createRefs()
        createHorizontalChain(countryCodeRef, phoneNumberRef, phoneTypeRef)
        GPInputField(
            modifier = Modifier.constrainAs(countryCodeRef) {
                width = Dimension.fillToConstraints
                centerVerticallyTo(parent)
                start.linkTo(parent.start)
                end.linkTo(phoneNumberRef.start)
                horizontalChainWeight = 0.20f
            },
            value = screenModel.phoneNumber.countryCode,
            onValueChanged = { bnplViewModel.countryCodeChanged(it) },
            title = "Country code",
        )

        GPInputField(
            modifier = Modifier
                .padding(start = 6.dp)
                .constrainAs(phoneNumberRef) {
                    width = Dimension.fillToConstraints
                    centerVerticallyTo(parent)
                    start.linkTo(countryCodeRef.end)
                    end.linkTo(phoneTypeRef.start)
                    horizontalChainWeight = 0.5f
                },
            value = screenModel.phoneNumber.number,
            onValueChanged = { bnplViewModel.phoneNumberChanged(it) },
            title = "Phone Number"
        )

        GPDropdown(
            modifier = Modifier
                .padding(start = 6.dp)
                .constrainAs(phoneTypeRef) {
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                    top.linkTo(phoneNumberRef.top)
                    bottom.linkTo(phoneNumberRef.bottom)
                    start.linkTo(phoneNumberRef.end)
                    end.linkTo(parent.end)
                    horizontalChainWeight = 0.30f
                },
            title = "Type",
            values = PhoneNumberType.entries,
            selectedValue = screenModel.phoneNumber.phoneNumberType,
            onValueSelected = bnplViewModel::phoneNumberTypeChanged
        )
    }

    GPSelectableField(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth(),
        title = "Customer",
        textToDisplay = screenModel.customerData.name,
        onButtonClick = { bnplViewModel.customerDataDialogVisibility(true) }
    )

    GPSubmitButton(
        modifier = Modifier
            .padding(top = 15.dp)
            .align(Alignment.CenterHorizontally),
        onClick = bnplViewModel::onBnplClicked,
        title = "Buy now pay later"
    )
}

private val Customer.name
    get() = "$firstName $lastName".takeIf { it.isNotBlank() } ?: ""

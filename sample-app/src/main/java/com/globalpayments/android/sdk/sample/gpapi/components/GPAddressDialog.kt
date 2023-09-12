package com.globalpayments.android.sdk.sample.gpapi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.global.api.entities.Address

@Composable
fun GPAddressDialog(
    address: Address,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    onDismissClicked: () -> Unit,
    onAddressCreated: (Address) -> Unit
) {

    Dialog(
        properties = properties,
        onDismissRequest = onDismissClicked
    ) {
        var addressState by remember { mutableStateOf(address.fromGpModel()) }

        Column(
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .background(Color.White)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GPInputField(modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
                title = "Street address 1",
                value = addressState.streetAddress1,
                onValueChanged = { addressState = addressState.copy(streetAddress1 = it) })

            GPInputField(modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
                value = addressState.streetAddress2,
                title = "Street address 2",
                onValueChanged = { addressState = addressState.copy(streetAddress2 = it) })
            GPInputField(modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
                value = addressState.streetAddress3,
                title = "Street address 3",
                onValueChanged = { addressState = addressState.copy(streetAddress3 = it) })
            GPInputField(modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
                value = addressState.city,
                title = "City",
                onValueChanged = { addressState = addressState.copy(city = it) })
            GPInputField(modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
                value = addressState.postalCode,
                title = "Postal code",
                onValueChanged = { addressState = addressState.copy(postalCode = it) })
            GPInputField(modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
                value = addressState.countryCode,
                title = "Country code",
                onValueChanged = { addressState = addressState.copy(countryCode = it) })
            GPInputField(modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
                value = addressState.state,
                title = "State",
                onValueChanged = { addressState = addressState.copy(state = it) })
            GPActionButton(
                modifier = Modifier.padding(vertical = 10.dp),
                onClick = { onAddressCreated(addressState.asGpModel()) },
                title = "Save address"
            )
        }
    }
}

data class AddressViewDataModel(
    val streetAddress1: String = "",
    val streetAddress2: String = "",
    val streetAddress3: String = "",
    val city: String = "",
    val postalCode: String = "",
    val countryCode: String = "",
    val state: String = "",
)

fun AddressViewDataModel.asGpModel(): Address {
    return Address().apply {
        streetAddress1 = this@asGpModel.streetAddress1
        streetAddress2 = this@asGpModel.streetAddress2
        streetAddress3 = this@asGpModel.streetAddress3
        city = this@asGpModel.city
        postalCode = this@asGpModel.postalCode
        countryCode = this@asGpModel.countryCode
        state = this@asGpModel.state
    }
}

fun Address.fromGpModel(): AddressViewDataModel {
    return AddressViewDataModel(
        streetAddress1 = this@fromGpModel.streetAddress1,
        streetAddress2 = this@fromGpModel.streetAddress2,
        streetAddress3 = this@fromGpModel.streetAddress3,
        city = this@fromGpModel.city,
        postalCode = this@fromGpModel.postalCode,
        countryCode = this@fromGpModel.countryCode,
        state = this@fromGpModel.state
    )
}

@Preview
@Composable
fun AddressViewPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        GPAddressDialog(address = Address(), onAddressCreated = {}, onDismissClicked = {})
    }
}

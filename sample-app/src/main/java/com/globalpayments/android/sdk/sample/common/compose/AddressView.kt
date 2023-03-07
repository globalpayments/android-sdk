package com.globalpayments.android.sdk.sample.common.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.global.api.entities.Address

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

@SuppressLint("UnrememberedMutableState")
@Composable
fun AddressView(
    modifier: Modifier = Modifier, onAddressCreated: (Address) -> Unit
) {

    var addressState by remember { mutableStateOf(AddressViewDataModel()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
            label = { Text(text = "Street address 1") },
            value = addressState.streetAddress1,
            onValueChange = { addressState = addressState.copy(streetAddress1 = it) })
        OutlinedTextField(modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
            value = addressState.streetAddress2,
            label = { Text(text = "Street address 2") },
            onValueChange = { addressState = addressState.copy(streetAddress2 = it) })
        OutlinedTextField(modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
            value = addressState.streetAddress3,
            label = { Text(text = "Street address 3") },
            onValueChange = { addressState = addressState.copy(streetAddress3 = it) })
        OutlinedTextField(modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
            value = addressState.city,
            label = { Text(text = "City") },
            onValueChange = { addressState = addressState.copy(city = it) })
        OutlinedTextField(modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
            value = addressState.postalCode,
            label = { Text(text = "Postal code") },
            onValueChange = { addressState = addressState.copy(postalCode = it) })
        OutlinedTextField(modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
            value = addressState.countryCode,
            label = { Text(text = "Country code") },
            onValueChange = { addressState = addressState.copy(countryCode = it) })
        OutlinedTextField(modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
            value = addressState.state,
            label = { Text(text = "State") },
            onValueChange = { addressState = addressState.copy(state = it) })
        Button(modifier = Modifier.padding(top = 10.dp), onClick = { onAddressCreated(addressState.asGpModel()) }) {
            Text(text = "Save address")
        }
    }
}

@Preview
@Composable
fun AddressViewPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AddressView(onAddressCreated = {})
    }
}

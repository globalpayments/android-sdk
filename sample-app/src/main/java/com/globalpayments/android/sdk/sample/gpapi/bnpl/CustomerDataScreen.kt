package com.globalpayments.android.sdk.sample.gpapi.bnpl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.global.api.entities.Customer
import com.global.api.entities.CustomerDocument
import com.global.api.entities.PhoneNumber
import com.global.api.entities.enums.CustomerDocumentType
import com.global.api.entities.enums.PhoneNumberType
import com.globalpayments.android.sdk.sample.common.compose.DropdownView

@Composable
fun CustomerDataScreen(onCustomerCreate: (Customer) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {

        var customer by remember { mutableStateOf(CustomerDataScreenModel()) }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = customer.firstName, onValueChange = { customer = customer.copy(firstName = it) },
            label = { Text(text = "First Name") }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = customer.lastName, onValueChange = { customer = customer.copy(lastName = it) },
            label = { Text(text = "Last Name") }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = customer.email, onValueChange = { customer = customer.copy(email = it) },
            label = { Text(text = "Email") }
        )


        Text(
            modifier = Modifier.padding(top = 15.dp),
            text = "Customer phone number",
        )

        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (countryCodeRef, phoneNumberRef, phoneTypeRef) = createRefs()
            createHorizontalChain(countryCodeRef, phoneNumberRef, phoneTypeRef)
            OutlinedTextField(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .constrainAs(countryCodeRef) {
                        width = Dimension.fillToConstraints
                        centerVerticallyTo(parent)
                        start.linkTo(parent.start)
                        end.linkTo(phoneNumberRef.start)
                        horizontalChainWeight = 0.25f
                    },
                value = customer.countryCode,
                onValueChange = { customer = customer.copy(countryCode = it) },
                label = { Text(text = "Country code", fontSize = 10.sp) }
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .constrainAs(phoneNumberRef) {
                        width = Dimension.fillToConstraints
                        centerVerticallyTo(parent)
                        start.linkTo(countryCodeRef.end, 6.dp)
                        end.linkTo(phoneTypeRef.start)
                        horizontalChainWeight = 0.5f
                    },
                value = customer.phoneNumber,
                onValueChange = { customer = customer.copy(phoneNumber = it) },
                label = { Text(text = "Number", fontSize = 12.sp) }
            )

            DropdownView(
                modifier = Modifier.constrainAs(phoneTypeRef) {
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                    top.linkTo(phoneNumberRef.top, 8.dp)
                    bottom.linkTo(phoneNumberRef.bottom)
                    start.linkTo(phoneNumberRef.end, 6.dp)
                    end.linkTo(parent.end)
                    horizontalChainWeight = 0.25f
                },
                items = PhoneNumberType.values(),
                selectedItem = customer.phoneType
            ) {
                customer = customer.copy(phoneType = it)
            }
        }

        Text(
            modifier = Modifier.padding(top = 15.dp),
            text = "Customer Document",
        )

        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (docRef, issuerRef, typeRef) = createRefs()
            createHorizontalChain(docRef, issuerRef, typeRef)
            OutlinedTextField(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .constrainAs(docRef) {
                        width = Dimension.fillToConstraints
                        centerVerticallyTo(parent)
                        start.linkTo(parent.start)
                        end.linkTo(issuerRef.start)
                        horizontalChainWeight = 0.25f
                    },
                value = customer.issuer,
                onValueChange = { customer = customer.copy(issuer = it) },
                label = { Text(text = "Issuer", fontSize = 10.sp) }
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .constrainAs(issuerRef) {
                        width = Dimension.fillToConstraints
                        centerVerticallyTo(parent)
                        start.linkTo(docRef.end, 6.dp)
                        end.linkTo(typeRef.start)
                        horizontalChainWeight = 0.5f
                    },
                value = customer.docRef,
                onValueChange = { customer = customer.copy(docRef = it) },
                label = { Text(text = "Document reference", fontSize = 12.sp) }
            )

            DropdownView(
                modifier = Modifier.constrainAs(typeRef) {
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                    top.linkTo(issuerRef.top, 8.dp)
                    bottom.linkTo(issuerRef.bottom)
                    start.linkTo(issuerRef.end, 6.dp)
                    end.linkTo(parent.end)
                    horizontalChainWeight = 0.25f
                },
                items = CustomerDocumentType.values(),
                selectedItem = customer.docType
            ) {
                customer = customer.copy(docType = it)
            }
        }


        Button(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(CenterHorizontally),
            onClick = {
                onCustomerCreate(customer.asGpModel())
            }
        ) {
            Text(text = "Create customer")
        }
    }
}

data class CustomerDataScreenModel(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val countryCode: String = "",
    val phoneNumber: String = "",
    val phoneType: PhoneNumberType = PhoneNumberType.Home,
    val issuer: String = "",
    val docRef: String = "",
    val docType: CustomerDocumentType = CustomerDocumentType.NATIONAL
)

fun CustomerDataScreenModel.asGpModel(): Customer {
    return Customer().apply GPModel@{
        this.firstName = this@asGpModel.firstName
        this.lastName = this@asGpModel.lastName
        this.email = this@asGpModel.email
        this.phone = PhoneNumber().apply {
            this.countryCode = this@asGpModel.countryCode
            this.number = this@asGpModel.phoneNumber
            this.areaCode = this@asGpModel.phoneType.toString()
        }
        this.documents = listOf(
            CustomerDocument().apply {
                this.issuer = this@asGpModel.issuer
                this.reference = this@asGpModel.docRef
                this.type = this@asGpModel.docType
            }
        )
    }
}

@Preview
@Composable
fun CustomerDataScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CustomerDataScreen(onCustomerCreate = {})
    }
}

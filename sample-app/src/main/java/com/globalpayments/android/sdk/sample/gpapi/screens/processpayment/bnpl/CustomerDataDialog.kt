package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.bnpl

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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.global.api.entities.Customer
import com.global.api.entities.CustomerDocument
import com.global.api.entities.PhoneNumber
import com.global.api.entities.enums.CustomerDocumentType
import com.global.api.entities.enums.PhoneNumberType
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPDropdown
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField

@Composable
fun CustomerDataDialog(
    dialogProperties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    onCustomerCreate: (Customer) -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = dialogProperties
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .background(Color.White)
                .padding(horizontal = 10.dp)
        ) {

            var customer by remember { mutableStateOf(CustomerDataScreenModel()) }

            GPInputField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                value = customer.firstName,
                onValueChanged = { customer = customer.copy(firstName = it) },
                title = "First Name"
            )

            GPInputField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                value = customer.lastName,
                onValueChanged = { customer = customer.copy(lastName = it) },
                title = "Last Name"
            )

            GPInputField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                value = customer.email,
                onValueChanged = { customer = customer.copy(email = it) },
                title = "Email"
            )

            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (countryCodeRef, phoneNumberRef, phoneTypeRef) = createRefs()
                createHorizontalChain(countryCodeRef, phoneNumberRef, phoneTypeRef)
                GPInputField(
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
                    onValueChanged = { customer = customer.copy(countryCode = it) },
                    title = "Country code"
                )

                GPInputField(
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
                    onValueChanged = { customer = customer.copy(phoneNumber = it) },
                    title = "Number"
                )

                GPDropdown(
                    modifier = Modifier.constrainAs(phoneTypeRef) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                        top.linkTo(phoneNumberRef.top)
                        bottom.linkTo(phoneNumberRef.bottom)
                        start.linkTo(phoneNumberRef.end, 6.dp)
                        end.linkTo(parent.end)
                        horizontalChainWeight = 0.25f
                    },
                    title = "PhoneType",
                    values = PhoneNumberType.values().toList(),
                    selectedValue = customer.phoneType,
                    onValueSelected = { customer = customer.copy(phoneType = it) }
                )
            }

            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (docRef, issuerRef, typeRef) = createRefs()
                createHorizontalChain(docRef, issuerRef, typeRef)
                GPInputField(
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
                    onValueChanged = { customer = customer.copy(issuer = it) },
                    title = "Issuer"
                )

                GPInputField(
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
                    onValueChanged = { customer = customer.copy(docRef = it) },
                    title = "Document reference"
                )

                GPDropdown(
                    modifier = Modifier.constrainAs(typeRef) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                        top.linkTo(issuerRef.top)
                        bottom.linkTo(issuerRef.bottom)
                        start.linkTo(issuerRef.end, 6.dp)
                        end.linkTo(parent.end)
                        horizontalChainWeight = 0.25f
                    },
                    values = CustomerDocumentType.values().toList(),
                    selectedValue = customer.docType,
                    onValueSelected = { customer = customer.copy(docType = it) },
                    title = "Customer document type"
                )
            }


            GPActionButton(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 10.dp)
                    .align(CenterHorizontally),
                title = "Create customer",
                onClick = {
                    onCustomerCreate(customer.asGpModel())
                }
            )
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
        CustomerDataDialog(onCustomerCreate = {})
    }
}

package com.globalpayments.android.sdk.sample.gpapi.bnpl

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.global.api.entities.Customer
import com.global.api.entities.enums.BNPLType
import com.global.api.entities.enums.PhoneNumberType
import com.globalpayments.android.sdk.sample.common.compose.DropdownView
import com.globalpayments.android.sdk.sample.common.compose.TransactionSuccessDialog

@Composable
fun BnplGeneralScreen(bnplViewModel: BnplViewModel) {

    val generalModel by bnplViewModel.screenModel.collectAsState()
    val screenModel = generalModel.bnplGeneralScreenModel

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {

        Text(modifier = Modifier.padding(top = 32.dp), text = "Information")

        OutlinedTextField(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            value = screenModel.authorize,
            onValueChange = bnplViewModel::onAmountChanged,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(text = "Amount to authorize") },
            isError = screenModel.amountError
        )

        DropdownView(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .height(64.dp),
            items = BNPLType.values(),
            selectedItem = screenModel.bnplType
        ) {
            bnplViewModel.onBnplTypeChanged(it)
        }

        Box(
            modifier = Modifier
                .padding(top = 15.dp)
                .fillMaxWidth()
                .height(64.dp),
            contentAlignment = Center
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxSize(),
                value = "${screenModel.products.size} products selected",
                onValueChange = {},
                readOnly = true,
                maxLines = 1,
                isError = screenModel.productsError
            )

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { bnplViewModel.goToScreen(ScreenState.Products) },
                color = Color.Transparent,
            ) {}
        }

        Box(
            modifier = Modifier
                .padding(top = 15.dp)
                .fillMaxWidth()
                .height(64.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxSize(),
                label = { Text("Billing Address") },
                value = screenModel.billingAddress.streetAddress1.take(10),
                onValueChange = {},
                enabled = false,
                maxLines = 1,
            )

            Surface(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { bnplViewModel.goToScreen(ScreenState.BillingAddress) },
                color = Color.Transparent,
            ) {}
        }

        Box(
            modifier = Modifier
                .padding(top = 15.dp)
                .fillMaxWidth()
                .height(64.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxSize(),
                label = { Text(text = "Shipping address") },
                value = screenModel.shippingAddress.streetAddress1.take(10),
                onValueChange = {},
                enabled = false,
                maxLines = 1,
            )

            Surface(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { bnplViewModel.goToScreen(ScreenState.ShippingAddress) },
                color = Color.Transparent,
            ) {}
        }

        ConstraintLayout(
            modifier = Modifier
                .padding(top = 15.dp)
                .fillMaxWidth()
                .height(64.dp)
        ) {
            val (countryCodeRef, phoneNumberRef, phoneTypeRef) = createRefs()
            createHorizontalChain(countryCodeRef, phoneNumberRef, phoneTypeRef)
            OutlinedTextField(
                modifier = Modifier.constrainAs(countryCodeRef) {
                    width = Dimension.fillToConstraints
                    centerVerticallyTo(parent)
                    start.linkTo(parent.start)
                    end.linkTo(phoneNumberRef.start)
                    horizontalChainWeight = 0.25f
                },
                value = screenModel.phoneNumber.countryCode,
                onValueChange = { bnplViewModel.countryCodeChanged(it) },
                label = { Text(text = "Country code", fontSize = 10.sp) },
                maxLines = 1,
            )

            OutlinedTextField(
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
                onValueChange = { bnplViewModel.phoneNumberChanged(it) },
                label = { Text(text = "Phone Number", fontSize = 12.sp) },
                maxLines = 1,
            )

            DropdownView(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .constrainAs(phoneTypeRef) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                        top.linkTo(phoneNumberRef.top, 8.dp)
                        bottom.linkTo(phoneNumberRef.bottom)
                        start.linkTo(phoneNumberRef.end)
                        end.linkTo(parent.end)
                        horizontalChainWeight = 0.25f
                    },
                items = PhoneNumberType.values(),
                selectedItem = screenModel.phoneNumber.phoneNumberType
            ) {
                bnplViewModel.phoneNumberTypeChanged(it)
            }
        }

        Box(
            modifier = Modifier
                .padding(top = 15.dp)
                .fillMaxWidth()
                .height(64.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxSize(),
                label = { Text(text = "Customer") },
                value = screenModel.customerData.name,
                onValueChange = {},
                enabled = false,
            )
            Surface(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { bnplViewModel.goToScreen(ScreenState.CustomerData) },
                color = Color.Transparent,
            ) {}
        }

        Button(
            modifier = Modifier
                .padding(top = 15.dp)
                .align(CenterHorizontally), onClick = bnplViewModel::onBnplClicked
        ) {
            Text(text = "Buy now pay later")
        }
    }

    val transaction = generalModel.transaction
    if (generalModel.showTransactionSuccessDialog && transaction != null) {
        TransactionSuccessDialog(
            transaction = transaction,
            onDismissRequest = { bnplViewModel.goToScreen(ScreenState.CaptureRefundReverse) })
    }
}

private val Customer.name
    get() = "$firstName $lastName".takeIf { it.isNotBlank() } ?: ""

@Preview
@Composable
fun BnplGeneralScreenPreview() {
    Box(modifier = Modifier.background(Color.White)) {
        BnplGeneralScreen(BnplViewModel())
    }
}




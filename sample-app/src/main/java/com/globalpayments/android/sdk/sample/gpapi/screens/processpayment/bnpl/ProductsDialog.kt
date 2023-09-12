package com.globalpayments.android.sdk.sample.gpapi.screens.processpayment.bnpl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import com.global.api.entities.Product
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPCheckbox
import java.math.BigDecimal
import java.util.UUID

private val products = generateProducts()

@Composable
fun ProductsDialog(
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    selectedProducts: List<Product>,
    onDismissRequest: () -> Unit = {},
    onProductsSelected: (List<Product>) -> Unit
) {

    Dialog(
        properties = properties,
        onDismissRequest = onDismissRequest
    ) {

        val selectedElements = remember { mutableStateListOf(*(selectedProducts.toTypedArray())) }

        ConstraintLayout(
            modifier = Modifier
                .padding(5.dp)
                .background(Color.White)
                .padding(10.dp)
        ) {
            val selectButtonRef = createRef()

            LazyColumn(
                modifier = Modifier.heightIn(max = 200.dp),
                contentPadding = PaddingValues(bottom = 64.dp, start = 10.dp, end = 10.dp)
            ) {
                items(products) { product ->
                    GPCheckbox(
                        modifier = Modifier.fillMaxWidth(),
                        title = product.productName,
                        checked = selectedElements.contains(product), onCheckChanged = {
                            if (selectedElements.contains(product)) {
                                selectedElements.remove(product)
                            } else {
                                selectedElements.add(product)
                            }
                        })
                }
            }

            GPActionButton(
                modifier = Modifier.constrainAs(selectButtonRef) {
                    bottom.linkTo(parent.bottom, 10.dp)
                    centerHorizontallyTo(parent)
                },
                onClick = { onProductsSelected(selectedElements.toList()) },
                title = "Select ${selectedElements.size} product"
            )
        }
    }
}

@Preview
@Composable
fun ProductsScreenPreview() {
    ProductsDialog(selectedProducts = mutableListOf(), onProductsSelected = {})
}

internal fun generateProducts(): List<Product> {
    return (0..10).map {
        Product().apply {
            productId = UUID.randomUUID().toString()
            productName = "iPhone 13"
            description = "iPhone 13"
            quantity = 1
            unitPrice = BigDecimal(550)
            taxAmount = BigDecimal(1)
            discountAmount = BigDecimal(0)
            taxPercentage = BigDecimal(0)
            netUnitAmount = BigDecimal(550)
            giftCardCurrency = Currency
            url = "https://www.example.com/iphone.html"
            imageUrl = "https://www.example.com/iphone.png"
        }
    }
}

private const val Currency: String = "USD"

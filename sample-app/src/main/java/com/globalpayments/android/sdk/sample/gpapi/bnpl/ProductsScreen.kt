package com.globalpayments.android.sdk.sample.gpapi.bnpl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.global.api.entities.Product
import java.math.BigDecimal
import java.util.*

private val products = generateProducts()

@Composable
fun ProductsScreen(modifier: Modifier = Modifier, selectedProducts: List<Product>, onProductsSelected: (List<Product>) -> Unit) {

    val selectedElements = remember { mutableStateListOf<Product>(*(selectedProducts.toTypedArray())) }

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val selectButtonRef = createRef()

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 64.dp, start = 10.dp, end = 10.dp)) {
            items(products) { product ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = selectedElements.contains(product), onCheckedChange = {
                        if (selectedElements.contains(product)) {
                            selectedElements.remove(product)
                        } else {
                            selectedElements.add(product)
                        }
                    })

                    Spacer(modifier = Modifier.weight(0.6f))
                    Text(text = product.productName)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = product.unitPrice.toString())
                }
            }
        }

        Button(
            modifier = Modifier.constrainAs(selectButtonRef) {
                bottom.linkTo(parent.bottom, 10.dp)
                centerHorizontallyTo(parent)
            },
            onClick = { onProductsSelected(selectedElements.toList()) }) {
            Text(text = "Select ${selectedElements.size} product")
        }
    }
}

@Preview
@Composable
fun ProductsScreenPreview() {
    ProductsScreen(selectedProducts = mutableListOf(), onProductsSelected = {})
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

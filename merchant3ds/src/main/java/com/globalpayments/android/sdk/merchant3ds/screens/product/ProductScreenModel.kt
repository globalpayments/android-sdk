package com.globalpayments.android.sdk.merchant3ds.screens.product

data class ProductScreenModel(
    val selectedSize: String = "L",
    val isLoading: Boolean = false,
    val error: String? = null
)

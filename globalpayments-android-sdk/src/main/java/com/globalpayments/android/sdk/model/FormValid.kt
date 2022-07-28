package com.globalpayments.android.sdk.model

data class FormValid(
    val numberValid: Boolean = false,
    val cvvValid: Boolean = false,
    val dateValid: Boolean = false,
    val holderValid: Boolean = false,
) {
    val cardValid: Boolean
        get() = numberValid && cvvValid && dateValid && holderValid
}
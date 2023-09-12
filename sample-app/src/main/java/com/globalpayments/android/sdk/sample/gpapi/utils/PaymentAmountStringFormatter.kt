package com.globalpayments.android.sdk.sample.gpapi.utils

fun String.formatAsPaymentAmount(): String {
    var wasDotEncountered = false
    return buildString {
        for (char in this@formatAsPaymentAmount) {
            if (char == ',') continue
            if (char == '.' && !wasDotEncountered && isNotBlank()) {
                append('.')
                wasDotEncountered = true
            }
            if (char.isDigit()) {
                append(char)
            }
        }
    }
}

package com.globalpayments.android.sdk.sample.gpapi.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PaymentAmountVisualTransformation(
    private val currency: String
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        var out = if (text.isNotBlank()) currency else ""
        for (i in text.indices) {
            out += text[i]
        }
        val amountOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return if (offset != 0) offset + 1 else 0
            }

            override fun transformedToOriginal(offset: Int): Int {
                return if (offset <= 1) 0 else offset - 1
            }

        }
        return TransformedText(AnnotatedString(out), amountOffsetTranslator)
    }
}

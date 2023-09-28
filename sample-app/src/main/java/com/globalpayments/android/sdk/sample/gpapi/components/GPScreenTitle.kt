package com.globalpayments.android.sdk.sample.gpapi.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun GPScreenTitle(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(
        modifier = modifier,
        text = title.uppercase(),
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        color = Color(0xFF003C71),
        fontWeight = FontWeight.Medium
    )
}

package com.globalpayments.android.sdk.sample.gpapi.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.globalpayments.android.sdk.sample.R

@Composable
fun GPWIPScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.wip),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color(0xFF003C71)
        )
    }
}

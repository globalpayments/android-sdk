package com.globalpayments.android.sdk.sample.gpapi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GPSelectableField(
    modifier: Modifier = Modifier,
    title: String,
    textToDisplay: String,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(80.dp)
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier,
            text = title,
            fontSize = 14.sp,
            color = Color(0xFF2E3038),
            maxLines = 1
        )

        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize()
                .border(width = 1.dp, color = Color(0xFFD2D8E1), shape = RoundedCornerShape(4.dp))
                .clickable { onButtonClick() },
            contentAlignment = Alignment.Center
        ) {
            AutoResizeText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                text = textToDisplay,
                fontSizeRange = FontSizeRange(5.sp, 15.sp),
                color = Color(0xFF3A3C44),
                maxLines = 1
            )

            Icon(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.CenterEnd),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
fun GPSelectableFieldPreview() {
    Box(
        modifier = Modifier
            .background(Color.White)
            .padding(10.dp)
    ) {
        GPSelectableField(title = "Title", textToDisplay = "Text to display") {

        }
    }
}

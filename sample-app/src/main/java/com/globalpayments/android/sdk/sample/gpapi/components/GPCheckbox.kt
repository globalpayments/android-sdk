package com.globalpayments.android.sdk.sample.gpapi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun GPCheckbox(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean,
    onCheckChanged: (Boolean) -> Unit
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        val checkBoxColor = Color(0xFF5A5E6D)
        Checkbox(
            modifier = Modifier,
            checked = checked,
            onCheckedChange = onCheckChanged,
            colors = CheckboxDefaults.colors(checkedColor = checkBoxColor, uncheckedColor = checkBoxColor, checkmarkColor = Color.White),
        )

        AutoResizeText(
            modifier = Modifier,
            text = title,
            color = Color(0xFF2E3038),
            fontSizeRange = FontSizeRange(min = 10.sp, max = 13.sp),
            maxLines = 1
        )
    }
}

@Preview
@Composable
fun GPCheckboxPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        GPCheckbox(title = "ACT_POST_MULTIPLE", checked = false, onCheckChanged = {})
    }
}

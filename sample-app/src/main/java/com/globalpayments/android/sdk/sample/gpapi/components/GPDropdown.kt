package com.globalpayments.android.sdk.sample.gpapi.components

import androidx.annotation.StringRes
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globalpayments.android.sdk.sample.R

@Composable
fun <T> GPDropdown(
    modifier: Modifier = Modifier,
    title: String,
    selectedValue: T,
    values: List<T>,
    onValueSelected: (T) -> Unit,
    onEmptyClicked: (() -> Unit)? = null
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

        var expanded by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize()
                .border(width = 1.dp, color = Color(0xFFD2D8E1), shape = RoundedCornerShape(4.dp))
                .clickable { expanded = !expanded },
            contentAlignment = Alignment.Center
        ) {
            AutoResizeText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 23.dp),
                text = selectedValue?.toString() ?: "",
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

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }) {
                values.forEach { value ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = value.toString(),
                                fontSize = 15.sp,
                                color = Color(0xFF3A3C44)
                            )
                        },
                        onClick = {
                            onValueSelected(value)
                            expanded = false
                        }
                    )
                }
                if (onEmptyClicked != null) {
                    DropdownMenuItem(text = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Clear",
                            fontSize = 15.sp,
                            color = Color(0xFF3A3C44)
                        )
                    }, onClick = {
                        onEmptyClicked()
                        expanded = false
                    })
                }
            }
        }
    }
}

@Composable
fun <T> GPDropdown(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    selectedValue: T,
    values: List<T>,
    onValueSelected: (T) -> Unit,
    onEmptyClicked: (() -> Unit)? = null
) {
    GPDropdown(
        modifier,
        stringResource(id = title),
        selectedValue,
        values,
        onValueSelected,
        onEmptyClicked
    )
}

@Preview
@Composable
fun GPDropdownPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        GPDropdown(
            modifier = Modifier.padding(horizontal = 25.dp),
            title = R.string.home_screen_title,
            selectedValue = "a",
            values = listOf("a", "b", "c", "d"),
            onValueSelected = {})
    }
}

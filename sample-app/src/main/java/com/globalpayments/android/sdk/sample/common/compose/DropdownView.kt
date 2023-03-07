package com.globalpayments.android.sdk.sample.common.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun <T> DropdownView(
    modifier: Modifier = Modifier,
    items: Array<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        OutlinedTextField(
            modifier = Modifier.fillMaxSize(), value = selectedItem?.toString() ?: "", onValueChange = {}, readOnly = true
        )
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium)
                .clickable { expanded = true },
            color = Color.Transparent,
        ) {}

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth()) {
            items.forEach { s ->
                DropdownMenuItem(onClick = {
                    onItemSelected(s)
                    expanded = false
                }) {
                    Text(
                        text = s.toString(), fontSize = 14.sp, color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun DropdownViewPreview() {
    DropdownView(items = arrayOf<String>(), onItemSelected = {}, selectedItem = null)
}

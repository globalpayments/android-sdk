@file:JvmName("GPResponseKt")

package com.globalpayments.android.sdk.sample.gpapi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GPResponse(
    modifier: Modifier = Modifier,
    isSuccess: Boolean,
    titleText: String,
    fields: List<Pair<String, String>>,
    maxFieldsToDisplay: Int = 4
) {
    val backgroundColor = if (isSuccess) Color(0XFF00804F) else Color(0XFFB31E14)

    Column(
        modifier = modifier.background(backgroundColor, RoundedCornerShape(4.dp))
    ) {

        Text(
            modifier = Modifier.padding(start = 14.dp, top = 7.dp),
            text = titleText,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Box(
            modifier = Modifier
                .padding(4.dp)
                .background(Color.White, RoundedCornerShape(4.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 7.dp)
                    .fillMaxWidth()
            ) {
                if (fields.size < maxFieldsToDisplay) {
                    FieldsDisplay(fieldsToDisplay = fields)
                } else {
                    var expanded by remember { mutableStateOf(false) }
                    if (expanded) {
                        FieldsDisplay(fieldsToDisplay = fields)
                    } else {
                        FieldsDisplay(fieldsToDisplay = fields.take(maxFieldsToDisplay))
                    }
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Center) {
                        Divider(modifier = Modifier.padding(vertical = 7.dp, horizontal = 14.dp), color = Color(0xFFD7DCE1))
                        Button(
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .border(1.dp, Color(0xFFD7DCE1), CircleShape)
                                .size(30.dp)
                                .align(Center),
                            contentPadding = PaddingValues(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF0074C7)),
                            onClick = { expanded = !expanded },
                        ) {
                            val icon = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
                            Icon(
                                imageVector = icon,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FieldsDisplay(fieldsToDisplay: List<Pair<String, String>>) {
    fieldsToDisplay.forEachIndexed { index, pair ->
        if (index != 0) {
            Divider(modifier = Modifier.padding(vertical = 7.dp, horizontal = 14.dp), color = Color(0xFFD7DCE1))
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier,
                text = pair.first,
                color = Color(0xFF5A5E6D),
                fontSize = 13.sp,
            )
            SelectionContainer {
                Text(
                    modifier = Modifier,
                    text = pair.second,
                    fontSize = 13.sp,
                    color = Color(0xFF2E3038)
                )
            }
        }
    }
}

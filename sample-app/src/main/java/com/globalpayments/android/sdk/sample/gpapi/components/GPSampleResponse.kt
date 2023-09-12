package com.globalpayments.android.sdk.sample.gpapi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GPSampleResponse(
    modifier: Modifier = Modifier,
    model: GPSampleResponseModel
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 15.dp)
                .fillMaxWidth()
                .border(1.dp, Color(0xFFD2D8E1), RoundedCornerShape(4.dp))
                .padding(vertical = 13.dp, horizontal = 14.dp)
        ) {

            SelectionContainer {
                Text(
                    text = model.transactionId,
                    fontSize = 13.sp,
                    color = Color(0xFF003C71),
                    fontWeight = FontWeight.Bold
                )
            }

            for ((name, field) in model.response) {
                Divider(modifier = Modifier.padding(top = 13.dp), color = Color(0xFFD2D8E1))
                Row(
                    modifier = Modifier
                        .padding(top = 13.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = name, modifier = Modifier.weight(1f), color = Color(0xFF5A5E6D), fontSize = 13.sp)
                    SelectionContainer { Text(text = field, modifier = Modifier.weight(1f), color = Color(0xFF2E3038), fontSize = 13.sp) }
                }
            }
        }
        Button(
            modifier = Modifier
                .border(1.dp, Color(0xFFD7DCE1), CircleShape)
                .size(30.dp)
                .align(Alignment.BottomCenter),
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF0074C7)),
            onClick = { },
        ) {
            val icon = Icons.Default.ArrowDropDown
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        }

    }
}

data class GPSampleResponseModel(
    val transactionId: String,
    val response: List<Pair<String, String>>
)

@Preview
@Composable
fun GPSampleResponsePreview() {
    GPSampleResponse(
        model = GPSampleResponseModel(
            transactionId = "TRN_7g3faeVD43hkwAQ44k5vgTzl4tb1Ep",
            response = listOf(
                "Time created" to "2020-11-02T14:31:44.160Z",
                "Status" to "CAPTURED",
                "Type" to "SALE"
            )
        )
    )

}

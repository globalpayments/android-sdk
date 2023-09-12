package com.globalpayments.android.sdk.sample.gpapi.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globalpayments.android.sdk.sample.R

@Composable
fun GPButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    title: String,
    description: String,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.elevatedButtonColors(containerColor = Color.White),
        elevation = ButtonDefaults.elevatedButtonElevation(2.dp)
    ) {

        if (icon != null) {
            Image(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(48.dp),
                painter = painterResource(id = icon),
                contentDescription = null
            )
        }

        Column(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .weight(1f)
                .align(CenterVertically)
        ) {

            Text(
                modifier = Modifier,
                text = title,
                fontSize = 16.sp,
                letterSpacing = 0.2.sp,
                color = Color(0xff2E3038),
                maxLines = 1
            )

            Text(
                modifier = Modifier,
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF5A5E6D),
                lineHeight = 14.sp,
                maxLines = 1
            )
        }

        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFF707689)
        )
    }
}

@Preview
@Composable
fun GPButtonPreview() {
    GPButton(
        title = "Home",
        description = "Create a Transaction, Hosted Fields, Digital Wallets, ACH, EBT",
        icon = R.drawable.ic_credit_sale
    )
}

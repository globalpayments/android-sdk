package com.globalpayments.android.sdk.sample.gpapi.openbanking.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.global.api.entities.TransactionSummary
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.compose.TransactionSuccessDialog

@Composable
fun ProcessingScreen(
    onBackPressed: () -> Unit,
    successTransaction: TransactionSummary,
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            },
            title = {
                Text(modifier = Modifier, text = "Processing")
            },
            backgroundColor = colorResource(id = R.color.colorAccent),
            contentColor = Color.White
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            modifier = Modifier.size(144.dp),
            imageVector = Icons.Default.Timer,
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = colorResource(id = R.color.colorAccent))
        )

        Text(
            modifier = Modifier.padding(top = 50.dp),
            text = "Thank you!",
            color = Color.Black,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            modifier = Modifier,
            text = "Your order is processing!",
            color = Color.Black,
            fontSize = 22.sp
        )

        Image(
            modifier = Modifier
                .padding(top = 25.dp)
                .size(200.dp),
            contentScale = ContentScale.FillBounds,
            painter = painterResource(id = R.drawable.gift),
            contentDescription = null
        )

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            modifier = Modifier.align(Alignment.Start),
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(contentColor = colorResource(id = R.color.colorAccent), backgroundColor = Color.Transparent)
        ) {
            Text(modifier = Modifier, text = "Show transaction")
        }
    }

    if (showDialog) {
        TransactionSuccessDialog(successTransaction, { showDialog = false })
    }
}

package com.globalpayments.android.sdk.sample.gpapi.openbanking.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.compose.TransactionErrorDialog

@Composable
fun ErrorScreen(
    onBackPressed: () -> Unit,
    errorMessage: String
) {

    var showErrorDialog by remember { mutableStateOf(false) }

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
                Text(modifier = Modifier, text = "Error :(")
            },
            backgroundColor = colorResource(id = R.color.colorAccent),
            contentColor = Color.White
        )

        Spacer(modifier = Modifier.weight(1f))
        Image(
            modifier = Modifier.size(150.dp),
            painter = painterResource(id = R.drawable.ic_sad),
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(top = 50.dp),
            text = "We're sorry...",
            color = Color.Black,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            modifier = Modifier.padding(horizontal = 25.dp),
            text = "Something went wrong while processing your transaction\n\nPlease try another card",
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 22.sp
        )
        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            modifier = Modifier.align(Alignment.Start),
            onClick = { showErrorDialog = true },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                backgroundColor = colorResource(id = R.color.colorAccent)
            )
        ) {
            Text(modifier = Modifier, text = "Show error message")
        }
    }

    if (showErrorDialog) {
        TransactionErrorDialog(errorMessage = errorMessage, onDismissRequest = { showErrorDialog = false })
    }
}

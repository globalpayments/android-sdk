package com.globalpayments.android.sdk.sample.gpapi.openbanking.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditScore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.openbanking.OpenBankingViewModel

@Composable
fun RedirectScreen(
    onBackPressed: () -> Unit,
    viewModel: OpenBankingViewModel
) {
    val uriHandler = LocalUriHandler.current

    val redirectUrl by viewModel.redirectUrl.collectAsState(initial = "")

    Column(
        modifier = Modifier.fillMaxSize(),
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
                Text(modifier = Modifier, text = "Pay")
            },
            backgroundColor = colorResource(id = R.color.colorAccent),
            contentColor = Color.White
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            modifier = Modifier.size(84.dp),
            imageVector = Icons.Default.CreditScore,
            contentDescription = null,
            colorFilter = ColorFilter.tint(colorResource(id = R.color.colorAccent))
        )

        Button(
            onClick = {
                if (redirectUrl.isNotBlank()) {
                    uriHandler.openUri(redirectUrl)
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.colorAccent), contentColor = Color.White)
        ) {
            Text(text = "Open url to pay")
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
fun RedirectScreenPreview() {
    RedirectScreen(viewModel = OpenBankingViewModel(), onBackPressed = {})
}

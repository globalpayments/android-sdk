package com.globalpatments.android.sdk.merchant3ds.screens.error

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.globalpatments.android.sdk.merchant3ds.R
import com.globalpatments.android.sdk.merchant3ds.navigation.NavigationManager
import com.globalpatments.android.sdk.merchant3ds.ui.theme.Background
import com.globalpatments.android.sdk.merchant3ds.ui.theme.Black
import com.globalpatments.android.sdk.merchant3ds.ui.theme.GlobalPaymentsAndroidSDKTheme

@Composable
fun ErrorScreen(viewModel: ErrorViewModel = hiltViewModel()) {

    Column(
        modifier = Modifier
            .background(color = Background)
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(12.dp),
            onClick = viewModel::navigateBack,
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, null, tint = Black)
        }

        Spacer(modifier = Modifier.weight(1f))
        Image(
            modifier = Modifier.size(150.dp),
            painter = painterResource(id = R.drawable.ic_sad),
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(top = 50.dp),
            text = "We're sorry...",
            color = Black,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            modifier = Modifier.padding(horizontal = 25.dp),
            text = "Something went wrong while processing your transaction\n\nPlease try another card",
            textAlign = TextAlign.Center,
            color = Black,
            fontSize = 22.sp
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
fun ErrorScreenPreview() {
    GlobalPaymentsAndroidSDKTheme {
        ErrorScreen(ErrorViewModel(NavigationManager()))
    }
}
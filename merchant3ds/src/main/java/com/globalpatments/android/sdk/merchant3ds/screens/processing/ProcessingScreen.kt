package com.globalpatments.android.sdk.merchant3ds.screens.processing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.globalpatments.android.sdk.merchant3ds.ui.findActivity
import com.globalpatments.android.sdk.merchant3ds.ui.theme.Background
import com.globalpatments.android.sdk.merchant3ds.ui.theme.Black

@Composable
fun ProcessingScreen(viewModel: ProcessingViewModel = hiltViewModel()) {

    val screenState = viewModel.state
    val context = LocalContext.current

    LaunchedEffect(screenState.transactionModel) {
        if (screenState.transactionModel.messageVersion.isNotBlank()) {
            viewModel.init3DS(context)
        }
    }

    LaunchedEffect(screenState.startChallenge) {
        if (!screenState.startChallenge) return@LaunchedEffect
        val activity = context.findActivity()
        viewModel.startChallenge(activity)
    }

    Column(
        modifier = Modifier
            .background(Background)
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        IconButton(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(12.dp),
            onClick = viewModel::goBack,
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, null, tint = Black)
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            modifier = Modifier.padding(top = 50.dp),
            text = stringResource(id = com.globalpatments.android.sdk.merchant3ds.R.string.processing),
            color = Black,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        CircularProgressIndicator(color = Black)

        Spacer(modifier = Modifier.weight(1f))
    }
}
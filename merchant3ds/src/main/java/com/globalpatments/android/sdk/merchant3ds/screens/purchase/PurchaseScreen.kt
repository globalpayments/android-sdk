package com.globalpatments.android.sdk.merchant3ds.screens.purchase

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
fun PurchaseScreen(viewModel: PurchaseViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier
            .background(Background)
            .systemBarsPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.weight(1f))

        Image(
            modifier = Modifier
                .clip(CircleShape)
                .size(144.dp)
                .background(Black),
            imageVector = Icons.Default.Check,
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = Color.White)
        )

        Text(
            modifier = Modifier.padding(top = 50.dp),
            text = "Thank you!",
            color = Black,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            modifier = Modifier,
            text = "Purchase was successful",
            color = Black,
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

        Button(
            modifier = Modifier
                .padding(top = 45.dp, bottom = 25.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Black),
            shape = RoundedCornerShape(24.dp),
            onClick = viewModel::donePressed
        ) {
            Text(
                text = "Done", color = Color.White, fontSize = 28.sp
            )
        }
    }
}

@Preview
@Composable
fun PurchaseScreenPreview() {
    GlobalPaymentsAndroidSDKTheme {
        PurchaseScreen(PurchaseViewModel(NavigationManager()))
    }
}
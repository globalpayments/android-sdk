package com.globalpayments.android.sdk.merchant3ds.screens.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.globalpayments.android.sdk.merchant3ds.ProductPrice
import com.globalpayments.android.sdk.merchant3ds.R
import com.globalpayments.android.sdk.merchant3ds.ui.theme.Background
import com.globalpayments.android.sdk.merchant3ds.ui.theme.Black

@Composable
fun ProductScreen(viewModel: ProductScreenViewModel = hiltViewModel()) {

    val screenState = viewModel.state

    Box(
        modifier = Modifier
            .background(color = Background)
            .fillMaxSize()
            .systemBarsPadding()
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.artwork_product),
                contentDescription = null
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = 20.dp),
                    text = "Product",
                    color = Black,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    modifier = Modifier.padding(end = 20.dp),
                    text = "$${ProductPrice}",
                    color = Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
                text = stringResource(id = R.string.placeholder),
                color = Black,
                fontSize = 18.sp,
                maxLines = 2
            )

            Row(
                modifier = Modifier
                    .padding(top = 15.dp)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(modifier = Modifier
                    .padding(10.dp)
                    .size(48.dp)
                    .clip(CircleShape),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = if (screenState.selectedSize == "M") Black else Color.White),
                    onClick = { viewModel.onSizeSelected("M") }) {
                    Text(
                        text = "M",
                        color = if (screenState.selectedSize == "M") Color.White else Black
                    )
                }
                TextButton(modifier = Modifier
                    .padding(10.dp)
                    .size(48.dp)
                    .clip(CircleShape),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = if (screenState.selectedSize == "L") Black else Color.White),
                    onClick = { viewModel.onSizeSelected("L") }) {
                    Text(
                        text = "L",
                        color = if (screenState.selectedSize == "L") Color.White else Black
                    )
                }
                TextButton(modifier = Modifier
                    .padding(10.dp)
                    .size(48.dp)
                    .clip(CircleShape),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = if (screenState.selectedSize == "XL") Black else Color.White),
                    onClick = { viewModel.onSizeSelected("XL") }) {
                    Text(
                        text = "XL",
                        color = if (screenState.selectedSize == "XL") Color.White else Black
                    )
                }
            }

            Button(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Black),
                shape = RoundedCornerShape(24.dp),
                onClick = viewModel::getAccessToken,
                enabled = !screenState.isLoading
            ) {
                Text(
                    text = "Buy now", color = Color.White, fontSize = 28.sp
                )
            }
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            onClick = viewModel::goBack,
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, null, tint = Color.White)
        }

        if (screenState.error != null) {
            AlertDialog(
                title = { Text(text = stringResource(id = R.string.error)) },
                text = { Text(text = screenState.error, color = Black) },
                onDismissRequest = viewModel::closeDialog,
                confirmButton = {
                    TextButton(onClick = viewModel::closeDialog) {
                        Text(text = stringResource(id = R.string.ok), color = Black)
                    }
                }
            )
        }
    }
}

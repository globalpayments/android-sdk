package com.globalpayments.android.sdk.sample.gpapi.openbanking.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.openbanking.FasterPaymentsAmount
import com.globalpayments.android.sdk.sample.gpapi.openbanking.SepaAmount

@Composable
fun ProductScreen(
    onBackPressed: () -> Unit,
    onFasterPaymentsClick: () -> Unit,
    onSepaClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            },
            title = {
                Text(modifier = Modifier, text = "Buy product")
            },
            backgroundColor = colorResource(id = R.color.colorAccent),
            contentColor = Color.White
        )

        Image(
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 10.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.4f),
            contentScale = ContentScale.Fit,
            painter = painterResource(id = R.drawable.artwork_bag),
            contentDescription = null
        )

        Text(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .fillMaxWidth(),
            text = "Product",
            color = Color.Black,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 15.dp),
            text = stringResource(id = R.string.placeholder),
            color = Color.Black,
            fontSize = 16.sp,
            maxLines = 5
        )

        Text(
            modifier = Modifier
                .padding(top = 20.dp)
                .align(CenterHorizontally),
            text = "Buy for",
            fontSize = 24.sp
        )

        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Button(
                modifier = Modifier.width(150.dp),
                onClick = onFasterPaymentsClick,
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.colorAccent), contentColor = Color.White)
            ) {
                Column(modifier = Modifier, horizontalAlignment = CenterHorizontally) {
                    Text(text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.White, fontSize = 20.sp)) {
                            append("£${FasterPaymentsAmount.substring(0 until FasterPaymentsAmount.indexOf("."))}")
                        }
                        withStyle(style = SpanStyle(color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)) {
                            append(FasterPaymentsAmount.substring(FasterPaymentsAmount.indexOf('.') until SepaAmount.length))
                        }
                    })

                    Text(text = "(FasterPayments)", fontSize = 10.sp)
                }
            }

            Text(modifier = Modifier.padding(10.dp), text = "OR")

            Button(
                modifier = Modifier.width(150.dp),
                onClick = onSepaClick,
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.colorAccent), contentColor = Color.White)
            ) {
                Column(modifier = Modifier, horizontalAlignment = CenterHorizontally) {
                    Text(text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.White, fontSize = 20.sp)) {
                            append("€${SepaAmount.substring(0 until SepaAmount.indexOf("."))}")
                        }
                        withStyle(style = SpanStyle(color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)) {
                            append(SepaAmount.substring(SepaAmount.indexOf('.') until SepaAmount.length))
                        }
                    })

                    Text(text = "(Sepa)", fontSize = 10.sp)
                }
            }
        }
    }
}

@Preview
@Composable
fun ProductScreenPreview() {
    ProductScreen(
        onBackPressed = {},
        onFasterPaymentsClick = {},
        onSepaClick = {}
    )
}

package com.globalpayments.android.sdk.sample.gpapi.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun GPCodeSnippet(
    modifier: Modifier = Modifier,
    @DrawableRes codeSnippet: Int,
) {
    Image(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp)),
        painter = painterResource(id = codeSnippet),
        contentDescription = null,
        contentScale = ContentScale.FillWidth
    )
}

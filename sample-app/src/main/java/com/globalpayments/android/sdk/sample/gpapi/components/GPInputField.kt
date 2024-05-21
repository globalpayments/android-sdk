package com.globalpayments.android.sdk.sample.gpapi.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globalpayments.android.sdk.sample.R

@Composable
fun GPInputField(
    modifier: Modifier = Modifier,
    title: String,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    value: String,
    hint: String = "",
    enabled: Boolean = true,
    isMandatory: Boolean = false,
    singleLine: Boolean = true,
    onValueChanged: (String) -> Unit
) {
    Column(
        modifier = modifier
            .height(80.dp)
            .fillMaxWidth()
    ) {
        AutoResizeText(
            modifier = Modifier,
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = Color(0xFF2E3038),
                        fontSize = 14.sp
                    )
                ) {
                    append(title)
                }
                if (isMandatory) {
                    withStyle(
                        SpanStyle(
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("*")
                    }
                }
            },
            fontSizeRange = FontSizeRange(8.sp, 14.sp),
            maxLines = 1
        )
        val borderColor = Color(0xFFD2D8E1)
        OutlinedTextField(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            value = value,
            onValueChange = onValueChanged,
            textStyle = TextStyle(
                fontSize = 15.sp,
                color = Color(0xFF3A3C44)
            ),
            placeholder = {
                if (hint.isNotBlank()) {
                    Text(hint, fontSize = 15.sp)
                }
            },
            enabled = enabled,
            shape = RoundedCornerShape(4.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = borderColor, unfocusedBorderColor = borderColor),
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            singleLine = singleLine
        )
    }
}

@Composable
fun GPInputField(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    value: String,
    hint: String = "",
    enabled: Boolean = true,
    isMandatory: Boolean = false,
    onValueChanged: (String) -> Unit
) {
    GPInputField(
        modifier = modifier,
        title = stringResource(id = title),
        value = value,
        onValueChanged = onValueChanged,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        hint = hint,
        enabled = enabled,
        isMandatory = isMandatory
    )
}

@Preview
@Composable
fun GPInputFieldPreview() {
    GPInputField(title = R.string.app_id, value = "", onValueChanged = {}, isMandatory = true)
}

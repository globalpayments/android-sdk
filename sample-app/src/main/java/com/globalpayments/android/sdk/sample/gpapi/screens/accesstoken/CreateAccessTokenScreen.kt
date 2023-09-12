package com.globalpayments.android.sdk.sample.gpapi.screens.accesstoken

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.global.api.entities.enums.Environment
import com.global.api.entities.enums.IntervalToExpire
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPCheckbox
import com.globalpayments.android.sdk.sample.gpapi.components.GPDropdown
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponse
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton

@Composable
fun CreateAccessTokenScreen(tokenViewModel: CreateAccessTokenViewModel = viewModel()) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.create_access_token),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color(0xFF003C71),
            fontWeight = FontWeight.Medium
        )

        Text(
            modifier = Modifier.padding(top = 11.dp),
            text = stringResource(id = R.string.create_access_token_long_description),
            fontSize = 14.sp,
            color = Color(0xFF5A5E6D)
        )

        Divider(
            modifier = Modifier
                .padding(top = 22.dp)
                .fillMaxWidth(),
            color = Color(0xFFD7DCE1)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            val screenModel by tokenViewModel.screenModel.collectAsState()

            GPInputField(
                modifier = Modifier.padding(top = 17.dp),
                title = R.string.app_id,
                value = screenModel.appId,
                onValueChanged = tokenViewModel::appIdChanged,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                isMandatory = true
            )
            GPInputField(
                modifier = Modifier.padding(top = 10.dp),
                title = R.string.app_key,
                value = screenModel.appKey,
                onValueChanged = tokenViewModel::appKeyChanged,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                isMandatory = true
            )
            GPInputField(
                modifier = Modifier.padding(top = 10.dp),
                title = R.string.seconds_to_expire,
                value = screenModel.secondsToExpire,
                onValueChanged = tokenViewModel::secondsToExpireChanged,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Decimal),
                isMandatory = true
            )

            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
            ) {
                GPDropdown(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .weight(1f),
                    title = R.string.environment,
                    selectedValue = screenModel.environment,
                    values = Environment.entries,
                    onValueSelected = tokenViewModel::environmentChanged,
                    onEmptyClicked = { tokenViewModel.environmentChanged(null) }
                )
                GPDropdown(
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .weight(1f),
                    title = R.string.interval_to_expire,
                    selectedValue = screenModel.intervalToExpire,
                    values = IntervalToExpire.entries,
                    onValueSelected = tokenViewModel::intervalToExpireChanged,
                    onEmptyClicked = { tokenViewModel.intervalToExpireChanged(null) }
                )
            }

            Column(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.permissions),
                    fontSize = 14.sp,
                    color = Color(0xFF2E3038),
                    maxLines = 1
                )

                for (permissions in CreateAccessTokenPermissions.entries.chunked(2)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (permission in permissions) {
                            GPCheckbox(
                                modifier = Modifier.weight(1f),
                                title = permission.name,
                                checked = screenModel.permissions.contains(permission.name),
                                onCheckChanged = { tokenViewModel.addOrRemovePermission(permission.name, it) }
                            )
                        }
                    }
                }
            }

            GPSubmitButton(
                modifier = Modifier.padding(top = 20.dp),
                title = stringResource(id = R.string.create_access_token),
                onClick = tokenViewModel::createAccessToken
            )

            Divider(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                color = Color(0xFFD7DCE1)
            )

            GPSnippetResponse(
                modifier = Modifier.padding(top = 20.dp),
                codeSampleLocation = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp
                        )
                    ) {
                        append("Find the code below in this file: sample/gpapi/screens/accesstoken/")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF5A5E6D),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("CreateAccessTokenViewModel.kt")
                    }
                },
                codeSampleSnippet = R.drawable.snippet_access_token,
                model = screenModel.responseModel
            )

        }
    }
}

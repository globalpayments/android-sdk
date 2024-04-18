package com.globalpayments.android.sdk.sample.gpapi.screens.config

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.global.api.entities.enums.Channel
import com.global.api.entities.enums.Environment
import com.global.api.entities.enums.IntervalToExpire
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.theme.Background
import com.globalpayments.android.sdk.sample.gpapi.components.GPDropdown
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPScreenTitle
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton

@Composable
fun ConfigScreen(vm: ConfigViewModel = viewModel()) {

    val screenModel by vm.screenModel.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GPScreenTitle(
            modifier = Modifier,
            title = stringResource(id = R.string.configuration)
        )

        Divider(
            modifier = Modifier
                .padding(top = 22.dp)
                .fillMaxWidth(),
            color = Color(0xFFD7DCE1)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp),
            ) {

                GPInputField(
                    modifier = Modifier.padding(top = 17.dp),
                    title = R.string.app_id,
                    value = screenModel.appId,
                    onValueChanged = vm::appIdChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                GPInputField(
                    modifier = Modifier.padding(top = 10.dp),
                    title = R.string.app_key,
                    value = screenModel.appKey,
                    onValueChanged = vm::appKeyChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                GPInputField(
                    modifier = Modifier.padding(top = 10.dp),
                    title = R.string.merchant_id,
                    value = screenModel.merchantId,
                    onValueChanged = vm::merchantIdChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                GPInputField(
                    modifier = Modifier.padding(top = 10.dp),
                    title = R.string.api_key,
                    value = screenModel.apiKey,
                    onValueChanged = vm::apiKeyChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                ) {
                    GPInputField(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .weight(1f),
                        title = "Transaction Processing Account Id",
                        value = screenModel.transactionProcessingAccountId,
                        onValueChanged = vm::transactionProcessingAccountIdChanged,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    GPInputField(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .weight(1f),
                        title = R.string.transaction_processing_account_name,
                        value = screenModel.transactionProcessingAccountName,
                        onValueChanged = vm::transactionProcessingAccountNameChanged,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                ) {
                    GPInputField(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .weight(1f),
                        title = "Tokenization Account Id",
                        value = screenModel.tokenizationAccountId,
                        onValueChanged = vm::tokenizationAccountIdChanged,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    GPInputField(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .weight(1f),
                        title = "Tokenization Account Name",
                        value = screenModel.tokenizationAccountName,
                        onValueChanged = vm::tokenizationAccountNameChanged,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                }

                GPInputField(
                    modifier = Modifier.padding(top = 10.dp),
                    title = "Risk Assessment Account Id",
                    value = screenModel.riskAssessmentAccountId,
                    onValueChanged = vm::riskAssessmentAccountIdChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                GPInputField(
                    modifier = Modifier.padding(top = 10.dp),
                    title = "Data Account Id",
                    value = screenModel.dataAccountId,
                    onValueChanged = vm::dataAccountIdChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                GPInputField(
                    modifier = Modifier.padding(top = 10.dp),
                    title = "Dispute Management Account Id",
                    value = screenModel.disputeManagementAccountId,
                    onValueChanged = vm::disputeManagementAccountIdChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                ) {

                    GPInputField(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .weight(1f),
                        title = R.string.seconds_to_expire,
                        value = screenModel.tokenSecondsToExpire,
                        onValueChanged = vm::secondsToExpireChanged,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal)
                    )
                    GPDropdown(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .weight(1f),
                        title = R.string.interval_to_expire,
                        selectedValue = screenModel.tokenIntervalToExpire,
                        values = IntervalToExpire.entries,
                        onValueSelected = vm::intervalToExpireChanged
                    )
                }

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
                        onValueSelected = vm::environmentChanged
                    )
                    GPDropdown(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .weight(1f),
                        title = R.string.channel,
                        selectedValue = screenModel.channel,
                        values = Channel.entries,
                        onValueSelected = vm::channelChanged
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                ) {
                    GPInputField(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .weight(1f),
                        title = R.string.country,
                        value = screenModel.country,
                        onValueChanged = vm::countryChanged,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    GPInputField(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .weight(1f),
                        title = R.string.api_version_configuration,
                        value = screenModel.apiVersion,
                        onValueChanged = vm::apiVersionChanged,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                }

                GPInputField(
                    modifier = Modifier.padding(top = 10.dp),
                    title = R.string.challenge_url_configuration,
                    value = screenModel.challengeNotificationUrl,
                    onValueChanged = vm::challengeNotificationUrlChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                GPInputField(
                    modifier = Modifier.padding(top = 10.dp),
                    title = R.string.method_url_configuration,
                    value = screenModel.methodNotificationUrl,
                    onValueChanged = vm::methodNotificationUrlChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                GPInputField(
                    modifier = Modifier.padding(top = 10.dp),
                    title = R.string.service_url_configuration,
                    value = screenModel.serviceUrl,
                    onValueChanged = vm::serviceUrlChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                GPInputField(
                    modifier = Modifier.padding(top = 10.dp),
                    title = "Status Url",
                    value = screenModel.statusUrl,
                    onValueChanged = vm::statusUrlChanged,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )

            }

            GPSubmitButton(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .align(Alignment.BottomCenter),
                title = stringResource(id = R.string.save_configuration),
                onClick = vm::saveConfiguration
            )
        }
    }
}

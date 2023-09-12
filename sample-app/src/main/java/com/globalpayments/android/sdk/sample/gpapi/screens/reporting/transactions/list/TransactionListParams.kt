package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.transactions.list

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.global.api.entities.enums.Channel
import com.global.api.entities.enums.DepositStatus
import com.global.api.entities.enums.PaymentEntryMode
import com.global.api.entities.enums.PaymentType
import com.global.api.entities.enums.SortDirection
import com.global.api.entities.enums.TransactionSortProperty
import com.global.api.entities.enums.TransactionStatus
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPDropdown
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSelectableField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPSwitch
import com.globalpayments.android.sdk.sample.gpapi.utils.showDatePicker
import java.text.SimpleDateFormat
import java.util.Locale

private val DateTimeFormatter = SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.US)

@Composable
fun TransactionListParams(viewModel: TransactionsListViewModel) {

    val columnSpacing = 3.dp
    var isExpanded by remember { mutableStateOf(false) }

    val model by viewModel.screenModel.collectAsState()
    val context = LocalContext.current

    GPSwitch(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(id = R.string.get_from_settlements),
        isChecked = model.getFromSettlements,
        onCheckedChanged = viewModel::updateGetFromSettlements
    )

    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        GPInputField(
            modifier = Modifier
                .padding(end = columnSpacing)
                .weight(1f),
            title = R.string.page,
            value = model.page,
            onValueChanged = viewModel::updatePage,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
        )
        GPInputField(
            modifier = Modifier
                .padding(start = columnSpacing)
                .weight(1f),
            title = R.string.page_size,
            value = model.pageSize,
            onValueChanged = viewModel::updatePageSize,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
        )
    }
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        GPDropdown(
            modifier = Modifier
                .padding(end = columnSpacing)
                .weight(1f),
            title = R.string.order,
            selectedValue = model.order,
            values = SortDirection.values().toList(),
            onValueSelected = viewModel::updateOrder,
            onEmptyClicked = { viewModel.updateOrder(null) }
        )
        GPDropdown(
            modifier = Modifier
                .padding(start = columnSpacing)
                .weight(1f),
            title = R.string.order_by,
            selectedValue = model.orderBy,
            values = TransactionSortProperty.values().toList(),
            onValueSelected = viewModel::updateOrderBy,
            onEmptyClicked = { viewModel.updateOrderBy(null) }
        )
    }

    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        val brands = stringArrayResource(id = R.array.brands)
        GPDropdown(
            modifier = Modifier
                .padding(start = columnSpacing)
                .weight(1f),
            title = R.string.brand,
            selectedValue = model.brand,
            values = brands.toList(),
            onValueSelected = viewModel::updateBrand,
            onEmptyClicked = { viewModel.updateBrand("") }
        )
        GPInputField(
            modifier = Modifier
                .padding(start = columnSpacing)
                .weight(1f),
            title = R.string.brand_reference,
            value = model.brandReference,
            onValueChanged = viewModel::updateBrandReference,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done)
        )
    }

    GPInputField(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
        title = R.string.auth_code,
        value = model.authCode,
        onValueChanged = viewModel::updateAuthCode,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
    )
    GPInputField(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
        title = R.string.reference,
        value = model.reference,
        onValueChanged = viewModel::updateReference,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
    )

    GPDropdown(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
        title = R.string.status,
        selectedValue = model.status,
        values = TransactionStatus.values().toList(),
        onValueSelected = viewModel::updateStatus,
        onEmptyClicked = { viewModel.updateStatus(null) }
    )

    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        GPInputField(
            modifier = Modifier
                .padding(end = columnSpacing)
                .weight(1f),
            title = R.string.number_first_6,
            value = model.numberFirst6,
            onValueChanged = viewModel::updateNumberFirst6,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
        )
        GPInputField(
            modifier = Modifier
                .padding(start = columnSpacing)
                .weight(1f),
            title = R.string.number_last_4,
            value = model.numberLast4,
            onValueChanged = viewModel::updateNumberLast4,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
        )
    }

    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        GPSelectableField(
            modifier = Modifier
                .padding(end = columnSpacing)
                .weight(1f),
            title = stringResource(id = R.string.from_time_created),
            textToDisplay = model.fromTimeCreated?.let { DateTimeFormatter.format(it) } ?: "",
            onButtonClick = {
                showDatePicker(context = context, onDateSelected = viewModel::updateFromTimeCreated)
            }
        )
        GPSelectableField(
            modifier = Modifier
                .padding(start = columnSpacing)
                .weight(1f),
            title = stringResource(id = R.string.to_time_created),
            textToDisplay = model.toTimeCreated?.let { DateTimeFormatter.format(it) } ?: "",
            onButtonClick = {
                showDatePicker(context = context, onDateSelected = viewModel::updateToTimeCreated)
            }
        )
    }

    if (isExpanded) {
        if (model.getFromSettlements) {
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
            ) {
                GPSelectableField(
                    modifier = Modifier
                        .padding(end = columnSpacing)
                        .weight(1f),
                    title = stringResource(id = R.string.from_deposit_time_created),
                    textToDisplay = model.fromDepositTimeCreated?.let { DateTimeFormatter.format(it) } ?: "",
                    onButtonClick = {
                        showDatePicker(context = context, onDateSelected = viewModel::updateFromDepositTimeCreated)
                    }
                )
                GPSelectableField(
                    modifier = Modifier
                        .padding(start = columnSpacing)
                        .weight(1f),
                    title = stringResource(id = R.string.to_deposit_time_created),
                    textToDisplay = model.toDepositTimeCreated?.let { DateTimeFormatter.format(it) } ?: "",
                    onButtonClick = {
                        showDatePicker(context = context, onDateSelected = viewModel::updateToDepositTimeCreated)
                    }
                )
            }

            GPDropdown(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                title = R.string.deposit_status,
                selectedValue = model.depositStatus,
                values = DepositStatus.values().toList(),
                onValueSelected = viewModel::updateDepositStatus,
                onEmptyClicked = { viewModel.updateDepositStatus(null) }
            )

            GPInputField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                title = R.string.arn,
                value = model.arn,
                onValueChanged = viewModel::updateArn,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            GPInputField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                title = R.string.deposit_id,
                value = model.depositId,
                onValueChanged = viewModel::updateDepositId,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
            ) {
                GPSelectableField(
                    modifier = Modifier
                        .padding(end = columnSpacing)
                        .weight(1f),
                    title = stringResource(id = R.string.from_batch_time_created),
                    textToDisplay = model.fromBatchTimeCreated?.let { DateTimeFormatter.format(it) } ?: "",
                    onButtonClick = {
                        showDatePicker(context = context, onDateSelected = viewModel::updateFromBatchTimeCreated)
                    }
                )
                GPSelectableField(
                    modifier = Modifier
                        .padding(start = columnSpacing)
                        .weight(1f),
                    title = stringResource(id = R.string.to_batch_time_created),
                    textToDisplay = model.toBatchTimeCreated?.let { DateTimeFormatter.format(it) } ?: "",
                    onButtonClick = {
                        showDatePicker(context = context, onDateSelected = viewModel::updateToBatchTimeCreated)
                    }
                )
            }

            GPInputField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                title = R.string.system_mid,
                value = model.merchantID,
                onValueChanged = viewModel::updateMerchantID,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
            )

            GPInputField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                title = R.string.system_hierarchy,
                value = model.systemHierarchy,
                onValueChanged = viewModel::updateSystemHierarchy,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
            )
        } else {
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
            ) {
                GPInputField(
                    modifier = Modifier
                        .padding(end = columnSpacing)
                        .weight(1f),
                    title = R.string.amount,
                    value = model.amount,
                    onValueChanged = viewModel::updateAmount,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                )
                val currencies = stringArrayResource(id = R.array.currencies).toList()
                GPDropdown(
                    modifier = Modifier
                        .padding(start = columnSpacing)
                        .weight(1f),
                    title = R.string.currency,
                    selectedValue = model.currency,
                    values = currencies,
                    onValueSelected = viewModel::updateCurrency,
                    onEmptyClicked = { viewModel.updateCurrency("") }
                )
            }
            GPInputField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                title = R.string.id,
                value = model.id,
                onValueChanged = viewModel::updateId,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
            )
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
            ) {
                GPDropdown(
                    modifier = Modifier
                        .padding(end = columnSpacing)
                        .weight(1f),
                    title = R.string.type,
                    selectedValue = model.type,
                    values = PaymentType.values().toList(),
                    onValueSelected = viewModel::updateType,
                    onEmptyClicked = { viewModel.updateType(null) }
                )
                GPDropdown(
                    modifier = Modifier
                        .padding(start = columnSpacing)
                        .weight(1f),
                    title = R.string.channel,
                    selectedValue = model.channel,
                    values = Channel.values().toList(),
                    onValueSelected = viewModel::updateChannel,
                    onEmptyClicked = { viewModel.updateChannel(null) }
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
            ) {
                GPInputField(
                    modifier = Modifier
                        .padding(end = columnSpacing)
                        .weight(1f),
                    title = R.string.token_first_6,
                    value = model.tokenFirst6,
                    onValueChanged = viewModel::updateTokenFirst6,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                )
                GPInputField(
                    modifier = Modifier
                        .padding(start = columnSpacing)
                        .weight(1f),
                    title = R.string.token_last_4,
                    value = model.tokenLast4,
                    onValueChanged = viewModel::updateTokenLast4,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                )
            }
            GPInputField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                title = R.string.account_name,
                value = model.accountName,
                onValueChanged = viewModel::updateAccountName,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
            )
            GPInputField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                title = R.string.country,
                value = model.country,
                onValueChanged = viewModel::updateCountry,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
            )
            GPInputField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                title = R.string.batch_id,
                value = model.batchId,
                onValueChanged = viewModel::updateBatchId,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
            )
            GPDropdown(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                title = R.string.entry_mode,
                selectedValue = model.entryMode,
                values = PaymentEntryMode.values().toList(),
                onValueSelected = viewModel::updateEntryMode,
                onEmptyClicked = { viewModel.updateEntryMode(null) }
            )

            GPInputField(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                title = R.string.name,
                value = model.name,
                onValueChanged = viewModel::updateName,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
            )
        }
    }

    GPActionButton(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        title = if (isExpanded) stringResource(id = R.string.fewer_fields) else stringResource(id = R.string.more_fields),
        onClick = { isExpanded = !isExpanded }
    )

    GPSubmitButton(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        title = stringResource(R.string.get_transactions),
        onClick = { viewModel.getTransactions() }
    )
}

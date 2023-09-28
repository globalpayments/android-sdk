package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.account.list

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.global.api.entities.enums.MerchantAccountStatus
import com.global.api.entities.enums.MerchantAccountsSortProperty
import com.global.api.entities.enums.SortDirection
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPDropdown
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSelectableField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton
import com.globalpayments.android.sdk.sample.gpapi.utils.showDatePicker

@Composable
fun AccountsListParams(viewModel: AccountsListViewModel) {
    val columnSpacing = 3.dp
    var isExpanded by remember { mutableStateOf(false) }

    val model by viewModel.screenModel.collectAsState()
    val context = LocalContext.current


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
            values = MerchantAccountsSortProperty.values().toList(),
            onValueSelected = viewModel::updateOrderBy,
            onEmptyClicked = { viewModel.updateOrderBy(null) }
        )
    }

    if (isExpanded) {
        GPInputField(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            title = R.string.id,
            value = model.id,
            onValueChanged = viewModel::updateId,
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
                title = stringResource(id = R.string.from_time_created),
                textToDisplay = model.fromTimeCreated?.toString() ?: "",
                onButtonClick = {
                    showDatePicker(context = context, onDateSelected = viewModel::updateFromTimeCreated)
                }
            )
            GPSelectableField(
                modifier = Modifier
                    .padding(start = columnSpacing)
                    .weight(1f),
                title = stringResource(id = R.string.to_time_created),
                textToDisplay = model.toTimeCreated?.toString() ?: "",
                onButtonClick = {
                    showDatePicker(context = context, onDateSelected = viewModel::updateToTimeCreated)
                }
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
                title = R.string.name,
                value = model.name,
                onValueChanged = viewModel::updateName,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            GPDropdown(
                modifier = Modifier
                    .padding(start = columnSpacing)
                    .weight(1f),
                title = R.string.status,
                selectedValue = model.status,
                values = MerchantAccountStatus.values().toList(),
                onValueSelected = viewModel::updateStatus,
                onEmptyClicked = { viewModel.updateStatus(null) }
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
        title = "Get Accounts",
        onClick = { viewModel.getAccounts() }
    )
}

package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.disputes.list

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
import com.global.api.entities.enums.DisputeSortProperty
import com.global.api.entities.enums.DisputeStage
import com.global.api.entities.enums.DisputeStatus
import com.global.api.entities.enums.SortDirection
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.components.GPActionButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPDropdown
import com.globalpayments.android.sdk.sample.gpapi.components.GPInputField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSelectableField
import com.globalpayments.android.sdk.sample.gpapi.components.GPSubmitButton
import com.globalpayments.android.sdk.sample.gpapi.components.GPSwitch
import com.globalpayments.android.sdk.sample.gpapi.utils.DateFormatter
import com.globalpayments.android.sdk.sample.gpapi.utils.showDatePicker

@Composable
fun DisputesListParams(viewModel: DisputesListViewModel) {

    val columnSpacing = 3.dp
    var isExpanded by remember { mutableStateOf(false) }

    val model by viewModel.screenModel.collectAsState()
    val context = LocalContext.current

    GPSwitch(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(id = R.string.get_from_settlements),
        isChecked = model.isFromSettlements,
        onCheckedChanged = viewModel::updateIsFromSettlements
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
            values = SortDirection.entries,
            onValueSelected = viewModel::updateOrder,
            onEmptyClicked = { viewModel.updateOrder(null) }
        )
        GPDropdown(
            modifier = Modifier
                .padding(start = columnSpacing)
                .weight(1f),
            title = R.string.order_by,
            selectedValue = model.orderBy,
            values = DisputeSortProperty.entries,
            onValueSelected = viewModel::updateOrderBy,
            onEmptyClicked = { viewModel.updateOrderBy(null) }
        )
    }

    GPDropdown(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
        title = R.string.status,
        selectedValue = model.status,
        values = DisputeStatus.entries,
        onValueSelected = viewModel::updateStatus,
        onEmptyClicked = { viewModel.updateStatus(null) }
    )

    if (isExpanded) {
        GPInputField(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            title = R.string.arn,
            value = model.arn,
            onValueChanged = viewModel::updateArn,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
        )
        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        ) {
            val fromText = if (model.isFromSettlements) R.string.from_stage_time_created else R.string.from_time_created
            GPSelectableField(
                modifier = Modifier
                    .padding(end = columnSpacing)
                    .weight(1f),
                title = stringResource(id = fromText),
                textToDisplay = model.fromTimeCreated?.let { DateFormatter.format(it) } ?: "",
                onButtonClick = {
                    showDatePicker(context = context, onDateSelected = viewModel::updateFromTimeCreated)
                }
            )
            val toText = if (model.isFromSettlements) R.string.to_stage_time_created else R.string.to_time_created
            GPSelectableField(
                modifier = Modifier
                    .padding(start = columnSpacing)
                    .weight(1f),
                title = stringResource(id = toText),
                textToDisplay = model.toTimeCreated?.let { DateFormatter.format(it) } ?: "",
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
                title = R.string.brand,
                value = model.brand,
                onValueChanged = viewModel::updateBrand,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
            )

            GPDropdown(
                modifier = Modifier
                    .padding(start = columnSpacing)
                    .weight(1f),
                title = R.string.stage,
                selectedValue = model.disputeStage,
                values = DisputeStage.entries,
                onValueSelected = viewModel::updateDisputeStage,
                onEmptyClicked = { viewModel.updateDisputeStage(null) }
            )
        }
    }

    GPInputField(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
        title = R.string.system_mid,
        value = model.systemMID,
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
        title = stringResource(R.string.get_disputes_list),
        onClick = { viewModel.getDisputes() }
    )
}

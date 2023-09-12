package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.deposits.list

import com.global.api.entities.enums.DepositSortProperty
import com.global.api.entities.enums.DepositStatus
import com.global.api.entities.enums.SortDirection
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import java.util.Date

data class DepositsListModel(
    val page: String = "1",
    val pageSize: String = "5",
    val orderBy: DepositSortProperty? = DepositSortProperty.TimeCreated,
    val order: SortDirection? = SortDirection.Ascending,
    val fromTimeCreated: Date? = null,
    val toTimeCreated: Date? = null,
    val id: String = "",
    val status: DepositStatus? = null,
    val amount: String = "",
    val maskedAccountNumberLast4: String = "",
    val systemMID: String = "",
    val systemHierarchy: String = "",

    val responses: List<GPSampleResponseModel> = emptyList(),
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.disputes.list

import com.global.api.entities.enums.DisputeSortProperty
import com.global.api.entities.enums.DisputeStage
import com.global.api.entities.enums.DisputeStatus
import com.global.api.entities.enums.SortDirection
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import java.util.Date

data class DisputesListModel(
    val page: String = "1",
    val pageSize: String = "5",
    val isFromSettlements: Boolean = false,
    val orderBy: DisputeSortProperty? = DisputeSortProperty.ARN,
    val order: SortDirection? = SortDirection.Ascending,
    val brand: String = "",
    val arn: String = "",
    val status: DisputeStatus? = null,
    val disputeStage: DisputeStage? = null,
    val fromTimeCreated: Date? = null,
    val toTimeCreated: Date? = null,
    val systemMID: String = "",
    val systemHierarchy: String = "",

    val responses: List<GPSampleResponseModel> = emptyList(),
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

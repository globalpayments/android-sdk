package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.actions.list

import com.global.api.entities.enums.ActionSortProperty
import com.global.api.entities.enums.SortDirection
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import java.util.Date

data class ActionsListModel(
    val page: String = "1",
    val pageSize: String = "5",
    val orderBy: ActionSortProperty? = ActionSortProperty.TimeCreated,
    val order: SortDirection? = SortDirection.Ascending,
    val fromTimeCreated: Date? = null,
    val toTimeCreated: Date? = null,
    val id: String = "",
    val type: String = "",
    val resource: String = "",
    val resourceStatus: String = "",
    val resourceId: String = "",
    val merchantName: String = "",
    val accountName: String = "",
    val appName: String = "",
    val version: String = "",
    val responseCode: String = "",
    val responseHttpCode: String = "",


    val responses: List<GPSampleResponseModel> = emptyList(),
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

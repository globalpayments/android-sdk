package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.account.list

import com.global.api.entities.enums.MerchantAccountStatus
import com.global.api.entities.enums.MerchantAccountsSortProperty
import com.global.api.entities.enums.SortDirection
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import java.util.Date

data class AccountsListModel(

    val page: String = "1",
    val pageSize: String = "5",
    val orderBy: MerchantAccountsSortProperty? = MerchantAccountsSortProperty.TIME_CREATED,
    val order: SortDirection? = SortDirection.Ascending,
    val fromTimeCreated: Date? = null,
    val toTimeCreated: Date? = null,
    val id: String = "",
    val name: String = "",
    val status: MerchantAccountStatus? = null,

    val responses: List<GPSampleResponseModel>? = null,
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.paymentmethods.list

import com.global.api.entities.enums.SortDirection
import com.global.api.entities.enums.StoredPaymentMethodStatus
import com.global.api.entities.enums.TransactionSortProperty
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import java.util.Date

data class PaymentMethodsListModel(
    val page: String = "1",
    val pageSize: String = "5",
    val orderBy: TransactionSortProperty? = TransactionSortProperty.TimeCreated,
    val order: SortDirection? = SortDirection.Ascending,
    val startDate: Date? = null,
    val endDate: Date? = null,
    val startLastUpdatedDate: Date? = null,
    val endLastUpdatedDate: Date? = null,
    val id: String = "",
    val referenceNumber: String = "",
    val status: StoredPaymentMethodStatus? = null,

    val responses: List<GPSampleResponseModel> = emptyList(),
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

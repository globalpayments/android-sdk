package com.globalpayments.android.sdk.sample.gpapi.screens.reporting.transactions.list

import com.global.api.entities.enums.Channel
import com.global.api.entities.enums.DepositStatus
import com.global.api.entities.enums.PaymentEntryMode
import com.global.api.entities.enums.PaymentType
import com.global.api.entities.enums.SortDirection
import com.global.api.entities.enums.TransactionSortProperty
import com.global.api.entities.enums.TransactionStatus
import com.globalpayments.android.sdk.sample.gpapi.components.GPSampleResponseModel
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import java.util.Date

data class TransactionsListModel(
    val getFromSettlements: Boolean = false,
    val page: String = "1",
    val pageSize: String = "5",
    val order: SortDirection? = null,
    val orderBy: TransactionSortProperty? = null,
    val id: String = "",
    val type: PaymentType? = null,
    val channel: Channel? = null,
    val arn: String = "",
    val amount: String = "",
    val currency: String = "",
    val numberFirst6: String = "",
    val numberLast4: String = "",
    val tokenFirst6: String = "",
    val tokenLast4: String = "",
    val accountName: String = "",
    val brand: String = "",
    val brandReference: String = "",
    val authCode: String = "",
    val reference: String = "",
    val status: TransactionStatus? = null,
    val depositStatus: DepositStatus? = null,
    val depositId: String = "",
    val fromTimeCreated: Date? = null,
    val toTimeCreated: Date? = null,
    val country: String = "",
    val batchId: String = "",
    val entryMode: PaymentEntryMode? = null,
    val name: String = "",
    val fromDepositTimeCreated: Date? = null,
    val toDepositTimeCreated: Date? = null,
    val fromBatchTimeCreated: Date? = null,
    val toBatchTimeCreated: Date? = null,
    val merchantID: String = "",
    val systemHierarchy: String = "",

    val responses: List<GPSampleResponseModel> = emptyList(),
    val gpSnippetResponseModel: GPSnippetResponseModel = GPSnippetResponseModel()
)

package com.globalpayments.android.sdk.sample.gpapi.utils

import com.global.api.entities.enums.MerchantAccountStatus
import com.global.api.entities.enums.MerchantAccountType
import com.global.api.entities.enums.MerchantAccountsSortProperty
import com.global.api.entities.enums.SortDirection
import com.global.api.entities.reporting.DataServiceCriteria
import com.global.api.entities.reporting.MerchantAccountSummary
import com.global.api.entities.reporting.MerchantAccountSummaryPaged
import com.global.api.entities.reporting.SearchCriteria
import com.global.api.services.ReportingService
import com.globalpayments.android.sdk.sample.common.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date


suspend fun getAccountByType(
    merchantSenderId: String,
    merchantAccountType: MerchantAccountType,
    startDate: Date,
    endDate: Date
): MerchantAccountSummary? =
    withContext(Dispatchers.IO) {
        val response: MerchantAccountSummaryPaged = ReportingService
            .findAccounts(1, 10)
            .orderBy(MerchantAccountsSortProperty.TIME_CREATED, SortDirection.Descending)
            .where<Any>(SearchCriteria.StartDate, startDate)
            .and<Any>(SearchCriteria.EndDate, endDate)
            .and(DataServiceCriteria.MerchantId, merchantSenderId)
            .and(SearchCriteria.AccountStatus, MerchantAccountStatus.ACTIVE)
            .execute(Constants.DEFAULT_GPAPI_CONFIG)
        response.results.find { it.type == merchantAccountType }
    }

package com.globalpayments.android.sdk.sample.gpapi.screens.accesstoken

import com.global.api.entities.enums.Environment
import com.global.api.entities.enums.IntervalToExpire
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel

data class CreateAccessTokenScreenModel(
    val appId: String = "",
    val appKey: String = "",
    val secondsToExpire: String = "",
    val environment: Environment? = null,
    val intervalToExpire: IntervalToExpire? = null,
    val permissions: List<String> = emptyList(),
    val currentTab: Int = 0,
    val responseModel: GPSnippetResponseModel = GPSnippetResponseModel(),
)

package com.globalpayments.android.sdk.sample.repository

import android.content.Context
import com.global.api.services.GpApiService
import com.globalpayments.android.sdk.sample.utils.AppPreferences
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfiguration
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfigurationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccessTokenRepository(
    private val context: Context
) {
    private val sharedAppPreferences = AppPreferences(context)
    suspend fun getAccessToken(): String = withContext(Dispatchers.IO) {
        GpApiService
            .generateTransactionKey(
                GPAPIConfigurationUtils.buildDefaultGpApiConfig(sharedAppPreferences.gpAPIConfiguration ?: GPAPIConfiguration.fromBuildConfig())
                    .apply {
                        permissions = arrayOf("PMT_POST_Create_Single", "ACC_GET_Single")
                    }
            ).accessToken
    }
}

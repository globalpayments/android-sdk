package com.globalpayments.android.sdk.sample.gpapi.screens.accesstoken

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.enums.Environment
import com.global.api.entities.enums.IntervalToExpire
import com.global.api.entities.gpApi.entities.AccessTokenInfo
import com.global.api.serviceConfigs.GpApiConfig
import com.global.api.services.GpApiService
import com.globalpayments.android.sdk.sample.gpapi.components.GPSnippetResponseModel
import com.globalpayments.android.sdk.sample.gpapi.utils.mapNotNullFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateAccessTokenViewModel : ViewModel() {

    val screenModel: MutableStateFlow<CreateAccessTokenScreenModel> = MutableStateFlow(CreateAccessTokenScreenModel())

    fun appIdChanged(value: String) {
        screenModel.update { it.copy(appId = value) }
    }

    fun appKeyChanged(value: String) {
        screenModel.update { it.copy(appKey = value) }
    }

    fun secondsToExpireChanged(value: String) {
        screenModel.update { it.copy(secondsToExpire = value) }
    }

    fun environmentChanged(value: Environment?) {
        screenModel.update { it.copy(environment = value) }
    }

    fun intervalToExpireChanged(value: IntervalToExpire?) {
        screenModel.update { it.copy(intervalToExpire = value) }
    }

    fun addOrRemovePermission(permission: String, checked: Boolean) {
        if (checked) {
            screenModel.update { it.copy(permissions = it.permissions + permission) }
        } else {
            screenModel.update { it.copy(permissions = it.permissions - permission) }
        }
    }

    fun createAccessToken() {
        viewModelScope.launch(Dispatchers.IO) {
            screenModel.update { it.copy(responseModel = GPSnippetResponseModel(), currentTab = 0) }
            val screenModel = screenModel.value
            try {
                val accessTokenInfo = createAccessToken(
                    appId = screenModel.appId.takeIf(String::isNotBlank),
                    appKey = screenModel.appKey.takeIf(String::isNotBlank),
                    environment = screenModel.environment,
                    secondsToExpire = screenModel.secondsToExpire.toIntOrNull(),
                    intervalToExpire = screenModel.intervalToExpire,
                    permissions = screenModel.permissions.toTypedArray()
                )
                Log.d("CreateAccessToken", accessTokenInfo.toString())
                this@CreateAccessTokenViewModel.screenModel.update {
                    it.copy(
                        responseModel = GPSnippetResponseModel(accessTokenInfo::class.java.simpleName, accessTokenInfo.mapNotNullFields(), false),
                        currentTab = 1
                    )
                }
            } catch (exception: Exception) {
                this@CreateAccessTokenViewModel.screenModel.update {
                    it.copy(
                        responseModel = GPSnippetResponseModel(
                            AccessTokenInfo::class.java.simpleName,
                            listOf("Exception" to (exception.message ?: "")),
                            true
                        ),
                        currentTab = 1
                    )
                }
            }
        }
    }

    private fun createAccessToken(
        appId: String?,
        appKey: String?,
        environment: Environment?,
        secondsToExpire: Int?,
        intervalToExpire: IntervalToExpire?,
        permissions: Array<String>
    ): AccessTokenInfo {
        val gpApiConfig = GpApiConfig().apply config@{
            setAppId(appId)
            setAppKey(appKey)
            if (environment != null) {
                setEnvironment(environment)
            }
            setSecondsToExpire(secondsToExpire ?: 0)
            if (intervalToExpire != null) {
                setIntervalToExpire(intervalToExpire)
            }
            if (permissions.isNotEmpty()) {
                setPermissions(permissions)
            }
        }
        return GpApiService.generateTransactionKey(gpApiConfig)
    }
}

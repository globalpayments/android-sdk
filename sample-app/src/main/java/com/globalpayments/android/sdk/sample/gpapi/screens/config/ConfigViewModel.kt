package com.globalpayments.android.sdk.sample.gpapi.screens.config

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.global.api.entities.enums.Channel
import com.global.api.entities.enums.Environment
import com.global.api.entities.enums.IntervalToExpire
import com.globalpayments.android.sdk.sample.gpapi.navigation.NavigationManager
import com.globalpayments.android.sdk.sample.utils.AppPreferences
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfiguration
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfigurationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConfigViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = AppPreferences(application)
    val screenModel = MutableStateFlow(ConfigScreenModel())

    fun appIdChanged(value: String) = screenModel.update { it.copy(appId = value) }
    fun appKeyChanged(value: String) = screenModel.update { it.copy(appKey = value) }
    fun merchantIdChanged(value: String) = screenModel.update { it.copy(merchantId = value) }
    fun apiKeyChanged(value: String) = screenModel.update { it.copy(apiKey = value) }
    fun secondsToExpireChanged(value: String) = screenModel.update { it.copy(tokenSecondsToExpire = value) }
    fun environmentChanged(value: Environment) = screenModel.update { it.copy(environment = value) }
    fun intervalToExpireChanged(value: IntervalToExpire) = screenModel.update { it.copy(tokenIntervalToExpire = value) }
    fun channelChanged(value: Channel) = screenModel.update { it.copy(channel = value) }
    fun countryChanged(value: String) = screenModel.update { it.copy(country = value) }
    fun apiVersionChanged(value: String) = screenModel.update { it.copy(apiVersion = value) }
    fun challengeNotificationUrlChanged(value: String) = screenModel.update { it.copy(challengeNotificationUrl = value) }
    fun methodNotificationUrlChanged(value: String) = screenModel.update { it.copy(methodNotificationUrl = value) }
    fun serviceUrlChanged(value: String) = screenModel.update { it.copy(serviceUrl = value) }
    fun statusUrlChanged(value: String) = screenModel.update { it.copy(statusUrl = value) }
    fun transactionProcessingAccountIdChanged(value: String) = screenModel.update { it.copy(transactionProcessingAccountId = value) }
    fun transactionProcessingAccountNameChanged(value: String) = screenModel.update { it.copy(transactionProcessingAccountName = value) }
    fun tokenizationAccountIdChanged(value: String) = screenModel.update { it.copy(tokenizationAccountId = value) }
    fun tokenizationAccountNameChanged(value: String) = screenModel.update { it.copy(tokenizationAccountName = value) }
    fun riskAssessmentAccountIdChanged(value: String) = screenModel.update { it.copy(riskAssessmentAccountId = value) }
    fun dataAccountIdChanged(value: String) = screenModel.update { it.copy(dataAccountId = value) }
    fun disputeManagementAccountIdChanged(value: String) = screenModel.update { it.copy(disputeManagementAccountId = value) }

    init {
        loadConfig()
    }

    fun saveConfiguration() {
        viewModelScope.launch(Dispatchers.IO) {
            val gpConfig = screenModel.value.toGPAPIConfig()
            sharedPreferences.gpAPIConfiguration = gpConfig
            GPAPIConfigurationUtils.initializeDefaultGPAPIConfiguration(gpConfig)
            NavigationManager.navigateBack()
        }
    }

    private fun loadConfig() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentConfig = sharedPreferences.gpAPIConfiguration ?: GPAPIConfiguration.fromBuildConfig()
            val model = currentConfig.toScreenModel()
            screenModel.value = model
        }
    }

    private fun ConfigScreenModel.toGPAPIConfig(): GPAPIConfiguration {
        return GPAPIConfiguration(
            merchantId = merchantId.takeIf(String::isNotBlank),
            transactionProcessingAccountName = transactionProcessingAccountName.takeIf(String::isNotBlank),
            tokenizationAccountName = tokenizationAccountName.takeIf(String::isNotBlank),
            transactionProcessingAccountId = transactionProcessingAccountId.takeIf(String::isNotBlank),
            dataAccountId = dataAccountId.takeIf(String::isNotBlank),
            disputeManagementAccountId = disputeManagementAccountId.takeIf(String::isNotBlank),
            tokenizationAccountId = tokenizationAccountId.takeIf(String::isNotBlank),
            riskAssessmentAccountId = riskAssessmentAccountId.takeIf(String::isNotBlank),
            appId = appId.takeIf(String::isNotBlank),
            appKey = appKey.takeIf(String::isNotBlank),
            serviceUrl = serviceUrl.takeIf(String::isNotBlank),
            challengeNotificationUrl = challengeNotificationUrl.takeIf(String::isNotBlank),
            methodNotificationUrl = methodNotificationUrl.takeIf(String::isNotBlank),
            apiVersion = apiVersion.takeIf(String::isNotBlank),
            tokenSecondsToExpire = tokenSecondsToExpire.toIntOrNull(),
            channel = channel,
            tokenIntervalToExpire = tokenIntervalToExpire,
            environment = environment,
            selectedCountry = country.takeIf(String::isNotBlank),
            statusUrl = statusUrl.takeIf(String::isNotBlank),
            apiKey = apiKey.takeIf(String::isNotBlank),
        )
    }

    private fun GPAPIConfiguration.toScreenModel(): ConfigScreenModel {
        return ConfigScreenModel(
            appId = appId ?: "",
            appKey = appKey ?: "",
            merchantId = merchantId ?: "",
            transactionProcessingAccountName = transactionProcessingAccountName ?: "",
            transactionProcessingAccountId = transactionProcessingAccountId ?: "",
            dataAccountId = dataAccountId ?: "",
            disputeManagementAccountId = disputeManagementAccountId ?: "",
            riskAssessmentAccountId = riskAssessmentAccountId ?: "",
            tokenizationAccountId = tokenizationAccountId ?: "",
            tokenizationAccountName = tokenizationAccountName ?: "",
            serviceUrl = serviceUrl ?: "",
            challengeNotificationUrl = challengeNotificationUrl ?: "",
            methodNotificationUrl = methodNotificationUrl ?: "",
            apiVersion = apiVersion ?: "",
            tokenSecondsToExpire = tokenSecondsToExpire?.toString() ?: "",
            tokenIntervalToExpire = tokenIntervalToExpire ?: IntervalToExpire.DAY,
            country = selectedCountry ?: "",
            channel = channel ?: Channel.CardNotPresent,
            environment = environment ?: Environment.TEST,
            apiKey = apiKey ?: "",
        )
    }
}

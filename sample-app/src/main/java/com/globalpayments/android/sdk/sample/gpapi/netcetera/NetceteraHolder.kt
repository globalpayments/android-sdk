package com.globalpayments.android.sdk.sample.gpapi.netcetera

import android.content.Context
import android.util.Log
import com.globalpayments.android.sdk.task.TaskExecutor
import com.globalpayments.android.sdk.task.TaskResult
import com.netcetera.threeds.sdk.ThreeDS2ServiceInstance
import com.netcetera.threeds.sdk.api.ThreeDS2Service
import com.netcetera.threeds.sdk.api.configparameters.ConfigParameters
import com.netcetera.threeds.sdk.api.configparameters.builder.ConfigurationBuilder
import com.netcetera.threeds.sdk.api.configparameters.builder.SchemeConfiguration
import com.netcetera.threeds.sdk.api.ui.logic.UiCustomization
import java.util.*

object NetceteraHolder {

    private var isInitialized = false
    private val threeDS2Service: ThreeDS2Service = ThreeDS2ServiceInstance.get()

    fun getInstance(context: Context, onInstanceCreated: (ThreeDS2Service) -> Unit) {
        if (isInitialized) {
            onInstanceCreated(threeDS2Service)
            return
        }
        TaskExecutor.executeAsync(
            taskToExecute = {
                threeDS2Service.initialize(
                    context,
                    getThreeDS2ConfigParams(context),
                    Locale.getDefault().language,
                    getThreeDS2UICustomization()
                )
            },
            onFinished = {
                when (it) {
                    is TaskResult.Error -> Log.e(
                        "NetceteraHolder",
                        it.exception.message ?: "Error initializing service"
                    )
                    is TaskResult.Success -> {
                        isInitialized = true
                        onInstanceCreated(threeDS2Service)
                    }
                }
            })
    }


    private fun getThreeDS2ConfigParams(context: Context): ConfigParameters {
        val assetManager = context.assets
        return ConfigurationBuilder()
            .license(assetManager.readLicense())
            .configureScheme(
                SchemeConfiguration.visaSchemeConfiguration()
                    .encryptionPublicKeyFromAssetCertificate(assetManager, "acs2021.pem")
                    .rootPublicKeyFromAssetCertificate(assetManager, "acs2021.pem")
                    .build()
            )
            .build()
    }

    private fun getThreeDS2UICustomization(): UiCustomization {
        return UiCustomization()
    }

}
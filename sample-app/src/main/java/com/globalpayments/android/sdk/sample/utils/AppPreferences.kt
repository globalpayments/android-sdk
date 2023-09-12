package com.globalpayments.android.sdk.sample.utils

import android.content.Context
import android.content.SharedPreferences
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfiguration
import com.globalpayments.android.sdk.utils.Strings
import com.google.gson.Gson

class AppPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE)
    }

    var gpAPIConfiguration: GPAPIConfiguration?
        get() {
            val gpapiConfigurationJson = sharedPreferences
                .getString(GPAPI_CONFIGURATION_KEY, Strings.EMPTY)
                .takeIf { !it.isNullOrBlank() }
                ?: return null
            return Gson().fromJson(gpapiConfigurationJson, GPAPIConfiguration::class.java)
        }
        set(value) {
            val gpapiConfigurationJson = Gson().toJson(value)
            sharedPreferences.edit()
                .putString(GPAPI_CONFIGURATION_KEY, gpapiConfigurationJson)
                .apply()
        }

    companion object {
        private const val PREFERENCES_FILENAME = "main_preferences"
        private const val GPAPI_CONFIGURATION_KEY = "GPAPI_CONFIGURATION_KEY"
    }
}

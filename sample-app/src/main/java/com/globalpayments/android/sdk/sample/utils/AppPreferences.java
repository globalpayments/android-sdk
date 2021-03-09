package com.globalpayments.android.sdk.sample.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.globalpayments.android.sdk.sample.gpapi.configuration.GPAPIConfiguration;
import com.google.gson.Gson;

import static com.globalpayments.android.sdk.utils.Strings.EMPTY;
import static com.globalpayments.android.sdk.utils.Utils.isNotNullOrBlank;

public class AppPreferences {
    private static final String PREFERENCES_FILENAME = "main_preferences";
    private static final String GPAPI_CONFIGURATION_KEY = "GPAPI_CONFIGURATION_KEY";

    private final SharedPreferences sharedPreferences;

    public AppPreferences(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE);
    }

    public void saveGPAPIConfiguration(GPAPIConfiguration gpapiConfiguration) {
        String gpapiConfigurationJson = new Gson().toJson(gpapiConfiguration);

        sharedPreferences.edit()
                .putString(GPAPI_CONFIGURATION_KEY, gpapiConfigurationJson)
                .apply();
    }

    public GPAPIConfiguration getGPAPIConfiguration() {
        GPAPIConfiguration gpapiConfiguration = null;

        String gpapiConfigurationJson = sharedPreferences.getString(GPAPI_CONFIGURATION_KEY, EMPTY);

        if (isNotNullOrBlank(gpapiConfigurationJson)) {
            gpapiConfiguration = new Gson().fromJson(gpapiConfigurationJson, GPAPIConfiguration.class);
        }

        return gpapiConfiguration;
    }
}

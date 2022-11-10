package com.globalpayments.android.sdk.sample.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    private static final String PREFERENCES_FILENAME = "main_preferences";
    private static final String GPAPI_CONFIGURATION_KEY = "GPAPI_CONFIGURATION_KEY";

    private final SharedPreferences sharedPreferences;

    public AppPreferences(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE);
    }
}

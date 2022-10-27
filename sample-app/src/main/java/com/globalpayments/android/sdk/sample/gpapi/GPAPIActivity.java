package com.globalpayments.android.sdk.sample.gpapi;

import static com.globalpayments.android.sdk.sample.utils.GPAPIConfigurationUtils.initializeDefaultGPAPIConfiguration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.global.api.entities.enums.Channel;
import com.global.api.entities.enums.Environment;
import com.global.api.entities.enums.IntervalToExpire;
import com.globalpayments.android.sdk.sample.BuildConfig;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseActivity;
import com.globalpayments.android.sdk.sample.gpapi.configuration.GPAPIConfiguration;
import com.globalpayments.android.sdk.sample.utils.AppPreferences;

public class GPAPIActivity extends BaseActivity {
    private AppPreferences appPreferences;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gp_api;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkGPAPIConfiguration();
    }

    @Override
    protected void initDependencies() {
        appPreferences = new AppPreferences(getApplication());
    }

    private void checkGPAPIConfiguration() {
        initializeDefaultGPAPIConfiguration(initConfigurationFromProperties());
        showFragment(new GPAPIMainFragment());
    }

    private GPAPIConfiguration initConfigurationFromProperties() {
        GPAPIConfiguration gpapiConfiguration = new GPAPIConfiguration();
        gpapiConfiguration.setAppId(BuildConfig.APP_ID);
        gpapiConfiguration.setAppKey(BuildConfig.APP_KEY);
        gpapiConfiguration.setServiceUrl(BuildConfig.SERVICE_URL);
        gpapiConfiguration.setChallengeNotificationUrl(BuildConfig.CHALLENGE_NOTIFICATION_URL);
        gpapiConfiguration.setMethodNotificationUrl(BuildConfig.METHOD_NOTIFICATION_URL);
        gpapiConfiguration.setApiVersion(BuildConfig.API_VERSION);
        gpapiConfiguration.setTokenSecondsToExpire(BuildConfig.TOKEN_SECONDS_TO_EXPIRE);
        gpapiConfiguration.setChannel(Channel.valueOf(BuildConfig.CHANNEL));
        gpapiConfiguration.setTokenIntervalToExpire(IntervalToExpire.valueOf(BuildConfig.TOKEN_INTERVAL_TO_EXPIRE));
        gpapiConfiguration.setEnvironment(Environment.valueOf(BuildConfig.ENVIRONMENT));
        gpapiConfiguration.setSelectedCountry(BuildConfig.COUNTRY);
        return gpapiConfiguration;
    }

    protected void showFragment(@NonNull Fragment fragment) {
        showFragment(R.id.gp_api_fragment_container, fragment);
    }

    public void onGPAPIConfigurationDone() {
        showFragment(new GPAPIMainFragment());
    }
}

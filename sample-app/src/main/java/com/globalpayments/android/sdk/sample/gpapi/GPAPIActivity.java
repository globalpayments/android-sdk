package com.globalpayments.android.sdk.sample.gpapi;

import static com.globalpayments.android.sdk.sample.utils.GPAPIConfigurationUtils.initializeDefaultGPAPIConfiguration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseActivity;
import com.globalpayments.android.sdk.sample.gpapi.configuration.GPAPIConfiguration;
import com.globalpayments.android.sdk.sample.gpapi.configuration.GPAPIConfigurationFragment;
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
        GPAPIConfiguration gpapiConfiguration = appPreferences.getGPAPIConfiguration();
        if (gpapiConfiguration == null) {
            showFragment(GPAPIConfigurationFragment.newInstance(false));
        } else {
            initializeDefaultGPAPIConfiguration(gpapiConfiguration);
            showFragment(new GPAPIMainFragment());
        }
    }

    protected void showFragment(@NonNull Fragment fragment) {
        showFragment(R.id.gp_api_fragment_container, fragment);
    }

    public void onGPAPIConfigurationDone() {
        showFragment(new GPAPIMainFragment());
    }
}

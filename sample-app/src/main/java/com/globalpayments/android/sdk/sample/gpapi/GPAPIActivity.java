package com.globalpayments.android.sdk.sample.gpapi;

import static com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfigurationUtils.initializeDefaultGPAPIConfiguration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseActivity;
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfiguration;

public class GPAPIActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gp_api;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkGPAPIConfiguration();
    }

    private void checkGPAPIConfiguration() {
        initializeDefaultGPAPIConfiguration(GPAPIConfiguration.createDefaultConfig());
        showFragment(new GPAPIMainFragment());
    }


    protected void showFragment(@NonNull Fragment fragment) {
        showFragment(R.id.gp_api_fragment_container, fragment);
    }
}

package com.globalpayments.android.sdk.sample.gpapi.configuration;

import static com.globalpayments.android.sdk.sample.utils.GPAPIConfigurationUtils.initializeDefaultGPAPIConfiguration;
import static com.globalpayments.android.sdk.utils.DateUtils.YYYY_MM_DD;
import static com.globalpayments.android.sdk.utils.DateUtils.isValidDate;
import static com.globalpayments.android.sdk.utils.Strings.EMPTY;
import static com.globalpayments.android.sdk.utils.UriUtils.isValidUrl;
import static com.globalpayments.android.sdk.utils.Utils.isNotNullOrBlank;
import static com.globalpayments.android.sdk.utils.Utils.isNull;
import static com.globalpayments.android.sdk.utils.Utils.isNullOrBlank;
import static com.globalpayments.android.sdk.utils.Utils.safeParseInt;
import static com.globalpayments.android.sdk.utils.ViewUtils.getEditTextValue;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.global.api.entities.enums.Channel;
import com.global.api.entities.enums.Environment;
import com.global.api.entities.enums.IntervalToExpire;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomSpinner;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.GPAPIActivity;
import com.globalpayments.android.sdk.sample.gpapi.partials.CountryUtils;
import com.globalpayments.android.sdk.sample.utils.AppPreferences;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GPAPIConfigurationFragment extends BaseFragment {
    private static final String RECONFIGURATION = "RECONFIGURATION";

    private TextInputLayout appIdTextInputLayout;
    private TextInputEditText etAppId;

    private TextInputLayout appKeyTextInputLayout;
    private TextInputEditText etAppKey;

    private TextInputLayout serviceUrlTextInputLayout;
    private TextInputEditText etServiceUrl;

    private TextInputEditText etChallengeNotificationUrl;

    private TextInputEditText etMethodNotificationUrl;

    private TextInputLayout apiVersionTextInputLayout;
    private TextInputEditText etApiVersion;

    private TextInputLayout tokenSecondsToExpireTextInputLayout;
    private TextInputEditText etTokenSecondsToExpire;

    private CustomSpinner intervalToExpireSpinner;
    private CustomSpinner environmentSpinner;
    private CustomSpinner selectCountrySpinner;
    private CustomSpinner selectChannel;

    private String emptyRequiredField;
    private String invalidServiceUrl;
    private String invalidDateFormat;
    private String invalidSecondsToExpire;

    private AppPreferences appPreferences;
    private boolean isStartedForReconfiguration = false;

    private GPAPIConfigurationFragment() {
    }

    public static GPAPIConfigurationFragment newInstance(boolean isStartedForReconfiguration) {
        GPAPIConfigurationFragment gpapiConfigurationFragment = new GPAPIConfigurationFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean(RECONFIGURATION, isStartedForReconfiguration);
        gpapiConfigurationFragment.setArguments(bundle);

        return gpapiConfigurationFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_gp_api_configuration;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            isStartedForReconfiguration = arguments.getBoolean(RECONFIGURATION, false);
        }
    }

    @Override
    protected void initDependencies() {
        emptyRequiredField = getString(R.string.empty_required_field);
        invalidServiceUrl = getString(R.string.invalid_service_url);
        invalidDateFormat = getString(R.string.invalid_date_format);
        invalidSecondsToExpire = getString(R.string.invalid_seconds_to_expire);
        appPreferences = new AppPreferences(requireContext());
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.gp_api_configuration);

        if (isStartedForReconfiguration) {
            customToolbar.setOnBackButtonListener(v -> close());
        }

        appIdTextInputLayout = findViewById(R.id.appIdTextInputLayout);
        etAppId = findViewById(R.id.etAppId);

        appKeyTextInputLayout = findViewById(R.id.appKeyTextInputLayout);
        etAppKey = findViewById(R.id.etAppKey);

        serviceUrlTextInputLayout = findViewById(R.id.serviceUrlTextInputLayout);
        etServiceUrl = findViewById(R.id.etServiceUrl);

        etChallengeNotificationUrl = findViewById(R.id.etChallengeNotificationUrl);

        etMethodNotificationUrl = findViewById(R.id.etMethodNotificationUrl);

        apiVersionTextInputLayout = findViewById(R.id.apiVersionTextInputLayout);
        etApiVersion = findViewById(R.id.etApiVersion);

        tokenSecondsToExpireTextInputLayout = findViewById(R.id.tokenSecondsToExpireTextInputLayout);
        etTokenSecondsToExpire = findViewById(R.id.etTokenSecondsToExpire);

        intervalToExpireSpinner = findViewById(R.id.intervalToExpireSpinner);
        intervalToExpireSpinner.init(IntervalToExpire.values());

        environmentSpinner = findViewById(R.id.environmentSpinner);
        environmentSpinner.init(Environment.values());

        selectChannel = findViewById(R.id.channelConfigSpinner);
        selectChannel.init(Channel.values());

        selectCountrySpinner = findViewById(R.id.selectionCountrySpinner);
        List<String> listCountries = new ArrayList<>(CountryUtils.countryCodeMapByCountry.keySet());
        Collections.sort(listCountries);
        selectCountrySpinner.init(listCountries);

        etAppId.setOnFocusChangeListener(getFocusChangeListener(appIdTextInputLayout));
        etAppKey.setOnFocusChangeListener(getFocusChangeListener(appKeyTextInputLayout));
        etServiceUrl.setOnFocusChangeListener(getFocusChangeListener(serviceUrlTextInputLayout));
        etApiVersion.setOnFocusChangeListener(getFocusChangeListener(apiVersionTextInputLayout));
        etTokenSecondsToExpire.setOnFocusChangeListener(getFocusChangeListener(tokenSecondsToExpireTextInputLayout));

        fillStoredGPAPIConfiguration();

        Button btSaveConfiguration = findViewById(R.id.btSaveConfiguration);
        btSaveConfiguration.setOnClickListener(v -> onSaveConfigurationClicked());
    }

    private View.OnFocusChangeListener getFocusChangeListener(TextInputLayout textInputLayout) {
        return (v, hasFocus) -> {
            if (hasFocus) {
                textInputLayout.setError(EMPTY);
            }
        };
    }

    private void fillStoredGPAPIConfiguration() {
        if (isStartedForReconfiguration) {
            GPAPIConfiguration gpapiConfiguration = appPreferences.getGPAPIConfiguration();

            if (gpapiConfiguration != null) {
                etAppId.setText(gpapiConfiguration.getAppId());
                etAppKey.setText(gpapiConfiguration.getAppKey());
                etServiceUrl.setText(gpapiConfiguration.getServiceUrl());
                etChallengeNotificationUrl.setText(gpapiConfiguration.getChallengeNotificationUrl());
                etMethodNotificationUrl.setText(gpapiConfiguration.getMethodNotificationUrl());
                etApiVersion.setText(gpapiConfiguration.getApiVersion());
                etTokenSecondsToExpire.setText(String.valueOf(gpapiConfiguration.getTokenSecondsToExpire()));
                intervalToExpireSpinner.selectItem(gpapiConfiguration.getTokenIntervalToExpire());
                environmentSpinner.selectItem(gpapiConfiguration.getEnvironment());
                selectCountrySpinner.selectItem(gpapiConfiguration.getSelectedCountry());
                selectChannel.selectItem(gpapiConfiguration.getChannel());
            }
        }
    }

    private void onSaveConfigurationClicked() {
        resetErrors();

        String appId = getEditTextValue(etAppId);
        String appKey = getEditTextValue(etAppKey);
        String serviceUrl = getEditTextValue(etServiceUrl);
        String challengeNotificationUrl = getEditTextValue(etChallengeNotificationUrl);
        String methodNotificationUrl = getEditTextValue(etMethodNotificationUrl);
        String apiVersion = getEditTextValue(etApiVersion);
        Integer tokenSecondsToExpire = safeParseInt(getEditTextValue(etTokenSecondsToExpire));
        IntervalToExpire tokenIntervalToExpire = intervalToExpireSpinner.getSelectedOption();
        Environment environment = environmentSpinner.getSelectedOption();
        String selectedCountry = selectCountrySpinner.getSelectedOption();
        Channel channel = selectChannel.getSelectedOption();

        if (areAllInputValuesValid(appId, appKey, serviceUrl, apiVersion, tokenSecondsToExpire)) {
            saveConfiguration(appId, appKey, serviceUrl, challengeNotificationUrl, methodNotificationUrl,
                    apiVersion, tokenSecondsToExpire,
                    tokenIntervalToExpire, environment, selectedCountry, channel);
            initGPAPIConfiguration();
            finish();
        }
    }

    private void finish() {
        GPAPIActivity gpapiActivity = (GPAPIActivity) requireActivity();

        if (isStartedForReconfiguration) {
            Toast.makeText(gpapiActivity, R.string.gp_api_configuration_is_reinitialized, Toast.LENGTH_LONG).show();
            close();
        } else {
            Toast.makeText(gpapiActivity, R.string.gp_api_configuration_is_initialized, Toast.LENGTH_LONG).show();
            gpapiActivity.onGPAPIConfigurationDone();
        }
    }

    private void saveConfiguration(String appId,
                                   String appKey,
                                   String serviceUrl,
                                   String challengeNotificationUrl,
                                   String methodNotificationUrl,
                                   String apiVersion,
                                   Integer tokenSecondsToExpire,
                                   IntervalToExpire tokenIntervalToExpire,
                                   Environment environment,
                                   String selectedCountry,
                                   Channel channel) {

        GPAPIConfiguration gpapiConfiguration = new GPAPIConfiguration();
        gpapiConfiguration.setAppId(appId);
        gpapiConfiguration.setAppKey(appKey);
        gpapiConfiguration.setServiceUrl(serviceUrl);
        gpapiConfiguration.setChallengeNotificationUrl(challengeNotificationUrl);
        gpapiConfiguration.setMethodNotificationUrl(methodNotificationUrl);
        gpapiConfiguration.setApiVersion(apiVersion);
        gpapiConfiguration.setTokenSecondsToExpire(tokenSecondsToExpire);
        gpapiConfiguration.setTokenIntervalToExpire(tokenIntervalToExpire);
        gpapiConfiguration.setEnvironment(environment);
        gpapiConfiguration.setSelectedCountry(selectedCountry);
        gpapiConfiguration.setChannel(channel);

        appPreferences.saveGPAPIConfiguration(gpapiConfiguration);
    }

    private void initGPAPIConfiguration() {
        GPAPIConfiguration gpapiConfiguration = appPreferences.getGPAPIConfiguration();
        initializeDefaultGPAPIConfiguration(gpapiConfiguration);
    }

    private void resetErrors() {
        appIdTextInputLayout.setError(EMPTY);
        appKeyTextInputLayout.setError(EMPTY);
        serviceUrlTextInputLayout.setError(EMPTY);
        apiVersionTextInputLayout.setError(EMPTY);
        tokenSecondsToExpireTextInputLayout.setError(EMPTY);
    }

    private boolean areAllInputValuesValid(String appId,
                                           String appKey,
                                           String serviceUrl,
                                           String apiVersion,
                                           Integer tokenSecondsToExpire) {

        boolean areAllValuesValid = true;

        if (isNullOrBlank(appId)) {
            areAllValuesValid = false;
            appIdTextInputLayout.setError(emptyRequiredField);
        }

        if (isNullOrBlank(appKey)) {
            areAllValuesValid = false;
            appKeyTextInputLayout.setError(emptyRequiredField);
        }

        if (isNotNullOrBlank(serviceUrl) && !isValidUrl(serviceUrl)) {
            areAllValuesValid = false;
            serviceUrlTextInputLayout.setError(invalidServiceUrl);
        }

        if (isNotNullOrBlank(apiVersion) && !isValidDate(YYYY_MM_DD, apiVersion)) {
            areAllValuesValid = false;
            apiVersionTextInputLayout.setError(invalidDateFormat);
        }

        if (isNull(tokenSecondsToExpire) || tokenSecondsToExpire <= 60) {
            areAllValuesValid = false;
            tokenSecondsToExpireTextInputLayout.setError(invalidSecondsToExpire);
        }

        return areAllValuesValid;
    }

}

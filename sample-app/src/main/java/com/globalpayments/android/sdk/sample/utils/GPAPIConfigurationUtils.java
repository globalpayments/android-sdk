package com.globalpayments.android.sdk.sample.utils;

import android.util.Log;

import com.global.api.ServicesContainer;
import com.global.api.entities.exceptions.ConfigurationException;
import com.global.api.gateways.GpApiConnector;
import com.global.api.serviceConfigs.GpApiConfig;
import com.globalpayments.android.sdk.sample.gpapi.configuration.GPAPIConfiguration;

import java.util.HashMap;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;
import static com.globalpayments.android.sdk.utils.Utils.isNotNull;
import static com.globalpayments.android.sdk.utils.Utils.isNotNullOrBlank;

public class GPAPIConfigurationUtils {
    private final static String TAG = GPAPIConfigurationUtils.class.getSimpleName();

    public static void initializeDefaultGPAPIConfiguration(GPAPIConfiguration gpapiConfiguration) {
        GpApiConfig gpApiConfig = buildDefaultGpApiConfig(gpapiConfiguration);
        configureService(gpApiConfig, gpapiConfiguration, DEFAULT_GPAPI_CONFIG);
    }

    public static boolean configureService(GpApiConfig gpApiConfig,
                                           GPAPIConfiguration gpapiConfiguration,
                                           String configName) {
        boolean isSuccessful;
        try {
            ServicesContainer.configureService(gpApiConfig, configName);
            setApiVersion(gpapiConfiguration.getApiVersion(), configName);
            isSuccessful = true;
        } catch (ConfigurationException e) {
            Log.d(TAG, "ServicesContainer.configureService failed");
            isSuccessful = false;
        }

        return isSuccessful;
    }

    private static void setApiVersion(String apiVersion, String configName) {
        if (isNotNullOrBlank(apiVersion)) {
            try {
                GpApiConnector gpApiConnector = (GpApiConnector) ServicesContainer.getInstance().getGateway(configName);
                HashMap<String, String> headers = gpApiConnector.getHeaders();
                headers.put("X-GP-Version", apiVersion);
                gpApiConnector.setHeaders(headers);
            } catch (Exception e) {
                Log.d(TAG, "setApiVersion failed");
            }
        }
    }

    public static GpApiConfig buildDefaultGpApiConfig(GPAPIConfiguration gpapiConfiguration) {
        GpApiConfig gpApiConfig = new GpApiConfig();

        gpApiConfig.setAppId(gpapiConfiguration.getAppId());
        gpApiConfig.setAppKey(gpapiConfiguration.getAppKey());

        String serviceUrl = gpapiConfiguration.getServiceUrl();
        if (isNotNullOrBlank(serviceUrl)) {
            gpApiConfig.setServiceUrl(serviceUrl);
        }

        Integer tokenSecondsToExpire = gpapiConfiguration.getTokenSecondsToExpire();
        if (isNotNull(tokenSecondsToExpire) && tokenSecondsToExpire > 60) {
            gpApiConfig.setSecondsToExpire(tokenSecondsToExpire);
        }

        gpApiConfig.setIntervalToExpire(gpapiConfiguration.getTokenIntervalToExpire());
        gpApiConfig.setEnvironment(gpapiConfiguration.getEnvironment());
        gpApiConfig.setEnableLogging(true);

        return gpApiConfig;
    }
}

package com.globalpayments.android.sdk.sample.utils;

import static com.globalpayments.android.sdk.sample.common.Constants.DCC_RATE_GP_API_CONFIG_NAME;
import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;
import static com.globalpayments.android.sdk.utils.Utils.isNotNull;
import static com.globalpayments.android.sdk.utils.Utils.isNotNullOrBlank;

import android.util.Log;

import com.global.api.ServicesContainer;
import com.global.api.entities.enums.Channel;
import com.global.api.entities.enums.Environment;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.exceptions.ConfigurationException;
import com.global.api.entities.gpApi.entities.AccessTokenInfo;
import com.global.api.gateways.GpApiConnector;
import com.global.api.serviceConfigs.GpApiConfig;
import com.globalpayments.android.sdk.sample.BuildConfig;
import com.globalpayments.android.sdk.sample.gpapi.configuration.GPAPIConfiguration;

import java.util.HashMap;

public class GPAPIConfigurationUtils {
    private final static String TAG = GPAPIConfigurationUtils.class.getSimpleName();

    public static void initializeDefaultGPAPIConfiguration(GPAPIConfiguration gpapiConfiguration) {
        GpApiConfig gpApiConfig = buildDefaultGpApiConfig(gpapiConfiguration);
        configureService(gpApiConfig, gpapiConfiguration, DEFAULT_GPAPI_CONFIG);
    }

    public static void initializeDCCRateGPAPIConfiguration() {
        GpApiConfig config = new GpApiConfig();

        // GP-API settings
        config
                .setAppId(BuildConfig.APP_ID_FOR_DCC)
                .setAppKey(BuildConfig.APP_KEY_FOR_DCC)
                .setCountry("GB")
                .setEnvironment(Environment.TEST);
        config.setChannel(Channel.CardNotPresent.getValue());

        config
                .setAccessTokenInfo(new AccessTokenInfo().setTransactionProcessingAccountName("dcc"))
                .setEnableLogging(true);
        try {
            ServicesContainer.configureService(config, DCC_RATE_GP_API_CONFIG_NAME);
        } catch (ApiException exception) {
            Log.e(TAG, exception.getLocalizedMessage());
        }
    }

    public static boolean configureService(GpApiConfig gpApiConfig,
                                           GPAPIConfiguration gpapiConfiguration,
                                           String configName) {
        boolean isSuccessful;
        try {
            gpApiConfig.setAndroid(true);
            ServicesContainer.configureService(gpApiConfig, configName);
            setApiVersion(
                    gpapiConfiguration.getApiVersion(),
                    configName
            );
            isSuccessful = true;
        } catch (ConfigurationException e) {
            Log.d(TAG, "ServicesContainer.configureService failed");
            isSuccessful = false;
        }

        return isSuccessful;
    }

    private static void setApiVersion(
            String apiVersion,
            String configName
    ) {
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
        HashMap<String, String> androidHeader = new HashMap<>();
        androidHeader.put("x-gp-library", "java;version=" + com.globalpayments.android.sdk.BuildConfig.JAVA_VERSION);
        androidHeader.put("x-gp-sdk", "Android;version=" + BuildConfig.VERSION_NAME);

        String merchantId = gpapiConfiguration.getMerchantId();
        if (isNotNullOrBlank(merchantId)) {
            gpApiConfig.setMerchantId(merchantId);
            AccessTokenInfo accessTokenInfo = new AccessTokenInfo();
            accessTokenInfo.setTransactionProcessingAccountName(gpapiConfiguration.getTransactionProcessingAccountName());
            accessTokenInfo.setTokenizationAccountName(gpapiConfiguration.getTokenizationAccountName());
            gpApiConfig.setAccessTokenInfo(accessTokenInfo);
        }
        gpApiConfig.setAppId(gpapiConfiguration.getAppId());
        gpApiConfig.setAppKey(gpapiConfiguration.getAppKey());
        gpApiConfig.setChannel(gpapiConfiguration.getChannel().getValue());
        gpApiConfig.setMerchantId(gpapiConfiguration.getMerchantId());
        gpApiConfig.setDynamicHeaders(androidHeader);

        gpApiConfig.setMerchantId(gpapiConfiguration.getMerchantId());
        gpApiConfig.setChallengeNotificationUrl("https://enp4qhvjseljg.x.pipedream.net/");
        gpApiConfig.setMethodNotificationUrl("https://enp4qhvjseljg.x.pipedream.net/");
        gpApiConfig.setMerchantContactUrl("https://enp4qhvjseljg.x.pipedream.net/");

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

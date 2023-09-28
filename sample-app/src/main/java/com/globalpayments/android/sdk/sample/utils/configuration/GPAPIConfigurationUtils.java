package com.globalpayments.android.sdk.sample.utils.configuration;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;
import static com.globalpayments.android.sdk.utils.Utils.isNotNull;
import static com.globalpayments.android.sdk.utils.Utils.isNotNullOrBlank;

import android.util.Log;

import com.global.api.ServicesContainer;
import com.global.api.entities.exceptions.ConfigurationException;
import com.global.api.entities.gpApi.entities.AccessTokenInfo;
import com.global.api.gateways.GpApiConnector;
import com.global.api.serviceConfigs.GpApiConfig;
import com.globalpayments.android.sdk.sample.BuildConfig;
import com.globalpayments.android.sdk.utils.AndroidSampleLogger;

import java.util.HashMap;

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
        }
        gpApiConfig.setAccessTokenInfo(createAccessTokenInfo(gpapiConfiguration));
        gpApiConfig.setAppId(gpapiConfiguration.getAppId());
        gpApiConfig.setAppKey(gpapiConfiguration.getAppKey());
        gpApiConfig.setChannel(gpapiConfiguration.getChannel());
        gpApiConfig.setDynamicHeaders(androidHeader);

        gpApiConfig.setCountry(gpapiConfiguration.getSelectedCountry());
        String notificationUrl = gpapiConfiguration.getChallengeNotificationUrl();
        if (isNotNullOrBlank(notificationUrl)) {
            gpApiConfig.setChallengeNotificationUrl(notificationUrl);
            gpApiConfig.setMethodNotificationUrl(notificationUrl);
            gpApiConfig.setMerchantContactUrl(notificationUrl);
        }
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
        gpApiConfig.setRequestLogger(new AndroidSampleLogger());

        return gpApiConfig;
    }

    private static AccessTokenInfo createAccessTokenInfo(GPAPIConfiguration gpapiConfiguration) {
        AccessTokenInfo accessTokenInfo = new AccessTokenInfo();
        if (isNotNullOrBlank(gpapiConfiguration.getTransactionProcessingAccountName())) {
            accessTokenInfo.setTransactionProcessingAccountName(gpapiConfiguration.getTransactionProcessingAccountName());
        }
        if (isNotNullOrBlank(gpapiConfiguration.getTokenizationAccountName())) {
            accessTokenInfo.setTokenizationAccountName(gpapiConfiguration.getTokenizationAccountName());
        }
        if (isNotNullOrBlank(gpapiConfiguration.getTransactionProcessingAccountId())) {
            accessTokenInfo.setTransactionProcessingAccountID(gpapiConfiguration.getTransactionProcessingAccountId());
        }
        if (isNotNullOrBlank(gpapiConfiguration.getDataAccountId())) {
            accessTokenInfo.setDataAccountID(gpapiConfiguration.getDataAccountId());
        }
        if (isNotNullOrBlank(gpapiConfiguration.getTokenizationAccountId())) {
            accessTokenInfo.setTokenizationAccountID(gpapiConfiguration.getTokenizationAccountId());
        }
        if (isNotNullOrBlank(gpapiConfiguration.getDisputeManagementAccountId())) {
            accessTokenInfo.setDisputeManagementAccountID(gpapiConfiguration.getDisputeManagementAccountId());
        }
        if (isNotNullOrBlank(gpapiConfiguration.getRiskAssessmentAccountId())) {
            accessTokenInfo.setRiskAssessmentAccountID(gpapiConfiguration.getRiskAssessmentAccountId());
        }
        return accessTokenInfo;
    }
}

package com.globalpayments.android.sdk.sample.utils.configuration;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPECOM_CONFIG;
import static com.globalpayments.android.sdk.utils.Utils.isNotNullOrBlank;

import com.global.api.ServicesContainer;
import com.global.api.entities.exceptions.ConfigurationException;
import com.global.api.serviceConfigs.GpEcomConfig;
import com.globalpayments.android.sdk.utils.AndroidSampleLogger;

import java.util.Objects;

public class GPEcomConfigurationUtils {
    private final static String TAG = GPEcomConfigurationUtils.class.getSimpleName();

    public static void initializeDefaultGPEcomConfiguration(GPEcomConfiguration gpecomConfiguration) {
        GpEcomConfig gpEcomConfig = buildDefaultGpEcomConfig(gpecomConfiguration);
        configureService(gpEcomConfig, gpecomConfiguration, DEFAULT_GPECOM_CONFIG);
    }

    public static boolean configureService(GpEcomConfig gpEcomConfig,
                                           GPEcomConfiguration gpEcomConfiguration,
                                           String configName) {
        boolean isSuccessful;
        try {
            ServicesContainer.configureService(gpEcomConfig, configName);
            isSuccessful = true;
        } catch (ConfigurationException e) {
            isSuccessful = false;
        }

        return isSuccessful;
    }

    public static GpEcomConfig buildDefaultGpEcomConfig(GPEcomConfiguration gpEcomConfiguration) {
        GpEcomConfig gpEcomConfig = new GpEcomConfig();

        String accountId = gpEcomConfiguration.getAccountId();
        if (isNotNullOrBlank(accountId)) {
            gpEcomConfig.setAccountId(accountId);
        }
        gpEcomConfig.setChannel(Objects.requireNonNull(gpEcomConfiguration.getChannel()).toString());

        String merchantId = gpEcomConfiguration.getMerchantId();
        if (isNotNullOrBlank(merchantId)) {
            gpEcomConfig.setMerchantId(merchantId);
        }
        gpEcomConfig.setRebatePassword(gpEcomConfiguration.getRebatePassword());
        gpEcomConfig.setRefundPassword(gpEcomConfiguration.getRefundPassword());
        gpEcomConfig.setSharedSecret(gpEcomConfiguration.getSharedSecret());
        gpEcomConfig.setShaHashType(gpEcomConfiguration.getShaHashType());
        gpEcomConfig.setRequestLogger(new AndroidSampleLogger());

        return gpEcomConfig;
    }
}

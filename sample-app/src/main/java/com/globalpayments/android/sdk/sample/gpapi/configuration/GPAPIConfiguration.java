package com.globalpayments.android.sdk.sample.gpapi.configuration;

import com.global.api.entities.enums.Environment;
import com.global.api.entities.enums.IntervalToExpire;

public class GPAPIConfiguration {
    private String appId;
    private String appKey;
    private String serviceUrl;
    private String apiVersion;
    private Integer tokenSecondsToExpire;
    private IntervalToExpire tokenIntervalToExpire;
    private Environment environment;
    private String selectedCountry;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Integer getTokenSecondsToExpire() {
        return tokenSecondsToExpire;
    }

    public void setTokenSecondsToExpire(Integer tokenSecondsToExpire) {
        this.tokenSecondsToExpire = tokenSecondsToExpire;
    }

    public IntervalToExpire getTokenIntervalToExpire() {
        return tokenIntervalToExpire;
    }

    public void setTokenIntervalToExpire(IntervalToExpire tokenIntervalToExpire) {
        this.tokenIntervalToExpire = tokenIntervalToExpire;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getSelectedCountry() {
        return selectedCountry;
    }

    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

}

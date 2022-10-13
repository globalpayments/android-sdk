package com.globalpayments.android.sdk.sample.gpapi.configuration;

import com.global.api.entities.enums.Channel;
import com.global.api.entities.enums.Environment;
import com.global.api.entities.enums.IntervalToExpire;

public class GPAPIConfiguration {
    private String merchantId;
    private String transactionProcessingAccountName;
    private String tokenizationAccountName;
    private String appId;
    private String appKey;
    private String serviceUrl;
    private String challengeNotificationUrl;
    private String methodNotificationUrl;
    private String apiVersion;
    private Integer tokenSecondsToExpire;
    private Channel channel;
    private IntervalToExpire tokenIntervalToExpire;
    private Environment environment;
    private String selectedCountry;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

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

    public String getChallengeNotificationUrl() {
        return challengeNotificationUrl;
    }

    public void setChallengeNotificationUrl(String challengeNotificationUrl) {
        this.challengeNotificationUrl = challengeNotificationUrl;
    }

    public String getMethodNotificationUrl() {
        return methodNotificationUrl;
    }

    public void setMethodNotificationUrl(String methodNotificationUrl) {
        this.methodNotificationUrl = methodNotificationUrl;
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

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getTransactionProcessingAccountName() {
        return transactionProcessingAccountName;
    }

    public void setTransactionProcessingAccountName(String transactionProcessingAccountName) {
        this.transactionProcessingAccountName = transactionProcessingAccountName;
    }

    public String getTokenizationAccountName() {
        return tokenizationAccountName;
    }

    public void setTokenizationAccountName(String tokenizationAccountName) {
        this.tokenizationAccountName = tokenizationAccountName;
    }
}

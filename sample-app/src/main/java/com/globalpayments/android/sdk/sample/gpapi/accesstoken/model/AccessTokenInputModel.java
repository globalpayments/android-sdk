package com.globalpayments.android.sdk.sample.gpapi.accesstoken.model;

import com.global.api.entities.enums.Environment;
import com.global.api.entities.enums.IntervalToExpire;

public class AccessTokenInputModel {
    private String appId;
    private String appKey;
    private int secondsToExpire;
    private Environment environment;
    private IntervalToExpire intervalToExpire;

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

    public int getSecondsToExpire() {
        return secondsToExpire;
    }

    public void setSecondsToExpire(int secondsToExpire) {
        this.secondsToExpire = secondsToExpire;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public IntervalToExpire getIntervalToExpire() {
        return intervalToExpire;
    }

    public void setIntervalToExpire(IntervalToExpire intervalToExpire) {
        this.intervalToExpire = intervalToExpire;
    }
}

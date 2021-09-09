package com.globalpayments.android.sdk.sample.gpapi.accesstoken;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.entities.enums.Environment;
import com.global.api.entities.enums.IntervalToExpire;
import com.global.api.entities.exceptions.GatewayException;
import com.global.api.entities.gpApi.entities.AccessTokenInfo;
import com.global.api.serviceConfigs.GpApiConfig;
import com.global.api.services.GpApiService;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel;

public class AccessTokenViewModel extends BaseViewModel {
    private MutableLiveData<AccessTokenInfo> accessTokenInfoLiveData = new MutableLiveData<>();

    public LiveData<AccessTokenInfo> getAccessTokenInfoLiveData() {
        return accessTokenInfoLiveData;
    }

    private void showResult(AccessTokenInfo accessTokenInfo) {
        hideProgress();
        accessTokenInfoLiveData.setValue(accessTokenInfo);
    }

    public void getAccessTokenInfo(String appId,
                                   String appKey,
                                   Environment environment,
                                   int secondsToExpire,
                                   IntervalToExpire intervalToExpire,
                                   String[] permissions) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<AccessTokenInfo>() {
            @Override
            public AccessTokenInfo executeAsync() throws Exception {
                return executeAccessTokenRequest(appId,
                        appKey,
                        environment,
                        secondsToExpire,
                        intervalToExpire,
                        permissions);
            }

            @Override
            public void onSuccess(AccessTokenInfo value) {
                showResult(value);
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    private AccessTokenInfo executeAccessTokenRequest(String appId,
                                                      String appKey,
                                                      Environment environment,
                                                      int secondsToExpire,
                                                      IntervalToExpire intervalToExpire,
                                                      String[] permissions) throws GatewayException {
        GpApiConfig gpApiConfig = new GpApiConfig();
        gpApiConfig.setAppId(appId);
        gpApiConfig.setAppKey(appKey);

        gpApiConfig.setEnvironment(environment);
        gpApiConfig.setSecondsToExpire(secondsToExpire);
        gpApiConfig.setIntervalToExpire(intervalToExpire);
        if (permissions.length > 0) {
            gpApiConfig.setPermissions(permissions);
        }
        return GpApiService.generateTransactionKey(gpApiConfig);
    }
}
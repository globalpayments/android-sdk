package com.globalpayments.android.sdk.sample.gpapi.accesstoken;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.entities.exceptions.GatewayException;
import com.global.api.entities.gpApi.entities.AccessTokenInfo;
import com.global.api.serviceConfigs.GpApiConfig;
import com.global.api.services.GpApiService;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel;
import com.globalpayments.android.sdk.sample.gpapi.accesstoken.model.AccessTokenInputModel;

public class AccessTokenViewModel extends BaseViewModel {
    private MutableLiveData<AccessTokenInfo> accessTokenInfoLiveData = new MutableLiveData<>();

    public LiveData<AccessTokenInfo> getAccessTokenInfoLiveData() {
        return accessTokenInfoLiveData;
    }

    private void showResult(AccessTokenInfo accessTokenInfo) {
        hideProgress();
        accessTokenInfoLiveData.setValue(accessTokenInfo);
    }

    public void getAccessTokenInfo(AccessTokenInputModel accessTokenInputModel) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<AccessTokenInfo>() {
            @Override
            public AccessTokenInfo executeAsync() throws Exception {
                return executeAccessTokenRequest(accessTokenInputModel);
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

    private AccessTokenInfo executeAccessTokenRequest(AccessTokenInputModel accessTokenInputModel) throws GatewayException {
        GpApiConfig gpApiConfig = new GpApiConfig();
        gpApiConfig.setAppId(accessTokenInputModel.getAppId());
        gpApiConfig.setAppKey(accessTokenInputModel.getAppKey());
        gpApiConfig.setEnvironment(accessTokenInputModel.getEnvironment());
        gpApiConfig.setSecondsToExpire(accessTokenInputModel.getSecondsToExpire());
        gpApiConfig.setIntervalToExpire(accessTokenInputModel.getIntervalToExpire());
        return GpApiService.generateTransactionKey(gpApiConfig);
    }
}
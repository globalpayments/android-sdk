package com.globalpayments.android.sdk.sample.gpapi.batch.closeBatch;

import static com.globalpayments.android.sdk.sample.common.Constants.GP_API_CONFIG_NAME;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.global.api.ServicesContainer;
import com.global.api.entities.BatchSummary;
import com.global.api.serviceConfigs.GpApiConfig;
import com.global.api.services.BatchService;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.common.base.BaseAndroidViewModel;
import com.globalpayments.android.sdk.sample.gpapi.configuration.GPAPIConfiguration;
import com.globalpayments.android.sdk.sample.utils.AppPreferences;
import java.util.Collections;
import java.util.List;

public class CloseViewModel extends BaseAndroidViewModel {
    private final MutableLiveData<List<BatchSummary>> closeBatchLiveData = new MutableLiveData<>();

    public CloseViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<BatchSummary>> getCloseBatchLiveData() {
        return closeBatchLiveData;
    }

    public void getCloseBatchById(String batchId) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<BatchSummary>() {
            @Override
            public BatchSummary executeAsync() throws Exception {
                return executeGetCloseBatchByIdRequest(batchId);
            }

            @Override
            public void onSuccess(BatchSummary value) {
                showResult(Collections.singletonList(value));
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    private void showResult(List<BatchSummary> batchSummaries) {
        if (batchSummaries == null || batchSummaries.isEmpty()) {
            showError(new Exception("Empty Actions List"));
        } else {
            hideProgress();
            closeBatchLiveData.setValue(batchSummaries);
        }
    }

    private BatchSummary executeGetCloseBatchByIdRequest(String batchId) throws Exception {
        GPAPIConfiguration gpapiConfiguration = new AppPreferences(getApplication()).getGPAPIConfiguration();
        GpApiConfig config = new GpApiConfig();

        // GP-API settings
        config
                // These credentials have permissions for executing BATCH
                .setAppId(gpapiConfiguration.getAppId())
                .setAppKey(gpapiConfiguration.getAppKey())
                .setChannel(gpapiConfiguration.getChannel().getValue());

        config.setEnableLogging(true);

        ServicesContainer.configureService(config, GP_API_CONFIG_NAME);

        Thread.sleep(1000);

        return BatchService.closeBatch(batchId, GP_API_CONFIG_NAME);
    }
}

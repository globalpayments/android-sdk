package com.globalpayments.android.sdk.sample.common.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.globalpayments.android.sdk.sample.common.ActionLiveData;

import static com.globalpayments.android.sdk.utils.Utils.getExceptionDescription;

public abstract class BaseViewModel extends ViewModel {
    private MutableLiveData<Boolean> progressStatus = new MutableLiveData<>();
    private ActionLiveData<String> error = new ActionLiveData<>();

    public LiveData<Boolean> getProgressStatus() {
        return progressStatus;
    }

    public LiveData<String> getError() {
        return error;
    }

    protected void init() {
        // No Op
    }

    public void showProgress() {
        progressStatus.setValue(true);
    }

    public void hideProgress() {
        progressStatus.setValue(false);
    }

    public void showError(String errorMessage) {
        hideProgress();
        error.setValue(errorMessage);
    }

    public void showError(Exception exception) {
        showError(getExceptionDescription(exception));
    }

    public String getLogTag() {
        return getClass().getSimpleName();
    }
}
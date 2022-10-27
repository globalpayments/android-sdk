package com.globalpayments.android.sdk.sample.common.base;

import static com.globalpayments.android.sdk.utils.Utils.getExceptionDescription;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.globalpayments.android.sdk.sample.common.ActionLiveData;

public abstract class BaseViewModel extends ViewModel {
    protected MutableLiveData<Boolean> progressStatus = new MutableLiveData<>();
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
        progressStatus.postValue(true);
    }

    public void hideProgress() {
        progressStatus.postValue(false);
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
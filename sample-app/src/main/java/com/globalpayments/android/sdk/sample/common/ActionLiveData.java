package com.globalpayments.android.sdk.sample.common;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class ActionLiveData<T> extends MutableLiveData<T> {
    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, data -> {
            if (data != null) {
                observer.onChanged(data);
                setValue(null);
            }
        });
    }
}
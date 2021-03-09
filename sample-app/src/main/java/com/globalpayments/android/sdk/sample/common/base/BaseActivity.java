package com.globalpayments.android.sdk.sample.common.base;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public abstract class BaseActivity extends AppCompatActivity {

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initDependencies();
        initViews();
    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected void initViews() {
        // No op
    }

    protected void initDependencies() {
        // No op
    }

    protected void showFragment(@IdRes int containerViewId, @NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }
}
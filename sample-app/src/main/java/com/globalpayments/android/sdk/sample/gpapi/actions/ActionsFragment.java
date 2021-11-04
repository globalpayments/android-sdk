package com.globalpayments.android.sdk.sample.gpapi.actions;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;

public class ActionsFragment extends BaseFragment {


    @Override
    protected int getLayoutId() {
        return R.layout.actions_fragment;
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.actions_report);
        customToolbar.setOnBackButtonListener(v -> close());

    }
}

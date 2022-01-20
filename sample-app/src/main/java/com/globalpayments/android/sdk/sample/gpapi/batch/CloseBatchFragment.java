package com.globalpayments.android.sdk.sample.gpapi.batch;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;

public class CloseBatchFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_close_batch;
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.close_batch);
        customToolbar.setOnBackButtonListener(v -> close());

    }
}

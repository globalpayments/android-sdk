package com.globalpayments.android.sdk.sample.gpapi.deposits;

import android.view.View;

import com.global.api.entities.reporting.DepositSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseAdapter;

public class DepositsAdapter extends BaseAdapter<DepositSummary, DepositsViewHolder> {
    private boolean isExpandedByDefault;

    public boolean isExpandedByDefault() {
        return isExpandedByDefault;
    }

    public void setExpandedByDefault(boolean expandedByDefault) {
        isExpandedByDefault = expandedByDefault;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_deposit;
    }

    @Override
    protected DepositsViewHolder getViewHolder(View view) {
        return new DepositsViewHolder(view, isExpandedByDefault);
    }
}

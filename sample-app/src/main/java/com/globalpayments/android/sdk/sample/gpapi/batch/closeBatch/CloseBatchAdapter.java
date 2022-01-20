package com.globalpayments.android.sdk.sample.gpapi.batch.closeBatch;

import android.view.View;
import com.global.api.entities.BatchSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseAdapter;

public class CloseBatchAdapter extends BaseAdapter<BatchSummary, CloseBatchViewHolder> {
    private boolean isExpandedByDefault;

    public boolean isExpandedByDefault() {
        return isExpandedByDefault;
    }

    public void setExpandedByDefault(boolean expandedByDefault) {
        isExpandedByDefault = expandedByDefault;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_close_batch;
    }

    @Override
    protected CloseBatchViewHolder getViewHolder(View view) {
        return new CloseBatchViewHolder(view, isExpandedByDefault);
    }
}

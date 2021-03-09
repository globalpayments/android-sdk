package com.globalpayments.android.sdk.sample.gpapi.disputes.report;

import android.view.View;

import com.global.api.entities.reporting.DisputeSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseAdapter;

public class DisputesReportAdapter extends BaseAdapter<DisputeSummary, DisputesReportViewHolder> {
    private boolean isExpandedByDefault;

    public boolean isExpandedByDefault() {
        return isExpandedByDefault;
    }

    public void setExpandedByDefault(boolean expandedByDefault) {
        isExpandedByDefault = expandedByDefault;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_dispute_report;
    }

    @Override
    protected DisputesReportViewHolder getViewHolder(View view) {
        return new DisputesReportViewHolder(view, isExpandedByDefault);
    }
}

package com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport;

import android.view.View;

import com.global.api.entities.reporting.ActionSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseAdapter;

public class ActionsReportAdapter extends BaseAdapter<ActionSummary, ActionsReportViewHolder> {
    private boolean isExpandedByDefault;

    public boolean isExpandedByDefault() {
        return isExpandedByDefault;
    }

    public void setExpandedByDefault(boolean expandedByDefault) {
        isExpandedByDefault = expandedByDefault;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_report_list;
    }

    @Override
    protected ActionsReportViewHolder getViewHolder(View view) {
        return new ActionsReportViewHolder(view, isExpandedByDefault);
    }
}

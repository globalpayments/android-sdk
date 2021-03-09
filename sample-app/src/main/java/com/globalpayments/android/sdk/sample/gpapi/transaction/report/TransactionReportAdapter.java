package com.globalpayments.android.sdk.sample.gpapi.transaction.report;

import android.view.View;

import com.global.api.entities.TransactionSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseAdapter;

public class TransactionReportAdapter extends BaseAdapter<TransactionSummary, TransactionReportViewHolder> {
    private boolean isExpandedByDefault;

    public boolean isExpandedByDefault() {
        return isExpandedByDefault;
    }

    public void setExpandedByDefault(boolean expandedByDefault) {
        isExpandedByDefault = expandedByDefault;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_transaction_report;
    }

    @Override
    protected TransactionReportViewHolder getViewHolder(View view) {
        return new TransactionReportViewHolder(view, isExpandedByDefault);
    }
}

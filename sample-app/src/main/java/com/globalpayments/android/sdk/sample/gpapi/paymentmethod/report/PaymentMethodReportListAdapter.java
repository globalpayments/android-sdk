package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import android.view.View;

import com.global.api.entities.reporting.StoredPaymentMethodSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseAdapter;

public class PaymentMethodReportListAdapter extends BaseAdapter<StoredPaymentMethodSummary, PaymentMethodReportListViewHolder> {

    @Override
    protected int getLayoutId() {
        return R.layout.item_payment_method_report_list;
    }

    @Override
    protected PaymentMethodReportListViewHolder getViewHolder(View view) {
        return new PaymentMethodReportListViewHolder(view);
    }
}

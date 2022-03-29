package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import android.view.View;

import com.global.api.entities.reporting.StoredPaymentMethodSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseAdapter;

public class PaymentMethodReportByIdAdapter extends BaseAdapter<StoredPaymentMethodSummary, PaymentMethodReportByIdViewHolder> {

    @Override
    protected int getLayoutId() {
        return R.layout.item_payment_method_report_by_id;
    }

    @Override
    protected PaymentMethodReportByIdViewHolder getViewHolder(View view) {
        return new PaymentMethodReportByIdViewHolder(view);
    }
}

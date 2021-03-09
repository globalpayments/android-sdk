package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import android.view.View;

import com.global.api.entities.Transaction;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseAdapter;

public class PaymentMethodReportAdapter extends BaseAdapter<Transaction, PaymentMethodReportViewHolder> {

    @Override
    protected int getLayoutId() {
        return R.layout.item_payment_method_report;
    }

    @Override
    protected PaymentMethodReportViewHolder getViewHolder(View view) {
        return new PaymentMethodReportViewHolder(view);
    }
}

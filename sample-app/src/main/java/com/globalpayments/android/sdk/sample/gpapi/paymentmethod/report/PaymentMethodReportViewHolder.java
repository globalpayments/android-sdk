package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import android.view.View;

import androidx.annotation.NonNull;

import com.global.api.entities.Transaction;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseViewHolder;

public class PaymentMethodReportViewHolder extends BaseViewHolder<Transaction> {
    private PaymentMethodView paymentMethodView;

    public PaymentMethodReportViewHolder(@NonNull View itemView) {
        super(itemView);
        initViews(itemView);
    }

    private void initViews(View itemView) {
        paymentMethodView = itemView.findViewById(R.id.paymentMethodView);
    }

    @Override
    public void bind(Transaction transaction) {
        paymentMethodView.bind(transaction);
    }
}
package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import android.view.View;

import androidx.annotation.NonNull;

import com.global.api.entities.reporting.StoredPaymentMethodSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseViewHolder;

public class PaymentMethodReportByIdViewHolder extends BaseViewHolder<StoredPaymentMethodSummary> {
    private PaymentMethodViewById paymentMethodViewById;

    public PaymentMethodReportByIdViewHolder(@NonNull View itemView) {
        super(itemView);
        initViews(itemView);
    }

    private void initViews(View itemView) {
        paymentMethodViewById = itemView.findViewById(R.id.paymentMethodViewById);
    }

    @Override
    public void bind(StoredPaymentMethodSummary transaction) {
        paymentMethodViewById.bind(transaction);
    }
}
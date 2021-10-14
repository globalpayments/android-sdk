package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import android.view.View;

import androidx.annotation.NonNull;

import com.global.api.entities.reporting.StoredPaymentMethodSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseViewHolder;

public class PaymentMethodReportListViewHolder extends BaseViewHolder<StoredPaymentMethodSummary> {
    private PaymentMethodViewList paymentMethodViewList;

    public PaymentMethodReportListViewHolder(@NonNull View itemView) {
        super(itemView);
        initViews(itemView);
    }

    private void initViews(View itemView) {
        paymentMethodViewList = itemView.findViewById(R.id.paymentMethodViewList);
    }

    @Override
    public void bind(StoredPaymentMethodSummary storedPaymentMethodSummary) {
        paymentMethodViewList.bind(storedPaymentMethodSummary);
    }
}
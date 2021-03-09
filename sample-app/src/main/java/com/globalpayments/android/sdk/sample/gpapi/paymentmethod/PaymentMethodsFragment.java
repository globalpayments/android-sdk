package com.globalpayments.android.sdk.sample.gpapi.paymentmethod;

import android.widget.Button;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations.PaymentMethodOperationsFragment;
import com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report.PaymentMethodReportFragment;

public class PaymentMethodsFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_payment_methods;
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.payment_methods);
        customToolbar.setOnBackButtonListener(v -> close());

        Button btPaymentMethodReport = findViewById(R.id.btPaymentMethodReport);
        btPaymentMethodReport.setOnClickListener(v ->
                show(R.id.gp_api_fragment_container, new PaymentMethodReportFragment()));

        Button btPaymentMethodOperations = findViewById(R.id.btPaymentMethodOperations);
        btPaymentMethodOperations.setOnClickListener(v ->
                show(R.id.gp_api_fragment_container, new PaymentMethodOperationsFragment()));
    }
}

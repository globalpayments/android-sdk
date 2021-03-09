package com.globalpayments.android.sdk.sample.gpapi.transaction;

import android.widget.Button;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.TransactionOperationsFragment;
import com.globalpayments.android.sdk.sample.gpapi.transaction.report.TransactionReportFragment;

public class TransactionFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_transaction;
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.transactions);
        customToolbar.setOnBackButtonListener(v -> close());

        Button btTransactionsReport = findViewById(R.id.btTransactionsReport);
        btTransactionsReport.setOnClickListener(v ->
                show(R.id.gp_api_fragment_container, new TransactionReportFragment()));

        Button btTransactionOperations = findViewById(R.id.btTransactionOperations);
        btTransactionOperations.setOnClickListener(v ->
                show(R.id.gp_api_fragment_container, new TransactionOperationsFragment()));
    }
}

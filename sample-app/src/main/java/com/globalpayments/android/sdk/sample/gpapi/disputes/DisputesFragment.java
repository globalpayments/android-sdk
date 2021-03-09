package com.globalpayments.android.sdk.sample.gpapi.disputes;

import android.widget.Button;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.disputes.operations.DisputeOperationsFragment;
import com.globalpayments.android.sdk.sample.gpapi.disputes.report.DisputesReportFragment;

public class DisputesFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_disputes;
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.disputes);
        customToolbar.setOnBackButtonListener(v -> close());

        Button btDisputesReport = findViewById(R.id.btDisputesReport);
        btDisputesReport.setOnClickListener(v -> show(R.id.gp_api_fragment_container, new DisputesReportFragment()));

        Button btDisputeOperations = findViewById(R.id.btDisputeOperations);
        btDisputeOperations.setOnClickListener(v -> show(R.id.gp_api_fragment_container, new DisputeOperationsFragment()));
    }
}

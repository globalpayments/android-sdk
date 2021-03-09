package com.globalpayments.android.sdk.sample.gpapi.disputes.report;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.global.api.entities.reporting.DisputeSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseViewHolder;

import static com.globalpayments.android.sdk.sample.common.Constants.INITIAL_DEGREE;
import static com.globalpayments.android.sdk.sample.common.Constants.ROTATED_DEGREE;

public class DisputesReportViewHolder extends BaseViewHolder<DisputeSummary> {
    private DisputeReportView disputeReportView;
    private ImageView arrow;

    private boolean isItemExpanded = false;
    private boolean isExpandedByDefault;

    public DisputesReportViewHolder(@NonNull View itemView, boolean isExpandedByDefault) {
        super(itemView);
        this.isExpandedByDefault = isExpandedByDefault;
        initViews(itemView);
    }

    private void initViews(View itemView) {
        disputeReportView = itemView.findViewById(R.id.disputeReportView);
        arrow = itemView.findViewById(R.id.arrow);
        arrow.setOnClickListener(v -> toggleView());
    }

    @Override
    public void bind(DisputeSummary disputeSummary) {
        handleFoldState(isExpandedByDefault);
        disputeReportView.bind(disputeSummary);
    }

    private void handleFoldState(boolean expand) {
        if (expand) {
            isItemExpanded = true;
            arrow.setRotation(ROTATED_DEGREE);
            disputeReportView.expand();
        } else {
            isItemExpanded = false;
            arrow.setRotation(INITIAL_DEGREE);
            disputeReportView.collapse();
        }
    }

    private void toggleView() {
        handleFoldState(!isItemExpanded);
    }
}
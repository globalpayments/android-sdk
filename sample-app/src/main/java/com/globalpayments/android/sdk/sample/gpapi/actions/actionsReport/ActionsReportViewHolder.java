package com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport;

import static com.globalpayments.android.sdk.sample.common.Constants.INITIAL_DEGREE;
import static com.globalpayments.android.sdk.sample.common.Constants.ROTATED_DEGREE;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.global.api.entities.reporting.ActionSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseViewHolder;

public class ActionsReportViewHolder extends BaseViewHolder<ActionSummary> {
    private ActionsReportView actionsReportView;
    private ImageView arrow;

    private boolean isItemExpanded = false;
    private boolean isExpandedByDefault;

    public ActionsReportViewHolder(@NonNull View itemView, boolean isExpandedByDefault) {
        super(itemView);
        this.isExpandedByDefault = isExpandedByDefault;
        initViews(itemView);
    }

    private void initViews(View itemView) {
        actionsReportView = itemView.findViewById(R.id.reportReportView);
        arrow = itemView.findViewById(R.id.arrow);
        arrow.setOnClickListener(v -> toggleView());
    }

    @Override
    public void bind(ActionSummary actionSummary) {
        handleFoldState(isExpandedByDefault);
        actionsReportView.bind(actionSummary);
    }

    private void handleFoldState(boolean expand) {
        if (expand) {
            isItemExpanded = true;
            arrow.setRotation(ROTATED_DEGREE);
            actionsReportView.expand();
        } else {
            isItemExpanded = false;
            arrow.setRotation(INITIAL_DEGREE);
            actionsReportView.collapse();
        }
    }

    private void toggleView() {
        handleFoldState(!isItemExpanded);
    }
}

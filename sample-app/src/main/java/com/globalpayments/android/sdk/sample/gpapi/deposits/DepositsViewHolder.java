package com.globalpayments.android.sdk.sample.gpapi.deposits;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.global.api.entities.reporting.DepositSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseViewHolder;

import static com.globalpayments.android.sdk.sample.common.Constants.INITIAL_DEGREE;
import static com.globalpayments.android.sdk.sample.common.Constants.ROTATED_DEGREE;

public class DepositsViewHolder extends BaseViewHolder<DepositSummary> {
    private DepositView depositView;
    private ImageView arrow;

    private boolean isItemExpanded = false;
    private boolean isExpandedByDefault;

    public DepositsViewHolder(@NonNull View itemView, boolean isExpandedByDefault) {
        super(itemView);
        this.isExpandedByDefault = isExpandedByDefault;
        initViews(itemView);
    }

    private void initViews(View itemView) {
        depositView = itemView.findViewById(R.id.depositView);
        arrow = itemView.findViewById(R.id.arrow);
        arrow.setOnClickListener(v -> toggleView());
    }

    @Override
    public void bind(DepositSummary depositSummary) {
        handleFoldState(isExpandedByDefault);
        depositView.bind(depositSummary);
    }

    private void handleFoldState(boolean expand) {
        if (expand) {
            isItemExpanded = true;
            arrow.setRotation(ROTATED_DEGREE);
            depositView.expand();
        } else {
            isItemExpanded = false;
            arrow.setRotation(INITIAL_DEGREE);
            depositView.collapse();
        }
    }

    private void toggleView() {
        handleFoldState(!isItemExpanded);
    }
}
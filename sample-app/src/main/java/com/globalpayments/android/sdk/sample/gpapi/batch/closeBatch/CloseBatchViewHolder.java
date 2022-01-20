package com.globalpayments.android.sdk.sample.gpapi.batch.closeBatch;

import static com.globalpayments.android.sdk.sample.common.Constants.INITIAL_DEGREE;
import static com.globalpayments.android.sdk.sample.common.Constants.ROTATED_DEGREE;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.global.api.entities.BatchSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseViewHolder;

public class CloseBatchViewHolder extends BaseViewHolder<BatchSummary> {
    private CloseBatchReportView closeBatchReportView;
    private ImageView arrow;

    private boolean isItemExpanded = false;
    private boolean isExpandedByDefault;

    public CloseBatchViewHolder(@NonNull View itemView, boolean isExpandedByDefault) {
        super(itemView);
        this.isExpandedByDefault = isExpandedByDefault;
        initViews(itemView);
    }

    private void initViews(View itemView) {
        closeBatchReportView = itemView.findViewById(R.id.closeBatchView);
        arrow = itemView.findViewById(R.id.arrow);
        arrow.setOnClickListener(v -> toggleView());
    }

    @Override
    public void bind(BatchSummary batchSummary) {
        handleFoldState(isExpandedByDefault);
        closeBatchReportView.bind(batchSummary);
    }

    private void handleFoldState(boolean expand) {
        if (expand) {
            isItemExpanded = true;
            arrow.setRotation(ROTATED_DEGREE);
            closeBatchReportView.expand();
        } else {
            isItemExpanded = false;
            arrow.setRotation(INITIAL_DEGREE);
            closeBatchReportView.collapse();
        }
    }

    private void toggleView() {
        handleFoldState(!isItemExpanded);
    }
}

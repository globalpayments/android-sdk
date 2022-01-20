package com.globalpayments.android.sdk.sample.gpapi.batch.closeBatch;

import static com.globalpayments.android.sdk.sample.common.views.Position.SECOND;
import static com.globalpayments.android.sdk.sample.common.views.Position.TOP;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.global.api.entities.BatchSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.views.ItemView;

public class CloseBatchReportView extends LinearLayout {
    private ItemView idItemView;
    private ItemView statusItemView;
    private ItemView amountItemView;
    private ItemView transactionCountItemView;

    private LinearLayout expandableContainer;

    public CloseBatchReportView(Context context) {
        this(context, null);
    }

    public CloseBatchReportView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CloseBatchReportView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CloseBatchReportView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);

        expandableContainer = new LinearLayout(context);
        expandableContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        expandableContainer.setOrientation(LinearLayout.VERTICAL);
        collapse();

        idItemView = ItemView.create(context, R.string.close_batch_id, this, TOP);
        statusItemView = ItemView.create(context, R.string.close_batch_status, this, SECOND);
        amountItemView = ItemView.create(context, R.string.close_batch_amount, this);
        transactionCountItemView = ItemView.create(context, R.string.close_batch_transaction_count, this);

        // Expandable group
        addView(expandableContainer);

    }

    public void expand() {
        showView(expandableContainer);
    }

    public void collapse() {
        hideView(expandableContainer);
    }

    public void bind(BatchSummary batchSummary) {
        idItemView.setValue(batchSummary.getBatchReference());
        statusItemView.setValue(batchSummary.getStatus());
        amountItemView.setValue(batchSummary.getTotalAmount().toString());
        transactionCountItemView.setValue(batchSummary.getTransactionCount().toString());
    }

}

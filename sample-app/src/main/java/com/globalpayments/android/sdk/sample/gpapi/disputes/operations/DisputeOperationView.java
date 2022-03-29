package com.globalpayments.android.sdk.sample.gpapi.disputes.operations;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.global.api.entities.Transaction;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.views.ItemView;

import static com.globalpayments.android.sdk.sample.common.views.Position.BOTTOM;
import static com.globalpayments.android.sdk.sample.common.views.Position.SECOND;
import static com.globalpayments.android.sdk.sample.common.views.Position.TOP;

public class DisputeOperationView extends LinearLayout {
    private ItemView idItemView;
    private ItemView statusItemView;
    private ItemView amountItemView;
    private ItemView resultCodeItemView;

    public DisputeOperationView(Context context) {
        this(context, null);
    }

    public DisputeOperationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DisputeOperationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DisputeOperationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        idItemView = ItemView.create(context, R.string.id, this, TOP);
        statusItemView = ItemView.create(context, R.string.status, this, SECOND);
        amountItemView = ItemView.create(context, R.string.amount, this);
        resultCodeItemView = ItemView.create(context, R.string.result_code, this, BOTTOM);
    }

    public void bind(Transaction transaction) {
        idItemView.setValue(transaction.getTransactionId());
        statusItemView.setValue(transaction.getResponseMessage());
        amountItemView.setValue(transaction.getBalanceAmount().toString());
        resultCodeItemView.setValue(transaction.getResponseCode());
    }
}
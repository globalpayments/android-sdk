package com.globalpayments.android.sdk.sample.gpapi.verifications;

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

public class VerificationsView extends LinearLayout {
    private ItemView idItemView;
    private ItemView timeCreatedItemView;
    private ItemView statusItemView;
    private ItemView referenceItemView;
    private ItemView batchIdItemView;
    private ItemView resultCodeItemView;

    private ItemView paymentMethodItemView;
    private ItemView resultItemView;
    private ItemView cardTypeItemView;
    private ItemView maskedCardNumberItemView;
    private ItemView cvvResultItemView;

    public VerificationsView(Context context) {
        this(context, null);
    }

    public VerificationsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerificationsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VerificationsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        idItemView = ItemView.create(context, R.string.id, this, TOP);
        timeCreatedItemView = ItemView.create(context, R.string.time_created, this, SECOND);
        statusItemView = ItemView.create(context, R.string.status, this);
        referenceItemView = ItemView.create(context, R.string.reference, this);
        batchIdItemView = ItemView.create(context, R.string.batch_id, this);
        resultCodeItemView = ItemView.create(context, R.string.result_code, this);

        paymentMethodItemView = ItemView.create(context, R.string.header_payment_method, this);
        paymentMethodItemView.setAsGroupHeader();
        resultItemView = ItemView.create(context, R.string.result, this);
        cardTypeItemView = ItemView.create(context, R.string.card_type, this);
        maskedCardNumberItemView = ItemView.create(context, R.string.masked_card_number, this);
        cvvResultItemView = ItemView.create(context, R.string.cvv_result, this, BOTTOM);
    }

    public void bind(Transaction transaction) {
        idItemView.setValue(transaction.getTransactionId());
        timeCreatedItemView.setValue(transaction.getTimestamp());
        statusItemView.setValue(transaction.getResponseMessage());
        referenceItemView.setValue(transaction.getReferenceNumber());
        batchIdItemView.setValue(transaction.getBatchSummary().getSequenceNumber());
        resultCodeItemView.setValue(transaction.getResponseCode());

        resultItemView.setValue(transaction.getAuthorizationCode());
        cardTypeItemView.setValue(transaction.getCardType());
        maskedCardNumberItemView.setValue(transaction.getCardLast4());
        cvvResultItemView.setValue(transaction.getCvnResponseMessage());
    }
}
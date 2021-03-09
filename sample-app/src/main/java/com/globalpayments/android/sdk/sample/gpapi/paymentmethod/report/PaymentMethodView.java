package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

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

public class PaymentMethodView extends LinearLayout {
    private ItemView idItemView;
    private ItemView timeCreatedItemView;
    private ItemView statusItemView;
    private ItemView referenceItemView;
    private ItemView resultCodeItemView;
    private ItemView cardTypeItemView;
    private ItemView cardNumberItemView;
    private ItemView cardNumberLast4ItemView;
    private ItemView cardExpiryMonthItemView;
    private ItemView cardExpiryYearItemView;

    public PaymentMethodView(Context context) {
        this(context, null);
    }

    public PaymentMethodView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentMethodView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PaymentMethodView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        idItemView = ItemView.create(context, R.string.id, this, TOP);
        timeCreatedItemView = ItemView.create(context, R.string.time_created, this, SECOND);
        statusItemView = ItemView.create(context, R.string.status, this);
        referenceItemView = ItemView.create(context, R.string.reference, this);
        resultCodeItemView = ItemView.create(context, R.string.result_code, this);
        cardTypeItemView = ItemView.create(context, R.string.card_type, this);
        cardNumberItemView = ItemView.create(context, R.string.card_number, this);
        cardNumberLast4ItemView = ItemView.create(context, R.string.card_number_last_4, this);
        cardExpiryMonthItemView = ItemView.create(context, R.string.card_expiry_month, this);
        cardExpiryYearItemView = ItemView.create(context, R.string.card_expiry_year, this, BOTTOM);
    }

    public void bind(Transaction transaction) {
        idItemView.setValue(transaction.getTransactionId());
        timeCreatedItemView.setValue(transaction.getTimestamp());
        statusItemView.setValue(transaction.getResponseMessage());
        referenceItemView.setValue(transaction.getReferenceNumber());
        resultCodeItemView.setValue(transaction.getResponseCode());
        cardTypeItemView.setValue(transaction.getCardType());
        cardNumberItemView.setValueOrHide(transaction.getCardNumber());
        cardNumberLast4ItemView.setValue(transaction.getCardLast4());
        cardExpiryMonthItemView.setValue(String.valueOf(transaction.getCardExpMonth()));
        cardExpiryYearItemView.setValue(String.valueOf(2000 + transaction.getCardExpYear()));
    }
}
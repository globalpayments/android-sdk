package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import static com.globalpayments.android.sdk.sample.common.views.Position.BOTTOM;
import static com.globalpayments.android.sdk.sample.common.views.Position.SECOND;
import static com.globalpayments.android.sdk.sample.common.views.Position.TOP;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.global.api.entities.reporting.StoredPaymentMethodSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.views.ItemView;

public class PaymentMethodViewList extends LinearLayout {
    private ItemView idItemView;
    private ItemView timeCreatedItemView;
    private ItemView statusItemView;
    private ItemView referenceItemView;
    private ItemView nameItemView;
    private ItemView cardTypeItemView;
    private ItemView cardExpiryMonthItemView;
    private ItemView cardExpiryYearItemView;
    private ItemView cardNumberLast4ItemView;

    public PaymentMethodViewList(Context context) {
        this(context, null);
    }

    public PaymentMethodViewList(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentMethodViewList(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PaymentMethodViewList(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        idItemView = ItemView.create(context, R.string.id, this, TOP);
        timeCreatedItemView = ItemView.create(context, R.string.time_created, this, SECOND);
        statusItemView = ItemView.create(context, R.string.status, this);
        referenceItemView = ItemView.create(context, R.string.reference, this);
        nameItemView = ItemView.create(context, R.string.name, this);
        cardTypeItemView = ItemView.create(context, R.string.card_type, this);
        cardExpiryMonthItemView = ItemView.create(context, R.string.card_expiry_month, this);
        cardExpiryYearItemView = ItemView.create(context, R.string.card_expiry_year, this, BOTTOM);
        cardNumberLast4ItemView = ItemView.create(context, R.string.card_number_last_4, this);
    }

    public void bind(StoredPaymentMethodSummary storedPaymentMethodSummary) {
        idItemView.setValue(storedPaymentMethodSummary.getId());
        timeCreatedItemView.setValue(storedPaymentMethodSummary.getTimeCreated().toString());
        statusItemView.setValue(storedPaymentMethodSummary.getStatus());
        referenceItemView.setValue(storedPaymentMethodSummary.getReference());
        nameItemView.setValue(storedPaymentMethodSummary.getName());
        cardTypeItemView.setValue(storedPaymentMethodSummary.getCardType());
        cardExpiryMonthItemView.setValue(String.valueOf(storedPaymentMethodSummary.getCardExpMonth()));
        cardExpiryYearItemView.setValue(String.valueOf(2000 + Integer.parseInt(storedPaymentMethodSummary.getCardExpYear())));
        cardNumberLast4ItemView.setValue(storedPaymentMethodSummary.getCardLast4());
    }

}
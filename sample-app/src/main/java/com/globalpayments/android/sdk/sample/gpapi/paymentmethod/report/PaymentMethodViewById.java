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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

public class PaymentMethodViewById extends LinearLayout {
    private ItemView idItemView;
    private ItemView timeCreatedItemView;
    private ItemView statusItemView;
    private ItemView referenceItemView;
    private ItemView cardTypeItemView;
    private ItemView cardNumberLast4ItemView;
    private ItemView cardExpiryMonthItemView;
    private ItemView cardExpiryYearItemView;

    public PaymentMethodViewById(Context context) {
        this(context, null);
    }

    public PaymentMethodViewById(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentMethodViewById(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PaymentMethodViewById(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        idItemView = ItemView.create(context, R.string.id, this, TOP);
        timeCreatedItemView = ItemView.create(context, R.string.time_created, this, SECOND);
        statusItemView = ItemView.create(context, R.string.status, this);
        referenceItemView = ItemView.create(context, R.string.reference, this);
        cardTypeItemView = ItemView.create(context, R.string.card_type, this);
        cardNumberLast4ItemView = ItemView.create(context, R.string.card_number_last_4, this);
        cardExpiryMonthItemView = ItemView.create(context, R.string.card_expiry_month, this);
        cardExpiryYearItemView = ItemView.create(context, R.string.card_expiry_year, this, BOTTOM);
    }

    public void bind(StoredPaymentMethodSummary transaction) {
        idItemView.setValue(transaction.getId());
        DateTime transactionDate = transaction.getTimeCreated().withZoneRetainFields(DateTimeZone.UTC);
        timeCreatedItemView.setValue(ISODateTimeFormat.dateTime().print(transactionDate));
        statusItemView.setValue(transaction.getStatus());
        referenceItemView.setValue(transaction.getReference());
        cardTypeItemView.setValue(transaction.getCardType());
        cardNumberLast4ItemView.setValue(transaction.getCardLast4());
        cardExpiryMonthItemView.setValue(String.valueOf(transaction.getCardExpMonth()));
        cardExpiryYearItemView.setValue(String.valueOf(2000 + Integer.parseInt(transaction.getCardExpYear())));
    }
}
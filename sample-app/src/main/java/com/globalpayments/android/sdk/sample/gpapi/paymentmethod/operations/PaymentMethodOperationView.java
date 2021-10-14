package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations;

import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.views.ItemView;
import com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations.model.PaymentMethodOperationUIModel;

public class PaymentMethodOperationView extends LinearLayout {
    private ItemView idItemView;
    private ItemView resultItemView;
    private ItemView cardTypeItemView;
    private ItemView cardNumberItemView;
    private ItemView cardExpiryMonthItemView;
    private ItemView cardExpiryYearItemView;

    public PaymentMethodOperationView(Context context) {
        this(context, null);
    }

    public PaymentMethodOperationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentMethodOperationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PaymentMethodOperationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        resultItemView = ItemView.create(context, R.string.result, this);
        idItemView = ItemView.create(context, R.string.payment_method_id, this);
        cardTypeItemView = ItemView.create(context, R.string.card_type, this);
        cardNumberItemView = ItemView.create(context, R.string.card_number, this);
        cardExpiryMonthItemView = ItemView.create(context, R.string.card_expiry_month, this);
        cardExpiryYearItemView = ItemView.create(context, R.string.card_expiry_year, this);
    }

    public void bind(PaymentMethodOperationUIModel paymentMethodOperationUIModel) {
        resultItemView.setValueOrHide(paymentMethodOperationUIModel.getResult());
        idItemView.setValueOrHide(paymentMethodOperationUIModel.getPaymentMethodId());
        cardTypeItemView.setValueOrHide(paymentMethodOperationUIModel.getCardType());
        cardNumberItemView.setValueOrHide(paymentMethodOperationUIModel.getCardNumber());
        cardExpiryMonthItemView.setValueOrHide(paymentMethodOperationUIModel.getExpiryMonth());
        cardExpiryYearItemView.setValueOrHide(paymentMethodOperationUIModel.getExpiryYear());
        showView(this);
    }
}
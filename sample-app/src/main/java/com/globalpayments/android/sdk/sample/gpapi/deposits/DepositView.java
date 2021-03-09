package com.globalpayments.android.sdk.sample.gpapi.deposits;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.global.api.entities.reporting.DepositSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.views.ItemView;

import static com.globalpayments.android.sdk.sample.common.views.Position.BOTTOM;
import static com.globalpayments.android.sdk.sample.common.views.Position.SECOND;
import static com.globalpayments.android.sdk.sample.common.views.Position.TOP;
import static com.globalpayments.android.sdk.utils.DateUtils.YYYY_MM_DD;
import static com.globalpayments.android.sdk.utils.DateUtils.getDateFormatted;
import static com.globalpayments.android.sdk.utils.Utils.safeParseBigDecimal;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

public class DepositView extends LinearLayout {
    private ItemView idItemView;
    private ItemView timeCreatedItemView;
    private ItemView statusItemView;
    private ItemView fundingTypeItemView;
    private ItemView amountItemView;
    private ItemView currencyItemView;

    private ItemView systemItemView;
    private ItemView systemMidItemView;
    private ItemView systemHierarchyItemView;
    private ItemView systemNameItemView;
    private ItemView systemDbaItemView;

    private ItemView salesItemView;
    private ItemView salesCountItemView;
    private ItemView salesAmountItemView;

    private ItemView refundsItemView;
    private ItemView refundsCountItemView;
    private ItemView refundsAmountItemView;

    private ItemView disputesChargebacksItemView;
    private ItemView disputesChargebacksCountItemView;
    private ItemView disputesChargebacksAmountItemView;
    private ItemView disputesReversalsItemView;
    private ItemView disputesReversalsCountItemView;
    private ItemView disputesReversalsAmountItemView;

    private ItemView feesItemView;
    private ItemView feesAmountItemView;

    private ItemView bankTransferItemView;
    private ItemView bankTransferMaskedAccountNumberLast4ItemView;

    private LinearLayout expandableContainer;

    public DepositView(Context context) {
        this(context, null);
    }

    public DepositView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DepositView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DepositView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

        idItemView = ItemView.create(context, R.string.id, this, TOP);
        timeCreatedItemView = ItemView.create(context, R.string.time_created, this, SECOND);
        statusItemView = ItemView.create(context, R.string.status, this);
        fundingTypeItemView = ItemView.create(context, R.string.funding_type, this);
        amountItemView = ItemView.create(context, R.string.amount, this);
        currencyItemView = ItemView.create(context, R.string.currency, this);

        // Expandable group
        addView(expandableContainer);

        systemItemView = ItemView.create(context, R.string.header_system, expandableContainer);
        systemItemView.setAsGroupHeader();
        systemMidItemView = ItemView.create(context, R.string.mid, expandableContainer);
        systemHierarchyItemView = ItemView.create(context, R.string.hierarchy, expandableContainer);
        systemNameItemView = ItemView.create(context, R.string.name, expandableContainer);
        systemDbaItemView = ItemView.create(context, R.string.dba, expandableContainer);

        salesItemView = ItemView.create(context, R.string.header_sales, expandableContainer);
        salesItemView.setAsGroupHeader();
        salesCountItemView = ItemView.create(context, R.string.count, expandableContainer);
        salesAmountItemView = ItemView.create(context, R.string.amount, expandableContainer);

        refundsItemView = ItemView.create(context, R.string.header_refunds, expandableContainer);
        refundsItemView.setAsGroupHeader();
        refundsCountItemView = ItemView.create(context, R.string.count, expandableContainer);
        refundsAmountItemView = ItemView.create(context, R.string.amount, expandableContainer);

        disputesChargebacksItemView = ItemView.create(context, R.string.header_disputes_chargebacks, expandableContainer);
        disputesChargebacksItemView.setAsGroupHeader();
        disputesChargebacksCountItemView = ItemView.create(context, R.string.count, expandableContainer);
        disputesChargebacksAmountItemView = ItemView.create(context, R.string.amount, expandableContainer);

        disputesReversalsItemView = ItemView.create(context, R.string.header_disputes_reversals, expandableContainer);
        disputesReversalsItemView.setAsGroupHeader();
        disputesReversalsCountItemView = ItemView.create(context, R.string.count, expandableContainer);
        disputesReversalsAmountItemView = ItemView.create(context, R.string.amount, expandableContainer);

        feesItemView = ItemView.create(context, R.string.header_fees, expandableContainer);
        feesItemView.setAsGroupHeader();
        feesAmountItemView = ItemView.create(context, R.string.amount, expandableContainer);

        bankTransferItemView = ItemView.create(context, R.string.header_bank_transfer, expandableContainer);
        bankTransferItemView.setAsGroupHeader();
        bankTransferMaskedAccountNumberLast4ItemView =
                ItemView.create(context, R.string.masked_account_number_last_4, expandableContainer, BOTTOM);
    }

    public void expand() {
        showView(expandableContainer);
    }

    public void collapse() {
        hideView(expandableContainer);
    }

    public void bind(DepositSummary depositSummary) {
        idItemView.setValue(depositSummary.getDepositId());
        timeCreatedItemView.setValue(getDateFormatted(depositSummary.getDepositDate(), YYYY_MM_DD));
        statusItemView.setValue(depositSummary.getStatus());
        fundingTypeItemView.setValue(depositSummary.getType());
        amountItemView.setValue(safeParseBigDecimal(depositSummary.getAmount()));
        currencyItemView.setValue(depositSummary.getCurrency());

        systemMidItemView.setValue(depositSummary.getMerchantNumber());
        systemHierarchyItemView.setValue(depositSummary.getMerchantHierarchy());
        systemNameItemView.setValue(depositSummary.getMerchantName());
        systemDbaItemView.setValue(depositSummary.getMerchantDbaName());

        salesCountItemView.setValue(String.valueOf(depositSummary.getSalesTotalCount()));
        salesAmountItemView.setValue(safeParseBigDecimal(depositSummary.getSalesTotalAmount()));

        refundsCountItemView.setValue(String.valueOf(depositSummary.getRefundsTotalCount()));
        refundsAmountItemView.setValue(safeParseBigDecimal(depositSummary.getRefundsTotalAmount()));

        disputesChargebacksCountItemView.setValue(String.valueOf(depositSummary.getChargebackTotalCount()));
        disputesChargebacksAmountItemView.setValue(safeParseBigDecimal(depositSummary.getChargebackTotalAmount()));

        disputesReversalsCountItemView.setValue(String.valueOf(depositSummary.getAdjustmentTotalCount()));
        disputesReversalsAmountItemView.setValue(safeParseBigDecimal(depositSummary.getAdjustmentTotalAmount()));

        feesAmountItemView.setValue(safeParseBigDecimal(depositSummary.getFeesTotalAmount()));

        bankTransferMaskedAccountNumberLast4ItemView.setValue(depositSummary.getAccountNumber());
    }
}
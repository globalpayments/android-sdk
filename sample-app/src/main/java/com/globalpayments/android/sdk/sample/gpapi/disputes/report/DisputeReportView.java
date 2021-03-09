package com.globalpayments.android.sdk.sample.gpapi.disputes.report;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.global.api.entities.reporting.DisputeSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.views.ItemView;

import static com.globalpayments.android.sdk.sample.common.views.Position.BELOW_A_GROUP;
import static com.globalpayments.android.sdk.sample.common.views.Position.BOTTOM;
import static com.globalpayments.android.sdk.sample.common.views.Position.SECOND;
import static com.globalpayments.android.sdk.sample.common.views.Position.TOP;
import static com.globalpayments.android.sdk.utils.DateUtils.getDateISOFormatted;
import static com.globalpayments.android.sdk.utils.Utils.safeParseBigDecimal;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

public class DisputeReportView extends LinearLayout {
    private ItemView idItemView;
    private ItemView timeCreatedItemView;
    private ItemView statusItemView;
    private ItemView stageItemView;
    private ItemView amountItemView;
    private ItemView currencyItemView;

    private ItemView systemItemView;
    private ItemView systemMidItemView;
    private ItemView systemHierarchyItemView;

    private ItemView cardItemView;
    private ItemView cardNumberItemView;
    private ItemView cardArnItemView;
    private ItemView cardBrandItemView;

    private ItemView reasonCodeItemView;
    private ItemView reasonDescriptionItemView;
    private ItemView timeToRespondByItemView;
    private ItemView resultItemView;
    private ItemView lastAdjustmentAmountItemView;
    private ItemView lastAdjustmentCurrencyItemView;
    private ItemView lastAdjustmentFundingItemView;

    private LinearLayout expandableContainer;

    public DisputeReportView(Context context) {
        this(context, null);
    }

    public DisputeReportView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DisputeReportView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DisputeReportView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        stageItemView = ItemView.create(context, R.string.stage, this);
        amountItemView = ItemView.create(context, R.string.amount, this);
        currencyItemView = ItemView.create(context, R.string.currency, this);

        // Expandable group
        addView(expandableContainer);

        systemItemView = ItemView.create(context, R.string.header_system, expandableContainer);
        systemItemView.setAsGroupHeader();
        systemMidItemView = ItemView.create(context, R.string.mid, expandableContainer);
        systemHierarchyItemView = ItemView.create(context, R.string.hierarchy, expandableContainer);

        cardItemView = ItemView.create(context, R.string.header_card, expandableContainer);
        cardItemView.setAsGroupHeader();
        cardNumberItemView = ItemView.create(context, R.string.number, expandableContainer);
        cardArnItemView = ItemView.create(context, R.string.arn, expandableContainer);
        cardBrandItemView = ItemView.create(context, R.string.brand, expandableContainer);

        reasonCodeItemView = ItemView.create(context, R.string.reason_code, expandableContainer, BELOW_A_GROUP);
        reasonDescriptionItemView = ItemView.create(context, R.string.reason_description, expandableContainer);
        timeToRespondByItemView = ItemView.create(context, R.string.time_to_respond_by, expandableContainer);
        resultItemView = ItemView.create(context, R.string.result, expandableContainer);
        lastAdjustmentAmountItemView = ItemView.create(context, R.string.last_adjustment_amount, expandableContainer);
        lastAdjustmentCurrencyItemView = ItemView.create(context, R.string.last_adjustment_currency, expandableContainer);
        lastAdjustmentFundingItemView = ItemView.create(context, R.string.last_adjustment_funding, expandableContainer, BOTTOM);
    }

    public void expand() {
        showView(expandableContainer);
    }

    public void collapse() {
        hideView(expandableContainer);
    }

    public void bind(DisputeSummary disputeSummary) {
        idItemView.setValue(disputeSummary.getCaseId());
        timeCreatedItemView.setValueOrHide(getDateISOFormatted(disputeSummary.getCaseIdTime()));
        statusItemView.setValue(disputeSummary.getCaseStatus());
        stageItemView.setValue(disputeSummary.getCaseStage());
        amountItemView.setValue(safeParseBigDecimal(disputeSummary.getCaseAmount()));
        currencyItemView.setValue(disputeSummary.getCaseCurrency());

        systemMidItemView.setValue(disputeSummary.getCaseMerchantId());
        systemHierarchyItemView.setValue(disputeSummary.getMerchantHierarchy());

        cardNumberItemView.setValue(disputeSummary.getTransactionMaskedCardNumber());
        cardArnItemView.setValue(disputeSummary.getTransactionARN());
        cardBrandItemView.setValue(disputeSummary.getTransactionCardType());

        reasonCodeItemView.setValue(disputeSummary.getReasonCode());
        reasonDescriptionItemView.setValue(disputeSummary.getReason());
        timeToRespondByItemView.setValue(getDateISOFormatted(disputeSummary.getRespondByDate()));
        resultItemView.setValue(disputeSummary.getResult());
        lastAdjustmentAmountItemView.setValue(safeParseBigDecimal(disputeSummary.getLastAdjustmentAmount()));
        lastAdjustmentCurrencyItemView.setValue(disputeSummary.getLastAdjustmentCurrency());
        lastAdjustmentFundingItemView.setValue(disputeSummary.getLastAdjustmentFunding());
    }
}
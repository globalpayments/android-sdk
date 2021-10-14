package com.globalpayments.android.sdk.sample.gpapi.disputes.report;

import static com.globalpayments.android.sdk.sample.common.views.Position.BELOW_A_GROUP;
import static com.globalpayments.android.sdk.sample.common.views.Position.BOTTOM;
import static com.globalpayments.android.sdk.sample.common.views.Position.SECOND;
import static com.globalpayments.android.sdk.sample.common.views.Position.TOP;
import static com.globalpayments.android.sdk.utils.DateUtils.ISO_DATE_FORMAT_2;
import static com.globalpayments.android.sdk.utils.DateUtils.getDateFormatted;
import static com.globalpayments.android.sdk.utils.DateUtils.getDateISOFormatted;
import static com.globalpayments.android.sdk.utils.Utils.isNotNullOrBlank;
import static com.globalpayments.android.sdk.utils.Utils.safeParseBigDecimal;
import static com.globalpayments.android.sdk.utils.ViewUtils.handleViewVisibility;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.global.api.entities.reporting.DisputeSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.views.ItemView;

import java.math.BigDecimal;
import java.util.Date;

public class DisputeReportView extends LinearLayout {
    private ItemView idItemView;
    private ItemView depositReference;
    private ItemView timeCreatedItemView;
    private ItemView statusItemView;
    private ItemView stageItemView;
    private ItemView amountItemView;
    private ItemView currencyItemView;

    // Expandable group
    private ItemView reasonCodeItemView;
    private ItemView reasonDescriptionItemView;
    private ItemView resultItemView;

    private ItemView systemItemView;
    private ItemView systemMidItemView;
    private ItemView systemTidItemView;
    private ItemView systemHierarchyItemView;
    private ItemView systemNameItemView;
    private ItemView systemDbaItemView;

    private ItemView lastAdjustmentAmountItemView;
    private ItemView lastAdjustmentCurrencyItemView;
    private ItemView lastAdjustmentFundingItemView;

    private ItemView cardItemView;
    private ItemView cardNumberItemView;
    private ItemView cardArnItemView;
    private ItemView cardBrandItemView;
    private ItemView cardAuthCodeItemView;

    private ItemView timeToRespondByItemView;

    private ItemView transactionItemView;
    private ItemView transactionTimeCreatedItemView;
    private ItemView transactionTypeItemView;
    private ItemView transactionAmountItemView;
    private ItemView transactionCurrencyItemView;
    private ItemView transactionReferenceItemView;

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
        depositReference = ItemView.create(context, R.string.deposit_id, this);
        statusItemView = ItemView.create(context, R.string.status, this);

        stageItemView = ItemView.create(context, R.string.stage, this);
        amountItemView = ItemView.create(context, R.string.amount, this);
        currencyItemView = ItemView.create(context, R.string.currency, this);

        // Expandable group
        addView(expandableContainer);

        reasonCodeItemView = ItemView.create(context, R.string.reason_code, expandableContainer);
        reasonDescriptionItemView = ItemView.create(context, R.string.reason_description, expandableContainer);
        resultItemView = ItemView.create(context, R.string.result, expandableContainer);

        systemItemView = ItemView.create(context, R.string.header_system, expandableContainer);
        systemItemView.setAsGroupHeader();
        systemMidItemView = ItemView.create(context, R.string.mid, expandableContainer);
        systemTidItemView = ItemView.create(context, R.string.tid, expandableContainer);
        systemHierarchyItemView = ItemView.create(context, R.string.hierarchy, expandableContainer);
        systemNameItemView = ItemView.create(context, R.string.name, expandableContainer);
        systemDbaItemView = ItemView.create(context, R.string.dba, expandableContainer);

        lastAdjustmentAmountItemView = ItemView.create(context, R.string.last_adjustment_amount, expandableContainer, BELOW_A_GROUP);
        lastAdjustmentCurrencyItemView = ItemView.create(context, R.string.last_adjustment_currency, expandableContainer);
        lastAdjustmentFundingItemView = ItemView.create(context, R.string.last_adjustment_funding, expandableContainer);

        cardItemView = ItemView.create(context, R.string.header_card, expandableContainer);
        cardItemView.setAsGroupHeader();
        cardNumberItemView = ItemView.create(context, R.string.number, expandableContainer);
        cardArnItemView = ItemView.create(context, R.string.arn, expandableContainer);
        cardBrandItemView = ItemView.create(context, R.string.brand, expandableContainer);
        cardAuthCodeItemView = ItemView.create(context, R.string.auth_code, expandableContainer);

        timeToRespondByItemView = ItemView.create(context, R.string.time_to_respond_by, expandableContainer, BELOW_A_GROUP);

        transactionItemView = ItemView.create(context, R.string.transaction, expandableContainer);
        transactionItemView.setAsGroupHeader();
        transactionTimeCreatedItemView = ItemView.create(context, R.string.time_created, expandableContainer);
        transactionTypeItemView = ItemView.create(context, R.string.type, expandableContainer);
        transactionAmountItemView = ItemView.create(context, R.string.amount, expandableContainer);
        transactionCurrencyItemView = ItemView.create(context, R.string.currency, expandableContainer);
        transactionReferenceItemView = ItemView.create(context, R.string.reference, expandableContainer, BOTTOM);
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
        depositReference.setValue(disputeSummary.getDepositReference());
        statusItemView.setValue(disputeSummary.getCaseStatus());
        stageItemView.setValue(disputeSummary.getCaseStage());
        amountItemView.setValue(safeParseBigDecimal(disputeSummary.getCaseAmount()));
        currencyItemView.setValue(disputeSummary.getCaseCurrency());

        reasonCodeItemView.setValue(disputeSummary.getReasonCode());
        reasonDescriptionItemView.setValue(disputeSummary.getReason());
        resultItemView.setValue(disputeSummary.getResult());

        systemMidItemView.setValue(disputeSummary.getCaseMerchantId());
        systemTidItemView.setValue(disputeSummary.getCaseTerminalId());
        systemHierarchyItemView.setValue(disputeSummary.getMerchantHierarchy());
        systemNameItemView.setValue(disputeSummary.getMerchantName());
        systemDbaItemView.setValue(disputeSummary.getMerchantDbaName());

        lastAdjustmentAmountItemView.setValue(safeParseBigDecimal(disputeSummary.getLastAdjustmentAmount()));
        lastAdjustmentCurrencyItemView.setValue(disputeSummary.getLastAdjustmentCurrency());
        lastAdjustmentFundingItemView.setValue(disputeSummary.getLastAdjustmentFunding());

        cardNumberItemView.setValue(disputeSummary.getTransactionMaskedCardNumber());
        cardArnItemView.setValue(disputeSummary.getTransactionARN());
        cardBrandItemView.setValue(disputeSummary.getTransactionCardType());
        cardAuthCodeItemView.setValueOrHide(disputeSummary.getTransactionAuthCode());

        timeToRespondByItemView.setValue(getDateISOFormatted(disputeSummary.getRespondByDate()));

        Date transactionTime = disputeSummary.getTransactionTime();
        String transactionType = disputeSummary.getTransactionType();
        BigDecimal transactionAmount = disputeSummary.getTransactionAmount();
        String transactionCurrency = disputeSummary.getTransactionCurrency();
        String transactionReferenceNumber = disputeSummary.getTransactionReferenceNumber();

        boolean showTransactionGroup = transactionTime != null
                || isNotNullOrBlank(transactionType)
                || transactionAmount != null
                || isNotNullOrBlank(transactionCurrency)
                || isNotNullOrBlank(transactionReferenceNumber);

        handleViewVisibility(transactionItemView, showTransactionGroup);
        transactionTimeCreatedItemView.setValueOrHide(getDateFormatted(transactionTime, ISO_DATE_FORMAT_2));
        transactionTypeItemView.setValueOrHide(transactionType);
        transactionAmountItemView.setValueOrHide(safeParseBigDecimal(transactionAmount));
        transactionCurrencyItemView.setValueOrHide(transactionCurrency);
        transactionReferenceItemView.setValueOrHide(transactionReferenceNumber);
    }
}
package com.globalpayments.android.sdk.sample.gpapi.transaction.report;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.global.api.entities.TransactionSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseViewHolder;
import com.globalpayments.android.sdk.sample.common.views.ItemView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import static com.globalpayments.android.sdk.sample.common.Constants.INITIAL_DEGREE;
import static com.globalpayments.android.sdk.sample.common.Constants.ROTATED_DEGREE;
import static com.globalpayments.android.sdk.sample.common.views.Position.BOTTOM;
import static com.globalpayments.android.sdk.sample.common.views.Position.SECOND;
import static com.globalpayments.android.sdk.sample.common.views.Position.TOP;
import static com.globalpayments.android.sdk.utils.Utils.getAmount;
import static com.globalpayments.android.sdk.utils.ViewUtils.handleViewVisibility;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

public class TransactionReportViewHolder extends BaseViewHolder<TransactionSummary> {
    private LinearLayout rowsContainer;
    private LinearLayout expandableContainer;
    private ImageView arrow;

    private ItemView idItemView;
    private ItemView timeCreatedItemView;
    private ItemView statusItemView;
    private ItemView typeItemView;
    private ItemView channelItemView;
    private ItemView amountItemView;
    private ItemView currencyItemView;
    private ItemView referenceItemView;
    private ItemView clientTransIdItemView;
    private ItemView batchIdItemView;
    private ItemView countryItemView;
    private ItemView originalTransIdItemView;
    private ItemView gatewayMessageItemView;
    private ItemView entryModeItemView;
    private ItemView nameItemView;
    private ItemView cardTypeItemView;
    private ItemView authcodeItemView;
    private ItemView brandReferenceItemView;
    private ItemView arnItemView;
    private ItemView maskedCardNumberItemView;

    private boolean isItemExpanded;
    private boolean isExpandedByDefault;

    public TransactionReportViewHolder(@NonNull View itemView, boolean isExpandedByDefault) {
        super(itemView);
        this.isExpandedByDefault = isExpandedByDefault;
        initViews(itemView);
    }

    private void initViews(View itemView) {
        Context context = itemView.getContext();
        rowsContainer = itemView.findViewById(R.id.rowsContainer);
        arrow = itemView.findViewById(R.id.arrow);

        expandableContainer = new LinearLayout(context);
        expandableContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        expandableContainer.setOrientation(LinearLayout.VERTICAL);
        handleViewVisibility(expandableContainer, isExpandedByDefault);

        idItemView = ItemView.create(context, R.string.id, rowsContainer, TOP);
        timeCreatedItemView = ItemView.create(context, R.string.time_created, rowsContainer, SECOND);
        statusItemView = ItemView.create(context, R.string.status, rowsContainer);
        typeItemView = ItemView.create(context, R.string.type, rowsContainer);

        // Expandable group
        rowsContainer.addView(expandableContainer);
        channelItemView = ItemView.create(context, R.string.channel, expandableContainer);
        amountItemView = ItemView.create(context, R.string.amount, expandableContainer);
        currencyItemView = ItemView.create(context, R.string.currency, expandableContainer);
        referenceItemView = ItemView.create(context, R.string.reference, expandableContainer);
        clientTransIdItemView = ItemView.create(context, R.string.client_transaction_id, expandableContainer);
        batchIdItemView = ItemView.create(context, R.string.batch_id, expandableContainer);
        countryItemView = ItemView.create(context, R.string.country, expandableContainer);
        originalTransIdItemView = ItemView.create(context, R.string.original_transaction_id, expandableContainer);
        gatewayMessageItemView = ItemView.create(context, R.string.gateway_response_message, expandableContainer);
        entryModeItemView = ItemView.create(context, R.string.entry_mode, expandableContainer);
        nameItemView = ItemView.create(context, R.string.name, expandableContainer);
        cardTypeItemView = ItemView.create(context, R.string.card_type, expandableContainer);
        authcodeItemView = ItemView.create(context, R.string.auth_code, expandableContainer);
        brandReferenceItemView = ItemView.create(context, R.string.brand_reference, expandableContainer);
        arnItemView = ItemView.create(context, R.string.aquirer_reference_number, expandableContainer);
        maskedCardNumberItemView = ItemView.create(context, R.string.masked_card_number, expandableContainer, BOTTOM);

        arrow.setOnClickListener(v -> toggleView());
    }

    @Override
    public void bind(TransactionSummary transactionSummary) {
        handleFoldState(isExpandedByDefault);

        idItemView.setValueOrHide(transactionSummary.getTransactionId());
        DateTime transactionDate = transactionSummary.getTransactionDate().withZoneRetainFields(DateTimeZone.UTC);
        timeCreatedItemView.setValue(ISODateTimeFormat.dateTime().print(transactionDate));
        statusItemView.setValue(transactionSummary.getTransactionStatus());
        typeItemView.setValue(transactionSummary.getTransactionType());
        channelItemView.setValueOrHide(transactionSummary.getChannel());
        amountItemView.setValue(getAmount(transactionSummary.getAmount()));
        currencyItemView.setValue(transactionSummary.getCurrency());
        referenceItemView.setValue(transactionSummary.getReferenceNumber());
        clientTransIdItemView.setValue(transactionSummary.getClientTransactionId());
        batchIdItemView.setValue(transactionSummary.getBatchSequenceNumber());
        countryItemView.setValueOrHide(transactionSummary.getCountry());
        originalTransIdItemView.setValueOrHide(transactionSummary.getOriginalTransactionId());
        gatewayMessageItemView.setValueOrHide(transactionSummary.getGatewayResponseMessage());
        entryModeItemView.setValue(transactionSummary.getEntryMode());
        nameItemView.setValueOrHide(transactionSummary.getCardHolderName());
        cardTypeItemView.setValue(transactionSummary.getCardType());
        authcodeItemView.setValue(transactionSummary.getAuthCode());
        brandReferenceItemView.setValue(transactionSummary.getBrandReference());
        arnItemView.setValueOrHide(transactionSummary.getAquirerReferenceNumber());
        maskedCardNumberItemView.setValue(transactionSummary.getMaskedCardNumber());
    }

    private void handleFoldState(boolean expand) {
        if (expand) {
            isItemExpanded = true;
            arrow.setRotation(ROTATED_DEGREE);
            showView(expandableContainer);
        } else {
            isItemExpanded = false;
            arrow.setRotation(INITIAL_DEGREE);
            hideView(expandableContainer);
        }
    }

    private void toggleView() {
        handleFoldState(!isItemExpanded);
    }
}
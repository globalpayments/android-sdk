package com.globalpayments.android.sdk.sample.gpapi.accesstoken;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.global.api.entities.gpApi.entities.AccessTokenInfo;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.views.ItemView;

import static com.globalpayments.android.sdk.sample.common.views.Position.SECOND;
import static com.globalpayments.android.sdk.sample.common.views.Position.TOP;

public class AccessTokenView extends LinearLayout {
    private ItemView tokenItemView;
    private ItemView dataAccountNameItemView;
    private ItemView disputeManagementAccountNameItemView;
    private ItemView tokenizationAccountNameItemView;
    private ItemView transactionProcessingAccountNameItemView;

    public AccessTokenView(Context context) {
        this(context, null);
    }

    public AccessTokenView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccessTokenView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AccessTokenView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        tokenItemView = ItemView.create(context, R.string.token, this, TOP);
        dataAccountNameItemView = ItemView.create(context, R.string.data_account_name, this, SECOND, ItemView.Orientation.VERTICAL);

        disputeManagementAccountNameItemView = ItemView.create(context, R.string.dispute_management_account_name, this,
                ItemView.Orientation.VERTICAL);

        tokenizationAccountNameItemView = ItemView.create(context, R.string.tokenization_account_name, this,
                ItemView.Orientation.VERTICAL);

        transactionProcessingAccountNameItemView = ItemView.create(context, R.string.transaction_processing_account_name, this,
                ItemView.Orientation.VERTICAL);
    }

    public void bind(AccessTokenInfo accessTokenInfo) {
        tokenItemView.setValue(accessTokenInfo.getToken());
        dataAccountNameItemView.setValue(accessTokenInfo.getDataAccountName());
        disputeManagementAccountNameItemView.setValue(accessTokenInfo.getDisputeManagementAccountName());
        tokenizationAccountNameItemView.setValue(accessTokenInfo.getTokenizationAccountName());
        transactionProcessingAccountNameItemView.setValue(accessTokenInfo.getTransactionProcessingAccountName());
    }
}
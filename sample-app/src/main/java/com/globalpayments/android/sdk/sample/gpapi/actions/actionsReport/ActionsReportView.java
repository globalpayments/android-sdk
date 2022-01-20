package com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport;

import static com.globalpayments.android.sdk.sample.common.views.Position.SECOND;
import static com.globalpayments.android.sdk.sample.common.views.Position.TOP;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import com.global.api.entities.reporting.ActionSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.views.ItemView;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

public class ActionsReportView extends LinearLayout {
    private ItemView idItemView;
    private ItemView typeItemView;
    private ItemView timeCreatedItemView;
    private ItemView resourceItemView;
    private ItemView resourceIdItemView;
    private ItemView resourceStatusItemView;

    // Expandable group
    private ItemView versionItemView;
    private ItemView httpResponseCodeItemView;
    private ItemView responseCodeItemView;
    private ItemView appIdItemView;
    private ItemView appNameItemView;
    private ItemView merchantNameItemView;
    private ItemView accountNameItemView;

    private LinearLayout expandableContainer;

    public ActionsReportView(Context context) {
        this(context, null);
    }

    public ActionsReportView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionsReportView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ActionsReportView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

        idItemView = ItemView.create(context, R.string.action_id, this, TOP);
        timeCreatedItemView = ItemView.create(context, R.string.time_created, this, SECOND);
        typeItemView = ItemView.create(context, R.string.report_type, this);
        resourceItemView = ItemView.create(context, R.string.report_resouce, this);

        resourceIdItemView = ItemView.create(context, R.string.report_resource_id, this);
        resourceStatusItemView = ItemView.create(context, R.string.report_resouce_status, this);

        // Expandable group
        addView(expandableContainer);

        versionItemView = ItemView.create(context, R.string.report_version, expandableContainer);
        httpResponseCodeItemView = ItemView.create(context, R.string.report_http_response_code, expandableContainer);
        responseCodeItemView = ItemView.create(context, R.string.report_response_code, expandableContainer);

        appIdItemView = ItemView.create(context, R.string.report_app_id, expandableContainer);
        appNameItemView = ItemView.create(context, R.string.report_app_name, expandableContainer);
        appNameItemView.setAsGroupHeader();
        merchantNameItemView = ItemView.create(context, R.string.report_merchant_name, expandableContainer);
        accountNameItemView = ItemView.create(context, R.string.report_account_name, expandableContainer);

    }

    public void expand() {
        showView(expandableContainer);
    }

    public void collapse() {
        hideView(expandableContainer);
    }

    public void bind(ActionSummary actionSummary) {
        idItemView.setValue(actionSummary.getId());
        DateTime transactionDate = actionSummary.getTimeCreated().withZoneRetainFields(DateTimeZone.UTC);
        timeCreatedItemView.setValueOrHide(ISODateTimeFormat.dateTime().print(transactionDate));
        typeItemView.setValue(actionSummary.getType());
        resourceItemView.setValue(actionSummary.getResource());
        resourceIdItemView.setValue(actionSummary.getResourceId());
        resourceStatusItemView.setValue(actionSummary.getResourceStatus());
        versionItemView.setValue(actionSummary.getVersion());

        httpResponseCodeItemView.setValue(actionSummary.getHttpResponseCode());
        responseCodeItemView.setValue(actionSummary.getResponseCode());
        appIdItemView.setValue(actionSummary.getAppId());

        appNameItemView.setValue(actionSummary.getAppName());
        merchantNameItemView.setValue(actionSummary.getMerchantName());
        accountNameItemView.setValue(actionSummary.getAccountName());

    }
}

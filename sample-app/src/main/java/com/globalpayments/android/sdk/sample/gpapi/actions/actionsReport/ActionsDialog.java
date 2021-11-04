package com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport;

import static com.globalpayments.android.sdk.sample.common.Constants.DIALOG_TYPE;
import static com.globalpayments.android.sdk.utils.DateUtils.YYYY_MM_DD;
import static com.globalpayments.android.sdk.utils.Utils.isNotNull;
import static com.globalpayments.android.sdk.utils.Utils.safeParseInt;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideAllViewsExcluding;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideViews;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.global.api.entities.enums.DisputeSortProperty;
import com.global.api.entities.enums.SortDirection;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomSpinner;
import com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport.model.ActionsReportParametersModel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ActionsDialog extends BaseDialogFragment {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD, Locale.getDefault());

    private GridLayout glContainer;
    private TextView tvHeader;
    private EditText etActionsListPage;
    private EditText etActionsListPageSize;
    private CustomSpinner etActionsListOrder;
    private CustomSpinner etActionsListOrderBy;
    private EditText etActionsListId;
    private TextView tvActionListId;
    private EditText etActionsListType;
    private EditText etActionsListResource;
    private EditText etActionsListResourceStatus;
    private TextView etActionsListFromTimeCreated;
    private TextView etActionsListToTimeCreated;
    private EditText etActionsListMerchantName;
    private EditText etActionsListAccountName;
    private EditText etActionsListVersion;
    private Button btSubmit;

    private TYPE type = TYPE.ACTIONS_REPORT_LIST;

    public static ActionsDialog newInstance(Fragment targetFragment, TYPE type) {
        ActionsDialog actionsDialog = new ActionsDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable(DIALOG_TYPE, type);
        actionsDialog.setArguments(bundle);

        actionsDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        actionsDialog.setTargetFragment(targetFragment, 0);

        return actionsDialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_actions_report_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            Serializable dialogType = arguments.getSerializable(DIALOG_TYPE);

            if (dialogType instanceof TYPE) {
                type = (TYPE) dialogType;
            }
        }
    }

    @Override
    protected void initViews() {
        glContainer = findViewById(R.id.glContainer);
        tvHeader = findViewById(R.id.tvHeader);
        etActionsListPage = findViewById(R.id.etActionsListPage);
        etActionsListPageSize = findViewById(R.id.etActionsListPageSize);
        etActionsListOrder = findViewById(R.id.etActionsListOrder);
        etActionsListOrderBy = findViewById(R.id.etActionsListOrderBy);
        etActionsListId = findViewById(R.id.etActionsListId);
        tvActionListId = findViewById(R.id.tvActionListId);
        etActionsListType = findViewById(R.id.etActionsListType);
        etActionsListResource = findViewById(R.id.etActionsListResource);
        etActionsListResourceStatus = findViewById(R.id.etActionsListResourceStatus);
        etActionsListFromTimeCreated = findViewById(R.id.etActionsListFromTimeCreated);
        etActionsListToTimeCreated = findViewById(R.id.etActionsListToTimeCreated);
        etActionsListMerchantName = findViewById(R.id.etActionsListMerchantName);
        etActionsListAccountName = findViewById(R.id.etActionsListAccountName);
        etActionsListVersion = findViewById(R.id.etActionsListAppVersion);
        btSubmit = findViewById(R.id.btSubmit);

        switch (type) {
            case ACTIONS_REPORT_LIST:
                handleListViewsVisibility();
                initSpinners();
                initDatePickers();
                break;
            case ACTIONS_REPORT_BY_ID:
                handleDisputeByIdViewsVisibility();
                break;
        }

        btSubmit.setOnClickListener(v -> submit());
    }

    private void handleListViewsVisibility() {
        hideViews(etActionsListVersion);
    }

    private void handleDisputeByIdViewsVisibility() {
        hideAllViewsExcluding(glContainer,
                tvHeader,
                tvActionListId,
                etActionsListId,
                btSubmit
        );
    }

    private void startDatePicker(View view) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener onDateSetListener = (pickerView, selectedYear, selectedMonth, selectedDay) -> {
            Date date = new GregorianCalendar(selectedYear, selectedMonth, selectedDay).getTime();
            TextView textView = (TextView) view;
            textView.setText(dateFormat.format(date));
            textView.setTag(date);
        };

        new DatePickerDialog(requireContext(), onDateSetListener, year, month, day).show();
    }

    private void initDatePickers() {
        etActionsListFromTimeCreated.setOnClickListener(this::startDatePicker);
        Date now = new Date();
        etActionsListFromTimeCreated.setText(dateFormat.format(now));
        etActionsListFromTimeCreated.setTag(now);
        etActionsListToTimeCreated.setOnClickListener(this::startDatePicker);
        etActionsListToTimeCreated.setText(dateFormat.format(now));
        etActionsListToTimeCreated.setTag(now);
    }

    private void initSpinners() {
        etActionsListOrderBy.init(DisputeSortProperty.values());
        etActionsListOrderBy.selectItem(DisputeSortProperty.FromStageTimeCreated);
        etActionsListOrder.init(SortDirection.values());
        etActionsListOrder.selectItem(SortDirection.Descending);
    }

    private ActionsReportParametersModel buildActionsRootParametersBuildModel() {
        ActionsReportParametersModel actionsReportParametersModel = new ActionsReportParametersModel();

        Integer page = safeParseInt(etActionsListPage.getText().toString());
        if (isNotNull(page)) {
            actionsReportParametersModel.setPage(page);
        }

        Integer pageSize = safeParseInt(etActionsListPageSize.getText().toString());
        if (isNotNull(pageSize)) {
            actionsReportParametersModel.setPageSize(pageSize);
        }

        actionsReportParametersModel.setType(etActionsListType.getText().toString());
        actionsReportParametersModel.setResource(etActionsListResource.getText().toString());
        actionsReportParametersModel.setResourceStatus(etActionsListResourceStatus.getText().toString());
        actionsReportParametersModel.setAccountName(etActionsListAccountName.getText().toString());
        actionsReportParametersModel.setMerchantName(etActionsListMerchantName.getText().toString());
        actionsReportParametersModel.setVersion(etActionsListVersion.getText().toString());
        actionsReportParametersModel.setFromTimeCreated(getDate(etActionsListFromTimeCreated));
        actionsReportParametersModel.setToTimeCreated(getDate(etActionsListToTimeCreated));


        return actionsReportParametersModel;
    }

    private Date getDate(View view) {
        Object tag = view.getTag();

        if (tag instanceof Date) {
            return (Date) tag;
        }

        return null;
    }

    private void submit() {
        Fragment targetFragment = getTargetFragment();

        if (targetFragment instanceof Callback) {
            Callback callback = (Callback) targetFragment;

            switch (type) {
                case ACTIONS_REPORT_LIST:
                    ActionsReportParametersModel actionsReportParametersModel = buildActionsRootParametersBuildModel();
                    callback.onSubmitActionsReportListParametersModel(actionsReportParametersModel);
                    break;
                case ACTIONS_REPORT_BY_ID:
                    callback.onSubmitActionsReportId(etActionsListId.getText().toString());
                    break;
            }
        }

        dismiss();
    }

    public enum TYPE {
        ACTIONS_REPORT_LIST,
        ACTIONS_REPORT_BY_ID
    }

    public interface Callback {
        void onSubmitActionsReportListParametersModel(ActionsReportParametersModel actionsReportParametersModel);

        void onSubmitActionsReportId(String disputeId);
    }
}

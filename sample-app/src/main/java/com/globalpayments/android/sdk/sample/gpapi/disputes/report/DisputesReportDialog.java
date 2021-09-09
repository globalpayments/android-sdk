package com.globalpayments.android.sdk.sample.gpapi.disputes.report;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.global.api.entities.enums.DisputeSortProperty;
import com.global.api.entities.enums.DisputeStage;
import com.global.api.entities.enums.DisputeStatus;
import com.global.api.entities.enums.SortDirection;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomSpinner;
import com.globalpayments.android.sdk.sample.gpapi.disputes.report.model.DisputesReportParametersModel;
import com.globalpayments.android.sdk.sample.gpapi.disputes.report.model.DocumentReportModel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.globalpayments.android.sdk.sample.common.Constants.DIALOG_TYPE;
import static com.globalpayments.android.sdk.utils.DateUtils.YYYY_MM_DD;
import static com.globalpayments.android.sdk.utils.Utils.isNotNull;
import static com.globalpayments.android.sdk.utils.Utils.safeParseInt;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideAllViewsExcluding;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideViews;

public class DisputesReportDialog extends BaseDialogFragment {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD, Locale.getDefault());

    private GridLayout glContainer;
    private TextView tvHeader;
    private EditText etPage;
    private EditText etPageSize;
    private CustomSpinner orderBySpinner;
    private CustomSpinner orderSpinner;
    private EditText etArn;
    private CustomSpinner brandSpinner;
    private CustomSpinner statusSpinner;
    private CustomSpinner stageSpinner;
    private TextView tvFromStageTimeCreated;
    private TextView tvToStageTimeCreated;
    private TextView tvFromAdjustmentTimeCreated;
    private TextView tvToAdjustmentTimeCreated;
    private EditText etSystemMID;
    private EditText etSystemHierarchy;
    private TextView tvLabelDisputeId;
    private EditText etDisputeId;
    private TextView tvLabelDocumentId;
    private EditText etDocumentId;
    private TextView tvLabelFromSettlements;
    private CheckBox cbFromSettlements;
    private Button btSubmit;

    private TYPE type = TYPE.DISPUTE_LIST;

    public static DisputesReportDialog newInstance(Fragment targetFragment, TYPE type) {
        DisputesReportDialog disputesReportDialog = new DisputesReportDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable(DIALOG_TYPE, type);
        disputesReportDialog.setArguments(bundle);

        disputesReportDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        disputesReportDialog.setTargetFragment(targetFragment, 0);

        return disputesReportDialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_disputes_report;
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
        etPage = findViewById(R.id.etPage);
        etPageSize = findViewById(R.id.etPageSize);
        orderBySpinner = findViewById(R.id.orderBySpinner);
        orderSpinner = findViewById(R.id.orderSpinner);
        etArn = findViewById(R.id.etArn);
        brandSpinner = findViewById(R.id.brandSpinner);
        statusSpinner = findViewById(R.id.statusSpinner);
        stageSpinner = findViewById(R.id.stageSpinner);
        tvFromStageTimeCreated = findViewById(R.id.tvFromStageTimeCreated);
        tvToStageTimeCreated = findViewById(R.id.tvToStageTimeCreated);
        tvFromAdjustmentTimeCreated = findViewById(R.id.tvFromAdjustmentTimeCreated);
        tvToAdjustmentTimeCreated = findViewById(R.id.tvToAdjustmentTimeCreated);
        etSystemMID = findViewById(R.id.etSystemMID);
        etSystemHierarchy = findViewById(R.id.etSystemHierarchy);
        tvLabelDisputeId = findViewById(R.id.tvLabelDisputeId);
        etDisputeId = findViewById(R.id.etDisputeId);
        tvLabelDocumentId = findViewById(R.id.tvLabelDocumentId);
        etDocumentId = findViewById(R.id.etDocumentId);
        tvLabelFromSettlements = findViewById(R.id.tvLabelFromSettlements);
        cbFromSettlements = findViewById(R.id.cbFromSettlements);
        btSubmit = findViewById(R.id.btSubmit);

        switch (type) {
            case DISPUTE_LIST:
                handleListViewsVisibility();
                initSpinners();
                initDatePickers();
                break;
            case DISPUTE_BY_ID:
                handleDisputeByIdViewsVisibility();
                break;
            case DOCUMENT_BY_ID:
                handleDocumentByIdViewsVisibility();
                break;
        }

        btSubmit.setOnClickListener(v -> submit());
    }

    private void handleListViewsVisibility() {
        hideViews(tvLabelDisputeId, etDisputeId, tvLabelDocumentId, etDocumentId);
    }

    private void handleDisputeByIdViewsVisibility() {
        hideAllViewsExcluding(glContainer,
                tvHeader,
                tvLabelDisputeId,
                etDisputeId,
                tvLabelFromSettlements,
                cbFromSettlements,
                btSubmit
        );
    }

    private void handleDocumentByIdViewsVisibility() {
        hideAllViewsExcluding(glContainer,
                tvHeader,
                tvLabelDisputeId,
                etDisputeId,
                tvLabelDocumentId,
                etDocumentId,
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
        tvFromStageTimeCreated.setOnClickListener(this::startDatePicker);
        Date now = new Date();
        tvFromStageTimeCreated.setText(dateFormat.format(now));
        tvFromStageTimeCreated.setTag(now);
        tvToStageTimeCreated.setOnClickListener(this::startDatePicker);
        tvFromAdjustmentTimeCreated.setOnClickListener(this::startDatePicker);
        tvToAdjustmentTimeCreated.setOnClickListener(this::startDatePicker);
    }

    private void initSpinners() {
        orderBySpinner.init(DisputeSortProperty.values());
        orderBySpinner.selectItem(DisputeSortProperty.FromStageTimeCreated);
        orderSpinner.init(SortDirection.values());
        brandSpinner.init(getResources().getStringArray(R.array.brands_disputes), true);
        statusSpinner.init(DisputeStatus.values(), true);
        stageSpinner.init(DisputeStage.values(), true);
    }

    private DisputesReportParametersModel buildDisputesReportParametersModel() {
        DisputesReportParametersModel disputesReportParametersModel = new DisputesReportParametersModel();

        Integer page = safeParseInt(etPage.getText().toString());
        if (isNotNull(page)) {
            disputesReportParametersModel.setPage(page);
        }

        Integer pageSize = safeParseInt(etPageSize.getText().toString());
        if (isNotNull(pageSize)) {
            disputesReportParametersModel.setPageSize(pageSize);
        }

        disputesReportParametersModel.setOrderBy(orderBySpinner.getSelectedOption());
        disputesReportParametersModel.setOrder(orderSpinner.getSelectedOption());
        disputesReportParametersModel.setArn(etArn.getText().toString());
        disputesReportParametersModel.setBrand(brandSpinner.getSelectedOption());
        disputesReportParametersModel.setStatus(statusSpinner.getSelectedOption());
        disputesReportParametersModel.setStage(stageSpinner.getSelectedOption());
        disputesReportParametersModel.setFromStageTimeCreated(getDate(tvFromStageTimeCreated));
        disputesReportParametersModel.setToStageTimeCreated(getDate(tvToStageTimeCreated));
        disputesReportParametersModel.setFromAdjustmentTimeCreated(getDate(tvFromAdjustmentTimeCreated));
        disputesReportParametersModel.setToAdjustmentTimeCreated(getDate(tvToAdjustmentTimeCreated));
        disputesReportParametersModel.setSystemMID(etSystemMID.getText().toString());
        disputesReportParametersModel.setSystemHierarchy(etSystemHierarchy.getText().toString());

        disputesReportParametersModel.setFromSettlements(cbFromSettlements.isChecked());

        return disputesReportParametersModel;
    }

    private Date getDate(View view) {
        Object tag = view.getTag();

        if (tag instanceof Date) {
            return (Date) tag;
        }

        return null;
    }

    private DocumentReportModel buildDocumentReportModel() {
        DocumentReportModel documentReportModel = new DocumentReportModel();
        documentReportModel.setDisputeId(etDisputeId.getText().toString());
        documentReportModel.setDocumentId(etDocumentId.getText().toString());
        return documentReportModel;
    }

    private void submit() {
        Fragment targetFragment = getTargetFragment();

        if (targetFragment instanceof Callback) {
            Callback callback = (Callback) targetFragment;

            switch (type) {
                case DISPUTE_LIST:
                    DisputesReportParametersModel disputesReportParametersModel = buildDisputesReportParametersModel();
                    callback.onSubmitDisputesReportParametersModel(disputesReportParametersModel);
                    break;
                case DISPUTE_BY_ID:
                    callback.onSubmitDisputeId(etDisputeId.getText().toString(), cbFromSettlements.isChecked());
                    break;
                case DOCUMENT_BY_ID:
                    DocumentReportModel documentReportModel = buildDocumentReportModel();
                    callback.onSubmitDocumentReportModel(documentReportModel);
                    break;
            }
        }

        dismiss();
    }

    public interface Callback {
        void onSubmitDisputesReportParametersModel(DisputesReportParametersModel disputesReportParametersModel);

        void onSubmitDisputeId(String disputeId, boolean fromSettlements);

        void onSubmitDocumentReportModel(DocumentReportModel documentReportModel);
    }

    public enum TYPE {
        DISPUTE_LIST,
        DISPUTE_BY_ID,
        DOCUMENT_BY_ID;
    }
}

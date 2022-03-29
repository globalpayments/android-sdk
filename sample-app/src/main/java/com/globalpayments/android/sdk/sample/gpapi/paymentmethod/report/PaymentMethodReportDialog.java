package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import static com.globalpayments.android.sdk.sample.common.Constants.DIALOG_TYPE;
import static com.globalpayments.android.sdk.utils.DateUtils.YYYY_MM_DD;
import static com.globalpayments.android.sdk.utils.Utils.isNotNull;
import static com.globalpayments.android.sdk.utils.Utils.safeParseInt;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideAllViewsExcluding;

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

import com.global.api.entities.enums.SortDirection;
import com.global.api.entities.enums.StoredPaymentMethodStatus;
import com.global.api.entities.enums.TransactionSortProperty;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomSpinner;
import com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE;
import com.globalpayments.android.sdk.sample.gpapi.transaction.report.model.TransactionReportParameters;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class PaymentMethodReportDialog extends BaseDialogFragment {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD, Locale.getDefault());
    private TYPE type = TYPE.LIST;

    private GridLayout glContainer;
    private TextView tvHeader;
    private TextView tvLabelPaymentMethodId;
    private EditText etPaymentMethodId;
    private TextView tvPaymentsListPage;
    private EditText etPaymentsListPage;
    private TextView tvPaymentsListPageSize;
    private EditText etPaymentsListPageSize;
    private TextView tvLabelOrderBy;
    private CustomSpinner etPaymentsListOrderBy;
    private TextView tvLabelListOrder;
    private CustomSpinner etPaymentsListOrder;
    private TextView tvPaymentsListId;
    private EditText etPaymentsListId;
    private TextView tvPaymentsListReference;
    private EditText etPaymentsListReference;
    private TextView tvLabelStatus;
    private CustomSpinner statusSpinner;
    private TextView tvLabelFromTimeCreated;
    private TextView tvFromTimeCreated;
    private TextView tvLabelToTimeCreated;
    private TextView tvToTimeCreated;
    private TextView tvLabelFromTimeUpdated;
    private TextView tvFromTimeLastUpdated;
    private TextView tvLabelToTimeUpdated;
    private TextView tvtoTimeLastUpdated;
    private Button btSubmit;

    public static PaymentMethodReportDialog newInstance(Fragment targetFragment, TYPE type) {
        PaymentMethodReportDialog paymentMethodReportDialog = new PaymentMethodReportDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable(DIALOG_TYPE, type);
        paymentMethodReportDialog.setArguments(bundle);

        paymentMethodReportDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        paymentMethodReportDialog.setTargetFragment(targetFragment, 0);
        return paymentMethodReportDialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_payment_method_report;
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
        tvLabelPaymentMethodId = findViewById(R.id.tvLabelPaymentMethodId);
        etPaymentMethodId = findViewById(R.id.etPaymentMethodId);
        tvPaymentsListPage = findViewById(R.id.tvPaymentsListPage);
        etPaymentsListPage = findViewById(R.id.etPaymentsListPage);
        tvPaymentsListPageSize = findViewById(R.id.tvPaymentsListPageSize);
        etPaymentsListPageSize = findViewById(R.id.etPaymentsListPageSize);
        tvLabelOrderBy = findViewById(R.id.tvLabelOrderBy);
        etPaymentsListOrderBy = findViewById(R.id.etPaymentsListOrderBy);
        tvLabelListOrder = findViewById(R.id.tvLabelListOrder);
        etPaymentsListOrder = findViewById(R.id.etPaymentsListOrder);
        tvPaymentsListId = findViewById(R.id.tvPaymentsListId);
        etPaymentsListId = findViewById(R.id.etPaymentsListId);
        tvPaymentsListReference = findViewById(R.id.tvPaymentsListReference);
        etPaymentsListReference = findViewById(R.id.etPaymentsListReference);
        tvLabelStatus = findViewById(R.id.tvLabelStatus);
        statusSpinner = findViewById(R.id.statusSpinner);
        tvLabelFromTimeCreated = findViewById(R.id.tvLabelFromTimeCreated);
        tvFromTimeCreated = findViewById(R.id.tvFromTimeCreated);
        tvLabelToTimeCreated = findViewById(R.id.tvLabelToTimeCreated);
        tvToTimeCreated = findViewById(R.id.tvToTimeCreated);
        tvLabelFromTimeUpdated = findViewById(R.id.tvLabelFromTimeUpdated);
        tvFromTimeLastUpdated = findViewById(R.id.tvFromTimeLastUpdated);
        tvLabelToTimeUpdated = findViewById(R.id.tvLabelToTimeUpdated);
        tvtoTimeLastUpdated = findViewById(R.id.tvtoTimeLastUpdated);
        btSubmit = findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(v -> submit());

        configureViews();
    }

    private void configureViews() {
        if (type == TYPE.BY_ID) {
            hideAllViewsExcluding(
                    glContainer, tvHeader, tvLabelPaymentMethodId, etPaymentMethodId, btSubmit);
        } else {
            initSpinners();
            initDatePickers();
            hideAllViewsExcluding(
                    glContainer, tvHeader, tvPaymentsListPage, etPaymentsListPage, tvPaymentsListPageSize,
                    etPaymentsListPageSize, tvLabelOrderBy, etPaymentsListOrderBy, tvLabelListOrder,
                    etPaymentsListOrder, tvPaymentsListId, etPaymentsListId, tvPaymentsListReference,
                    etPaymentsListReference, tvLabelStatus, statusSpinner, tvLabelFromTimeCreated,
                    tvFromTimeCreated, tvLabelToTimeCreated, tvToTimeCreated, tvLabelFromTimeUpdated,
                    tvFromTimeLastUpdated, tvLabelToTimeUpdated, tvtoTimeLastUpdated, btSubmit
            );
        }

        btSubmit.setOnClickListener(v -> submit());
    }

    private void initDatePickers() {
        tvFromTimeCreated.setOnClickListener(this::startDatePicker);
        tvToTimeCreated.setOnClickListener(this::startDatePicker);

        tvFromTimeLastUpdated.setOnClickListener(this::startDatePicker);
        tvtoTimeLastUpdated.setOnClickListener(this::startDatePicker);
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

    private void initSpinners() {
        etPaymentsListOrderBy.init(new TransactionSortProperty[]{TransactionSortProperty.TimeCreated});
        etPaymentsListOrderBy.selectItem(TransactionSortProperty.TimeCreated);

        etPaymentsListOrder.init(SortDirection.values());
        etPaymentsListOrder.selectItem(SortDirection.Descending);

        statusSpinner.init(StoredPaymentMethodStatus.values(), true);
    }

    private TransactionReportParameters buildTransactionReportParameters() {
        TransactionReportParameters transactionReportParameters = new TransactionReportParameters();

        Integer page = safeParseInt(etPaymentsListPage.getText().toString());
        if (isNotNull(page)) {
            transactionReportParameters.setPage(page);
        }

        Integer pageSize = safeParseInt(etPaymentsListPageSize.getText().toString());
        if (isNotNull(pageSize)) {
            transactionReportParameters.setPageSize(pageSize);
        }

        transactionReportParameters.setOrder(etPaymentsListOrder.getSelectedOption());
        transactionReportParameters.setOrderBy(etPaymentsListOrderBy.getSelectedOption());
        transactionReportParameters.setId(etPaymentsListId.getText().toString());
        transactionReportParameters.setReference(etPaymentsListReference.getText().toString());
        transactionReportParameters.setTransactionStatus(statusSpinner.getSelectedOption());
        transactionReportParameters.setFromTimeCreated(getDate(tvFromTimeCreated));
        transactionReportParameters.setToTimeCreated(getDate(tvToTimeCreated));
        transactionReportParameters.setFromTimeLastUpdated(getDate(tvFromTimeLastUpdated));
        transactionReportParameters.setToTimeLastUpdated(getDate(tvtoTimeLastUpdated));

        return transactionReportParameters;
    }

    private void submit() {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof Callback) {
            Callback callback = (Callback) targetFragment;

            if (type == TYPE.BY_ID) {
                callback.onSubmitPaymentMethodId(etPaymentMethodId.getText().toString());
            } else {
                TransactionReportParameters transactionReportParameters = buildTransactionReportParameters();
                callback.onSubmitPaymentsReportParameters(transactionReportParameters);
            }
        }
        dismiss();
    }

    private Date getDate(View view) {
        Object tag = view.getTag();

        if (tag instanceof Date) {
            return (Date) tag;
        }

        return null;
    }

    public interface Callback {
        void onSubmitPaymentMethodId(String paymentMethodId);
        void onSubmitPaymentsReportParameters(TransactionReportParameters transactionReportParameters);
    }

}

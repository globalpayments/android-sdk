package com.globalpayments.android.sdk.sample.gpapi.deposits;

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

import com.global.api.entities.enums.DepositSortProperty;
import com.global.api.entities.enums.DepositStatus;
import com.global.api.entities.enums.SortDirection;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomSpinner;
import com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE;
import com.globalpayments.android.sdk.sample.gpapi.deposits.model.DepositParametersModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.globalpayments.android.sdk.sample.common.Constants.DIALOG_TYPE;
import static com.globalpayments.android.sdk.utils.DateUtils.YYYY_MM_DD;
import static com.globalpayments.android.sdk.utils.Utils.isNotNull;
import static com.globalpayments.android.sdk.utils.Utils.safeParseBigDecimal;
import static com.globalpayments.android.sdk.utils.Utils.safeParseInt;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideAllViewsExcluding;

public class DepositsDialog extends BaseDialogFragment {
    private static final String DEPOSIT_ID_EXAMPLE = "DEP_2342423435";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD, Locale.getDefault());

    private GridLayout glContainer;
    private TextView tvHeader;
    private EditText etPage;
    private EditText etPageSize;
    private CustomSpinner orderBySpinner;
    private CustomSpinner orderSpinner;
    private TextView tvFromTimeCreated;
    private TextView tvLabelId;
    private EditText etId;
    private CustomSpinner statusSpinner;
    private TextView tvToTimeCreated;
    private EditText etAmount;
    private EditText etMaskedAccountNumberLast4;
    private EditText etSystemMID;
    private EditText etSystemHierarchy;
    private Button btSubmit;

    private TYPE type = TYPE.LIST;

    public static DepositsDialog newInstance(Fragment targetFragment, TYPE type) {
        DepositsDialog depositsDialog = new DepositsDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable(DIALOG_TYPE, type);
        depositsDialog.setArguments(bundle);

        depositsDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        depositsDialog.setTargetFragment(targetFragment, 0);

        return depositsDialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_deposits;
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
        tvFromTimeCreated = findViewById(R.id.tvFromTimeCreated);
        tvLabelId = findViewById(R.id.tvLabelId);
        etId = findViewById(R.id.etId);
        statusSpinner = findViewById(R.id.statusSpinner);
        tvToTimeCreated = findViewById(R.id.tvToTimeCreated);
        etAmount = findViewById(R.id.etAmount);
        etMaskedAccountNumberLast4 = findViewById(R.id.etMaskedAccountNumberLast4);
        etSystemMID = findViewById(R.id.etSystemMID);
        etSystemHierarchy = findViewById(R.id.etSystemHierarchy);
        btSubmit = findViewById(R.id.btSubmit);

        if (type == TYPE.BY_ID) {
            hideAllViewsExcluding(glContainer, tvHeader, tvLabelId, etId, btSubmit);
            etId.setText(DEPOSIT_ID_EXAMPLE);
        } else {
            initSpinners();
            tvFromTimeCreated.setOnClickListener(this::startDatePicker);
            tvToTimeCreated.setOnClickListener(this::startDatePicker);
        }

        btSubmit.setOnClickListener(v -> submit());
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
        orderBySpinner.init(DepositSortProperty.values());
        orderSpinner.init(SortDirection.values());
        orderSpinner.selectItem(SortDirection.Descending);
        statusSpinner.init(DepositStatus.values(), true);
    }

    private DepositParametersModel buildDepositParametersModel() {
        DepositParametersModel depositParametersModel = new DepositParametersModel();

        Integer page = safeParseInt(etPage.getText().toString());
        if (isNotNull(page)) {
            depositParametersModel.setPage(page);
        }

        Integer pageSize = safeParseInt(etPageSize.getText().toString());
        if (isNotNull(pageSize)) {
            depositParametersModel.setPageSize(pageSize);
        }

        depositParametersModel.setOrderBy(orderBySpinner.getSelectedOption());
        depositParametersModel.setOrder(orderSpinner.getSelectedOption());
        depositParametersModel.setFromTimeCreated(getDate(tvFromTimeCreated));
        depositParametersModel.setId(etId.getText().toString());
        depositParametersModel.setStatus(statusSpinner.getSelectedOption());
        depositParametersModel.setToTimeCreated(getDate(tvToTimeCreated));

        BigDecimal amount = safeParseBigDecimal(etAmount.getText().toString());
        if (isNotNull(amount)) {
            depositParametersModel.setAmount(amount);
        }

        depositParametersModel.setMaskedAccountNumberLast4(etMaskedAccountNumberLast4.getText().toString());
        depositParametersModel.setSystemMID(etSystemMID.getText().toString());
        depositParametersModel.setSystemHierarchy(etSystemHierarchy.getText().toString());
        
        return depositParametersModel;
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

            if (type == TYPE.BY_ID) {
                callback.onSubmitDepositId(etId.getText().toString());
            } else {
                DepositParametersModel depositParametersModel = buildDepositParametersModel();
                callback.onSubmitDepositParametersModel(depositParametersModel);
            }
        }

        dismiss();
    }

    public interface Callback {
        void onSubmitDepositParametersModel(DepositParametersModel depositParametersModel);

        void onSubmitDepositId(String depositId);
    }
}

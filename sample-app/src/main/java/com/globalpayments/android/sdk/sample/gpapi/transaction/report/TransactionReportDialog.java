package com.globalpayments.android.sdk.sample.gpapi.transaction.report;

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

import com.global.api.entities.enums.Channel;
import com.global.api.entities.enums.DepositStatus;
import com.global.api.entities.enums.PaymentEntryMode;
import com.global.api.entities.enums.PaymentType;
import com.global.api.entities.enums.SortDirection;
import com.global.api.entities.enums.TransactionSortProperty;
import com.global.api.entities.enums.TransactionStatus;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomSpinner;
import com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE;
import com.globalpayments.android.sdk.sample.gpapi.transaction.report.model.TransactionReportParameters;

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
import static com.globalpayments.android.sdk.utils.ViewUtils.handleViewsVisibility;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideAllViewsExcluding;

public class TransactionReportDialog extends BaseDialogFragment {
    private static final String TRANSACTION_ID_EXAMPLE = "TRN_7g3faeVD43hkwAQ44k5vgTzl4tb1Ep";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD, Locale.getDefault());
    private TYPE type = TYPE.LIST;

    private GridLayout glContainer;
    private TextView tvHeader;
    private CheckBox cbFromSettlements;
    private EditText etPage;
    private EditText etPageSize;
    private CustomSpinner orderBySpinner;
    private CustomSpinner orderSpinner;
    private EditText etNumberFirst6;
    private EditText etNumberLast4;
    private CustomSpinner brandSpinner;
    private EditText etBrandReference;
    private EditText etAuthCode;
    private EditText etReference;
    private CustomSpinner statusSpinner;
    private TextView tvFromTimeCreated;
    private TextView tvToTimeCreated;
    private Button btSubmit;

    //Non Settlement views
    private TextView tvLabelId;
    private EditText etId;
    private TextView tvLabelType;
    private CustomSpinner typesSpinner;
    private TextView tvLabelChannel;
    private CustomSpinner channelSpinner;
    private TextView tvLabelAmount;
    private EditText etAmount;
    private TextView tvLabelCurrency;
    private CustomSpinner currencySpinner;
    private TextView tvLabelTokenFirst6;
    private EditText etTokenFirst6;
    private TextView tvLabelTokenLast4;
    private EditText etTokenLast4;
    private TextView tvLabelAccountName;
    private EditText etAccountName;
    private TextView tvLabelCountry;
    private EditText etCountry;
    private TextView tvLabelBatchId;
    private EditText etBatchId;
    private TextView tvLabelEntryMode;
    private CustomSpinner entryModeSpinner;
    private TextView tvLabelName;
    private EditText etName;

    //Settlement views
    private TextView tvLabelDepositStatus;
    private CustomSpinner depositStatusSpinner;
    private TextView tvLabelArn;
    private EditText etArn;
    private TextView tvLabelDepositId;
    private EditText etDepositId;
    private TextView tvLabelFromDepositTimeCreated;
    private TextView tvFromDepositTimeCreated;
    private TextView tvLabelToDepositTimeCreated;
    private TextView tvToDepositTimeCreated;
    private TextView tvLabelFromBatchTimeCreated;
    private TextView tvFromBatchTimeCreated;
    private TextView tvLabelToBatchTimeCreated;
    private TextView tvToBatchTimeCreated;
    private TextView tvLabelSystemMID;
    private EditText etSystemMID;
    private TextView tvLabelSystemHierarchy;
    private EditText etSystemHierarchy;

    public static TransactionReportDialog newInstance(Fragment targetFragment, TYPE type) {
        TransactionReportDialog transactionReportDialog = new TransactionReportDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable(DIALOG_TYPE, type);
        transactionReportDialog.setArguments(bundle);

        transactionReportDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        transactionReportDialog.setTargetFragment(targetFragment, 0);

        return transactionReportDialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_transaction_report;
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
        cbFromSettlements = findViewById(R.id.cbFromSettlements);
        etPage = findViewById(R.id.etPage);
        etPageSize = findViewById(R.id.etPageSize);
        orderBySpinner = findViewById(R.id.orderBySpinner);
        orderSpinner = findViewById(R.id.orderSpinner);
        etNumberFirst6 = findViewById(R.id.etNumberFirst6);
        etNumberLast4 = findViewById(R.id.etNumberLast4);
        brandSpinner = findViewById(R.id.brandSpinner);
        etBrandReference = findViewById(R.id.etBrandReference);
        etAuthCode = findViewById(R.id.etAuthCode);
        etReference = findViewById(R.id.etReference);
        statusSpinner = findViewById(R.id.statusSpinner);
        tvFromTimeCreated = findViewById(R.id.tvFromTimeCreated);
        tvToTimeCreated = findViewById(R.id.tvToTimeCreated);
        btSubmit = findViewById(R.id.btSubmit);

        tvLabelId = findViewById(R.id.tvLabelId);
        etId = findViewById(R.id.etId);
        tvLabelType = findViewById(R.id.tvLabelType);
        typesSpinner = findViewById(R.id.typesSpinner);
        tvLabelChannel = findViewById(R.id.tvLabelChannel);
        channelSpinner = findViewById(R.id.channelSpinner);
        tvLabelAmount = findViewById(R.id.tvLabelAmount);
        etAmount = findViewById(R.id.etAmount);
        tvLabelCurrency = findViewById(R.id.tvLabelCurrency);
        currencySpinner = findViewById(R.id.currencySpinner);
        tvLabelTokenFirst6 = findViewById(R.id.tvLabelTokenFirst6);
        etTokenFirst6 = findViewById(R.id.etTokenFirst6);
        tvLabelTokenLast4 = findViewById(R.id.tvLabelTokenLast4);
        etTokenLast4 = findViewById(R.id.etTokenLast4);
        tvLabelAccountName = findViewById(R.id.tvLabelAccountName);
        etAccountName = findViewById(R.id.etAccountName);
        tvLabelCountry = findViewById(R.id.tvLabelCountry);
        etCountry = findViewById(R.id.etCountry);
        tvLabelBatchId = findViewById(R.id.tvLabelBatchId);
        etBatchId = findViewById(R.id.etBatchId);
        tvLabelEntryMode = findViewById(R.id.tvLabelEntryMode);
        entryModeSpinner = findViewById(R.id.entryModeSpinner);
        tvLabelName = findViewById(R.id.tvLabelName);
        etName = findViewById(R.id.etName);

        tvLabelDepositStatus = findViewById(R.id.tvLabelDepositStatus);
        depositStatusSpinner = findViewById(R.id.depositStatusSpinner);
        tvLabelArn = findViewById(R.id.tvLabelArn);
        etArn = findViewById(R.id.etArn);
        tvLabelDepositId = findViewById(R.id.tvLabelDepositId);
        etDepositId = findViewById(R.id.etDepositId);
        tvLabelFromDepositTimeCreated = findViewById(R.id.tvLabelFromDepositTimeCreated);
        tvFromDepositTimeCreated = findViewById(R.id.tvFromDepositTimeCreated);
        tvLabelToDepositTimeCreated = findViewById(R.id.tvLabelToDepositTimeCreated);
        tvToDepositTimeCreated = findViewById(R.id.tvToDepositTimeCreated);
        tvLabelFromBatchTimeCreated = findViewById(R.id.tvLabelFromBatchTimeCreated);
        tvFromBatchTimeCreated = findViewById(R.id.tvFromBatchTimeCreated);
        tvLabelToBatchTimeCreated = findViewById(R.id.tvLabelToBatchTimeCreated);
        tvToBatchTimeCreated = findViewById(R.id.tvToBatchTimeCreated);
        tvLabelSystemMID = findViewById(R.id.tvLabelSystemMID);
        etSystemMID = findViewById(R.id.etSystemMID);
        tvLabelSystemHierarchy = findViewById(R.id.tvLabelSystemHierarchy);
        etSystemHierarchy = findViewById(R.id.etSystemHierarchy);

        configureViews();
    }

    private void configureViews() {
        if (type == TYPE.BY_ID) {
            hideAllViewsExcluding(glContainer, tvHeader, tvLabelId, etId, btSubmit);
            etId.setText(TRANSACTION_ID_EXAMPLE);
        } else {
            initSpinners();
            initDatePickers();
            cbFromSettlements.setOnCheckedChangeListener((buttonView, isChecked) -> handleSettlementViewsVisibility(isChecked));
            handleSettlementViewsVisibility(false);
        }

        btSubmit.setOnClickListener(v -> submit());
    }

    private void initDatePickers() {
        tvFromTimeCreated.setOnClickListener(this::startDatePicker);
        tvToTimeCreated.setOnClickListener(this::startDatePicker);

        tvFromDepositTimeCreated.setOnClickListener(this::startDatePicker);
        tvToDepositTimeCreated.setOnClickListener(this::startDatePicker);
        tvFromBatchTimeCreated.setOnClickListener(this::startDatePicker);
        tvToBatchTimeCreated.setOnClickListener(this::startDatePicker);
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
        orderBySpinner.init(new TransactionSortProperty[]{TransactionSortProperty.Id, TransactionSortProperty.TimeCreated, TransactionSortProperty.Type});
        orderBySpinner.selectItem(TransactionSortProperty.TimeCreated);

        orderSpinner.init(SortDirection.values());
        orderSpinner.selectItem(SortDirection.Descending);

        typesSpinner.init(PaymentType.values(), true);
        channelSpinner.init(Channel.values(), true);
        currencySpinner.init(getResources().getStringArray(R.array.currencies_transaction_report), true);
        brandSpinner.init(getResources().getStringArray(R.array.brands), true);
        statusSpinner.init(TransactionStatus.values(), true);
        entryModeSpinner.init(PaymentEntryMode.values(), true);

        depositStatusSpinner.init(DepositStatus.values(), true);
    }

    private void handleSettlementViewsVisibility(boolean fromSettlements) {
        handleViewsVisibility(fromSettlements,
                tvLabelDepositStatus,
                depositStatusSpinner,
                tvLabelArn,
                etArn,
                tvLabelDepositId,
                etDepositId,
                tvLabelFromDepositTimeCreated,
                tvFromDepositTimeCreated,
                tvLabelToDepositTimeCreated,
                tvToDepositTimeCreated,
                tvLabelFromBatchTimeCreated,
                tvFromBatchTimeCreated,
                tvLabelToBatchTimeCreated,
                tvToBatchTimeCreated,
                tvLabelSystemMID,
                etSystemMID,
                tvLabelSystemHierarchy,
                etSystemHierarchy
        );

        handleViewsVisibility(!fromSettlements,
                tvLabelId,
                etId,
                tvLabelType,
                typesSpinner,
                tvLabelChannel,
                channelSpinner,
                tvLabelAmount,
                etAmount,
                tvLabelCurrency,
                currencySpinner,
                tvLabelTokenFirst6,
                etTokenFirst6,
                tvLabelTokenLast4,
                etTokenLast4,
                tvLabelAccountName,
                etAccountName,
                tvLabelCountry,
                etCountry,
                tvLabelBatchId,
                etBatchId,
                tvLabelEntryMode,
                entryModeSpinner,
                tvLabelName,
                etName);
    }

    private TransactionReportParameters buildTransactionReportParameters() {
        TransactionReportParameters transactionReportParameters = new TransactionReportParameters();
        boolean fromSettlements = cbFromSettlements.isChecked();
        transactionReportParameters.setFromSettlements(fromSettlements);

        Integer page = safeParseInt(etPage.getText().toString());
        if (isNotNull(page)) {
            transactionReportParameters.setPage(page);
        }

        Integer pageSize = safeParseInt(etPageSize.getText().toString());
        if (isNotNull(pageSize)) {
            transactionReportParameters.setPageSize(pageSize);
        }

        transactionReportParameters.setOrder(orderSpinner.getSelectedOption());
        transactionReportParameters.setOrderBy(orderBySpinner.getSelectedOption());
        transactionReportParameters.setNumberFirst6(etNumberFirst6.getText().toString());
        transactionReportParameters.setNumberLast4(etNumberLast4.getText().toString());
        transactionReportParameters.setBrand(brandSpinner.getSelectedOption());
        transactionReportParameters.setBrandReference(etBrandReference.getText().toString());
        transactionReportParameters.setAuthCode(etAuthCode.getText().toString());
        transactionReportParameters.setDepositReference(etReference.getText().toString());
        transactionReportParameters.setStatus(statusSpinner.getSelectedOption());
        transactionReportParameters.setFromTimeCreated(getDate(tvFromTimeCreated));
        transactionReportParameters.setToTimeCreated(getDate(tvToTimeCreated));

        if (fromSettlements) {
            transactionReportParameters.setDepositStatus(depositStatusSpinner.getSelectedOption());
            transactionReportParameters.setArn(etArn.getText().toString());
            transactionReportParameters.setFromDepositTimeCreated(getDate(tvFromDepositTimeCreated));
            transactionReportParameters.setToDepositTimeCreated(getDate(tvToDepositTimeCreated));
            transactionReportParameters.setFromBatchTimeCreated(getDate(tvFromBatchTimeCreated));
            transactionReportParameters.setToBatchTimeCreated(getDate(tvToBatchTimeCreated));
            transactionReportParameters.setSystemMID(etSystemMID.getText().toString());
            transactionReportParameters.setSystemHierarchy(etSystemHierarchy.getText().toString());
        } else {
            transactionReportParameters.setId(etId.getText().toString());
            transactionReportParameters.setType(typesSpinner.getSelectedOption());
            transactionReportParameters.setChannel(channelSpinner.getSelectedOption());

            BigDecimal amount = safeParseBigDecimal(etAmount.getText().toString());
            if (isNotNull(amount)) {
                transactionReportParameters.setAmount(amount);
            }

            transactionReportParameters.setCurrency(currencySpinner.getSelectedOption());
            transactionReportParameters.setTokenFirst6(etTokenFirst6.getText().toString());
            transactionReportParameters.setTokenLast4(etTokenLast4.getText().toString());
            transactionReportParameters.setAccountName(etAccountName.getText().toString());
            transactionReportParameters.setCountry(etCountry.getText().toString());
            transactionReportParameters.setBatchId(etBatchId.getText().toString());
            transactionReportParameters.setEntryMode(entryModeSpinner.getSelectedOption());
            transactionReportParameters.setName(etName.getText().toString());
        }

        return transactionReportParameters;
    }

    private void submit() {
        Fragment targetFragment = getTargetFragment();

        if (targetFragment instanceof Callback) {
            Callback callback = (Callback) targetFragment;

            if (type == TYPE.BY_ID) {
                callback.onSubmitTransactionId(etId.getText().toString());
            } else {
                TransactionReportParameters transactionReportParameters = buildTransactionReportParameters();
                callback.onSubmitTransactionReportParameters(transactionReportParameters);
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
        void onSubmitTransactionReportParameters(TransactionReportParameters transactionReportParameters);

        void onSubmitTransactionId(String transactionId);
    }

}
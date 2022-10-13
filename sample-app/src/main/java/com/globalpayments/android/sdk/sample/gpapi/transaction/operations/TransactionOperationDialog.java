package com.globalpayments.android.sdk.sample.gpapi.transaction.operations;

import static com.globalpayments.android.sdk.utils.Utils.safeParseBigDecimal;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.global.api.entities.enums.Channel;
import com.globalpayments.android.sdk.model.PaymentCardModel;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomSpinner;
import com.globalpayments.android.sdk.sample.gpapi.configuration.GPAPIConfiguration;
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationModel;
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationType;
import com.globalpayments.android.sdk.sample.utils.AppPreferences;
import com.globalpayments.android.sdk.sample.utils.FingerprintMethodUsageMode;
import com.globalpayments.android.sdk.sample.utils.ManualEntryMethodUsageMode;
import com.globalpayments.android.sdk.sample.utils.PaymentMethodUsageMode;

import java.math.BigDecimal;

public class TransactionOperationDialog extends BaseDialogFragment {
    private CustomSpinner currenciesSpinner;
    private CustomSpinner transactionTypeSpinner;
    private CustomSpinner cardModelsSpinner;
    private CustomSpinner multiTokenSpinner;
    private CustomSpinner manualEntryModeSpinner;
    private CheckBox cbRequestMultiUseToken;
    private CheckBox cbFromFingerPrint;
    private EditText etCardNumber;
    private EditText etExpiryMonth;
    private EditText etExpiryYear;
    private EditText etCvnCvv;
    private EditText etAmount;
    private EditText etIdempotencyKey;
    private EditText etDynamicDescriptor;
    private TextView tvFingerPrintId;
    private TextView etPaymentLinkId;
    private TextView tvManualEntryMode;
    private CustomSpinner multiFingerPrintUsageMode;

    GPAPIConfiguration gpapiConfiguration;

    public static TransactionOperationDialog newInstance(Fragment targetFragment) {
        TransactionOperationDialog transactionOperationDialog = new TransactionOperationDialog();
        transactionOperationDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        transactionOperationDialog.setTargetFragment(targetFragment, 0);
        return transactionOperationDialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_transaction_operation;
    }

    @Override
    protected void initViews() {
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiryMonth = findViewById(R.id.etExpiryMonth);
        etExpiryYear = findViewById(R.id.etExpiryYear);
        etCvnCvv = findViewById(R.id.etCvnCvv);
        etAmount = findViewById(R.id.etAmount);
        etIdempotencyKey = findViewById(R.id.etIdempotencyKey);
        currenciesSpinner = findViewById(R.id.currenciesSpinner);
        transactionTypeSpinner = findViewById(R.id.transactionTypeSpinner);
        multiTokenSpinner = findViewById(R.id.multiTokenSpinner);
        cbRequestMultiUseToken = findViewById(R.id.cbRequestMultiUseToken);
        cbFromFingerPrint = findViewById(R.id.cbFromFingerPrint);
        tvFingerPrintId = findViewById(R.id.tvFingerPrintId);
        multiFingerPrintUsageMode = findViewById(R.id.multiFingerPrintUsageMode);
        etPaymentLinkId = findViewById(R.id.etPaymentLinkId);
        etDynamicDescriptor = findViewById(R.id.etDynamicDescriptor);
        tvManualEntryMode = findViewById(R.id.tvManualEntryMode);
        manualEntryModeSpinner = findViewById(R.id.manualEntryModeSpinner);
        initSpinners();
        Button btSubmit = findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(v -> submitTransactionOperationModel());
        cbFromFingerPrint.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                tvFingerPrintId.setVisibility(View.VISIBLE);
                multiFingerPrintUsageMode.setVisibility(View.VISIBLE);
            } else {
                tvFingerPrintId.setVisibility(View.GONE);
                multiFingerPrintUsageMode.setVisibility(View.GONE);
            }
        });

        AppPreferences appPreferences = new AppPreferences(requireContext());
        gpapiConfiguration = appPreferences.getGPAPIConfiguration();

        if (gpapiConfiguration.getChannel() == Channel.CardNotPresent) {
            tvManualEntryMode.setVisibility(View.VISIBLE);
            manualEntryModeSpinner.setVisibility(View.VISIBLE);
            manualEntryModeSpinner.init(ManualEntryMethodUsageMode.values());
        } else {
            tvManualEntryMode.setVisibility(View.GONE);
            manualEntryModeSpinner.setVisibility(View.GONE);
        }
    }

    private void initSpinners() {
        transactionTypeSpinner.init(TransactionOperationType.values());
        currenciesSpinner.init(getResources().getStringArray(R.array.currencies));
        multiTokenSpinner.init(PaymentMethodUsageMode.values());
        cardModelsSpinner = findViewById(R.id.cardModelsSpinner);
        cardModelsSpinner.init(PaymentCardModel.values(), false, this::fillPaymentCardFields);
        multiFingerPrintUsageMode.init(FingerprintMethodUsageMode.values());
    }

    private void fillPaymentCardFields(PaymentCardModel paymentCardModel) {
        etCardNumber.setText(paymentCardModel.getCardNumber());
        etExpiryMonth.setText(paymentCardModel.getExpiryMonth());
        etExpiryYear.setText(paymentCardModel.getExpiryYear());
        etCvnCvv.setText(paymentCardModel.getCvnCvv());
    }

    private void submitTransactionOperationModel() {
        BigDecimal amount = safeParseBigDecimal(etAmount.getText().toString());
        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof TransactionOperationDialog.Callback) {
            TransactionOperationDialog.Callback callback = (TransactionOperationDialog.Callback) targetFragment;
            TransactionOperationModel transactionOperationModel = new TransactionOperationModel();
            boolean fingerPrint = cbFromFingerPrint.isChecked();
            transactionOperationModel.setFingerPrintSelection(fingerPrint);
            transactionOperationModel.setFingerprintMethodUsageMode(multiFingerPrintUsageMode.getSelectedOption());
            transactionOperationModel.setCardNumber(etCardNumber.getText().toString());
            transactionOperationModel.setExpiryMonth(Integer.parseInt(etExpiryMonth.getText().toString()));
            transactionOperationModel.setExpiryYear(Integer.parseInt(etExpiryYear.getText().toString()));
            transactionOperationModel.setCvnCvv(etCvnCvv.getText().toString());
            transactionOperationModel.setAmount(amount == null ? new BigDecimal(0) : amount);
            transactionOperationModel.setCurrency(currenciesSpinner.getSelectedOption());
            transactionOperationModel.setTransactionOperationType(transactionTypeSpinner.getSelectedOption());
            transactionOperationModel.setIdempotencyKey(etIdempotencyKey.getText().toString());
            transactionOperationModel.setRequestMultiUseToken(cbRequestMultiUseToken.isChecked());
            transactionOperationModel.setPaymentMethodUsageMode(multiTokenSpinner.getSelectedOption());
            transactionOperationModel.setPaymentLinkId(etPaymentLinkId.getText().toString());
            transactionOperationModel.setDynamicDescriptor(etDynamicDescriptor.getText().toString());
            transactionOperationModel.setManualEntryMethodUsageMode(manualEntryModeSpinner.getSelectedOption());
            callback.onSubmitTransactionOperationModel(transactionOperationModel);
        }
        dismiss();
    }

    public interface Callback {
        void onSubmitTransactionOperationModel(TransactionOperationModel transactionOperationModel);
    }

}

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
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationModel;
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationType;
import com.globalpayments.android.sdk.sample.utils.FingerprintMethodUsageMode;
import com.globalpayments.android.sdk.sample.utils.ManualEntryMethodUsageMode;
import com.globalpayments.android.sdk.sample.utils.PaymentMethodUsageMode;
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfiguration;

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

        gpapiConfiguration = GPAPIConfiguration.createDefaultConfig();

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
        transactionTypeSpinner.init(TransactionOperationType.getRoots());
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
            TransactionOperationModel transactionOperationModel = new TransactionOperationModel(
                    etCardNumber.getText().toString(),
                    Integer.parseInt(etExpiryMonth.getText().toString()),
                    Integer.parseInt(etExpiryYear.getText().toString()),
                    etCvnCvv.getText().toString(),
                    amount == null ? new BigDecimal(0) : amount,
                    currenciesSpinner.getSelectedOption(),
                    transactionTypeSpinner.getSelectedOption(),
                    multiTokenSpinner.getSelectedOption(),
                    cbRequestMultiUseToken.isChecked(),
                    multiFingerPrintUsageMode.getSelectedOption(),
                    etIdempotencyKey.getText().toString(),
                    cbFromFingerPrint.isChecked(),
                    etPaymentLinkId.getText().toString(),
                    etDynamicDescriptor.getText().toString(),
                    manualEntryModeSpinner.getSelectedOption()
            );
            callback.onSubmitTransactionOperationModel(transactionOperationModel);
        }
        dismiss();
    }

    public interface Callback {
        void onSubmitTransactionOperationModel(TransactionOperationModel transactionOperationModel);
    }

}

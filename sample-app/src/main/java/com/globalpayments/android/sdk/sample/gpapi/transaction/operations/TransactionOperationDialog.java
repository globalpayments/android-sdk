package com.globalpayments.android.sdk.sample.gpapi.transaction.operations;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.globalpayments.android.sdk.model.PaymentCardModel;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomSpinner;
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationModel;
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationType;
import com.globalpayments.android.sdk.sample.utils.FingerprintMethodUsageMode;
import com.globalpayments.android.sdk.sample.utils.PaymentMethodUsageMode;

import java.math.BigDecimal;

import static com.globalpayments.android.sdk.utils.Utils.safeParseBigDecimal;

public class TransactionOperationDialog extends BaseDialogFragment {
    private CustomSpinner currenciesSpinner;
    private CustomSpinner transactionTypeSpinner;
    private CustomSpinner cardModelsSpinner;
    private CustomSpinner multiTokenSpinner;
    private CheckBox cbRequestMultiUseToken;
    private CheckBox cbFromFingerPrint;
    private EditText etCardNumber;
    private EditText etExpiryMonth;
    private EditText etExpiryYear;
    private EditText etCvnCvv;
    private EditText etAmount;
    private EditText etIdempotencyKey;
    private TextView tvFingerPrintId;
    private CustomSpinner multiFingerPrintUsageMode;
    private boolean use3ds;

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
        CheckBox cbUse3ds = findViewById(R.id.cbUse3ds);
        cbUse3ds.setOnCheckedChangeListener((buttonView, isChecked) -> use3ds = isChecked);
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
            transactionOperationModel.setTypeCardOption(cardModelsSpinner.getSelectedOption().toString());
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
            transactionOperationModel.setUse3DS(use3ds);
            callback.onSubmitTransactionOperationModel(transactionOperationModel);
        }
        dismiss();
    }

    public interface Callback {
        void onSubmitTransactionOperationModel(TransactionOperationModel transactionOperationModel);
    }

}

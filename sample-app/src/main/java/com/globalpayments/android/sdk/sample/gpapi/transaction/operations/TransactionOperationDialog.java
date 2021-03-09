package com.globalpayments.android.sdk.sample.gpapi.transaction.operations;

import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.globalpayments.android.sdk.model.PaymentCardModel;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomSpinner;
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationModel;
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationType;

import java.math.BigDecimal;

import static com.globalpayments.android.sdk.utils.Utils.safeParseBigDecimal;

public class TransactionOperationDialog extends BaseDialogFragment {
    private CustomSpinner currenciesSpinner;
    private CustomSpinner transactionTypeSpinner;
    private EditText etCardNumber;
    private EditText etExpiryMonth;
    private EditText etExpiryYear;
    private EditText etCvnCvv;
    private EditText etAmount;
    private EditText etIdempotencyKey;

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
        initSpinners();

        Button btSubmit = findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(v -> submitTransactionOperationModel());
    }

    private void initSpinners() {
        transactionTypeSpinner.init(TransactionOperationType.values());
        currenciesSpinner.init(getResources().getStringArray(R.array.currencies));

        CustomSpinner cardModelsSpinner = findViewById(R.id.cardModelsSpinner);
        cardModelsSpinner.init(PaymentCardModel.values(), false, this::fillPaymentCardFields);
    }

    private void fillPaymentCardFields(PaymentCardModel paymentCardModel) {
        etCardNumber.setText(paymentCardModel.getCardNumber());
        etExpiryMonth.setText(paymentCardModel.getExpiryMonth());
        etExpiryYear.setText(paymentCardModel.getExpiryYear());
        etCvnCvv.setText(paymentCardModel.getCvnCvv());
    }

    private TransactionOperationModel buildTransactionOperationModel() {
        TransactionOperationModel transactionOperationModel = new TransactionOperationModel();

        transactionOperationModel.setCardNumber(etCardNumber.getText().toString());
        transactionOperationModel.setExpiryMonth(Integer.parseInt(etExpiryMonth.getText().toString()));
        transactionOperationModel.setExpiryYear(Integer.parseInt(etExpiryYear.getText().toString()));
        transactionOperationModel.setCvnCvv(etCvnCvv.getText().toString());

        BigDecimal amount = safeParseBigDecimal(etAmount.getText().toString());
        transactionOperationModel.setAmount(amount == null ? new BigDecimal(0) : amount);

        transactionOperationModel.setCurrency(currenciesSpinner.getSelectedOption());
        transactionOperationModel.setTransactionOperationType(transactionTypeSpinner.getSelectedOption());
        transactionOperationModel.setIdempotencyKey(etIdempotencyKey.getText().toString());

        return transactionOperationModel;
    }

    private void submitTransactionOperationModel() {
        TransactionOperationModel transactionOperationModel = buildTransactionOperationModel();

        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof TransactionOperationDialog.Callback) {
            TransactionOperationDialog.Callback callback = (TransactionOperationDialog.Callback) targetFragment;
            callback.onSubmitTransactionOperationModel(transactionOperationModel);
        }
        dismiss();
    }

    public interface Callback {
        void onSubmitTransactionOperationModel(TransactionOperationModel transactionOperationModel);
    }
}

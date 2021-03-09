package com.globalpayments.android.sdk.sample.gpapi.verifications;

import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.globalpayments.android.sdk.model.PaymentCardModel;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomSpinner;
import com.globalpayments.android.sdk.sample.gpapi.verifications.model.VerificationsModel;

public class VerificationsDialog extends BaseDialogFragment {
    private CustomSpinner currenciesSpinner;
    private EditText etCardNumber;
    private EditText etExpiryMonth;
    private EditText etExpiryYear;
    private EditText etCvnCvv;

    private EditText etIdempotencyKey;

    public static VerificationsDialog newInstance(Fragment targetFragment) {
        VerificationsDialog verificationsDialog = new VerificationsDialog();
        verificationsDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        verificationsDialog.setTargetFragment(targetFragment, 0);
        return verificationsDialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_verifications;
    }

    @Override
    protected void initViews() {
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiryMonth = findViewById(R.id.etExpiryMonth);
        etExpiryYear = findViewById(R.id.etExpiryYear);
        etCvnCvv = findViewById(R.id.etCvnCvv);
        currenciesSpinner = findViewById(R.id.currenciesSpinner);
        etIdempotencyKey = findViewById(R.id.etIdempotencyKey);
        initSpinners();
        
        Button btSubmit = findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(v -> submitVerificationsModel());
    }

    private void initSpinners() {
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

    private VerificationsModel buildVerificationsModel() {
        VerificationsModel verificationsModel = new VerificationsModel();
        verificationsModel.setCardNumber(etCardNumber.getText().toString());
        verificationsModel.setExpiryMonth(Integer.parseInt(etExpiryMonth.getText().toString()));
        verificationsModel.setExpiryYear(Integer.parseInt(etExpiryYear.getText().toString()));
        verificationsModel.setCvnCvv(etCvnCvv.getText().toString());
        verificationsModel.setCurrency(currenciesSpinner.getSelectedOption());
        verificationsModel.setIdempotencyKey(etIdempotencyKey.getText().toString());
        return verificationsModel;
    }

    private void submitVerificationsModel() {
        VerificationsModel verificationsModel = buildVerificationsModel();

        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof Callback) {
            Callback callback = (Callback) targetFragment;
            callback.onSubmitVerificationsModel(verificationsModel);
        }

        dismiss();
    }

    public interface Callback {
        void onSubmitVerificationsModel(VerificationsModel verificationsModel);
    }
}

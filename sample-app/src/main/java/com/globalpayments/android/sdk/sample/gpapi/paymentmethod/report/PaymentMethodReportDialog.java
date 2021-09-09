package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;

public class PaymentMethodReportDialog extends BaseDialogFragment {

    private EditText etPaymentMethodId;
    private Button btSubmit;

    public static PaymentMethodReportDialog newInstance(Fragment targetFragment) {
        PaymentMethodReportDialog paymentMethodReportDialog = new PaymentMethodReportDialog();
        paymentMethodReportDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        paymentMethodReportDialog.setTargetFragment(targetFragment, 0);
        return paymentMethodReportDialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_payment_method_report;
    }

    @Override
    protected void initViews() {
        etPaymentMethodId = findViewById(R.id.etPaymentMethodId);
        btSubmit = findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(v -> submit());
    }

    private void submit() {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof Callback) {
            Callback callback = (Callback) targetFragment;
            callback.onSubmitPaymentMethodId(etPaymentMethodId.getText().toString());
        }
        dismiss();
    }

    public interface Callback {
        void onSubmitPaymentMethodId(String paymentMethodId);
    }

}

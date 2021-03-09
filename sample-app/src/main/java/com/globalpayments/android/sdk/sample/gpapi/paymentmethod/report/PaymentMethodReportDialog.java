package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;
import com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE;
import com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report.model.PaymentMethodReportParameters;

import java.io.Serializable;

import static com.globalpayments.android.sdk.sample.common.Constants.DIALOG_TYPE;

public class PaymentMethodReportDialog extends BaseDialogFragment {
    private EditText etPaymentMethodId;
    private Button btSubmit;

    private TYPE type = TYPE.LIST;

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
        etPaymentMethodId = findViewById(R.id.etPaymentMethodId);
        btSubmit = findViewById(R.id.btSubmit);

        btSubmit.setOnClickListener(v -> submit());
    }

    private PaymentMethodReportParameters buildPaymentMethodReportParameters() {
        return new PaymentMethodReportParameters();
    }

    private void submit() {
        Fragment targetFragment = getTargetFragment();

        if (targetFragment instanceof Callback) {
            Callback callback = (Callback) targetFragment;

            if (type == TYPE.BY_ID) {
                callback.onSubmitPaymentMethodId(etPaymentMethodId.getText().toString());
            } else {
                PaymentMethodReportParameters reportParameters = buildPaymentMethodReportParameters();
                callback.onSubmitPaymentMethodReportParameters(reportParameters);
            }
        }

        dismiss();
    }

    public interface Callback {
        void onSubmitPaymentMethodReportParameters(PaymentMethodReportParameters paymentMethodReportParameters);
        void onSubmitPaymentMethodId(String paymentMethodId);
    }
}

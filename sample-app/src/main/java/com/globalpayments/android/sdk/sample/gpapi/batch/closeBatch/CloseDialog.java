package com.globalpayments.android.sdk.sample.gpapi.batch.closeBatch;

import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;

public class CloseDialog extends BaseDialogFragment {

    private EditText etCloseBatchId;
    private Button btSubmit;

    public static CloseDialog newInstance(Fragment targetFragment) {
        CloseDialog closeDialog = new CloseDialog();
        closeDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        closeDialog.setTargetFragment(targetFragment, 0);
        return closeDialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_close_batch;
    }

    @Override
    protected void initViews() {
        etCloseBatchId = findViewById(R.id.etCloseBatchId);
        btSubmit = findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(v -> submit());
    }

    private void submit() {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof Callback) {
            Callback callback = (Callback) targetFragment;
            callback.onSubmitCloseBatchParametersModel(etCloseBatchId.getText().toString());
        }

        dismiss();
    }

    public interface Callback {
        void onSubmitCloseBatchParametersModel(String batchId);
    }
}
package com.globalpayments.android.sdk.sample.gpapi.batch.closeBatch;

import static com.globalpayments.android.sdk.sample.common.Constants.DIALOG_TYPE;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideViews;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;
import com.globalpayments.android.sdk.sample.gpapi.batch.closeBatch.model.CloseBatchParametersModel;
import java.io.Serializable;

public class CloseDialog extends BaseDialogFragment {

    private GridLayout glContainer;
    private TextView tvHeader;
    private EditText etCloseBatchId;
    private Button btSubmit;

    private TYPE type = TYPE.CLOSE_BATCH_BY_ID;

    public static CloseDialog newInstance(Fragment targetFragment, TYPE type) {
        CloseDialog closeDialog = new CloseDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable(DIALOG_TYPE, type);
        closeDialog.setArguments(bundle);

        closeDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        closeDialog.setTargetFragment(targetFragment, 0);

        return closeDialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_close_batch;
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
        etCloseBatchId = findViewById(R.id.etCloseBatchId);
        btSubmit = findViewById(R.id.btSubmit);

        switch (type) {
            case CLOSE_BATCH_BY_ID:
                //handleListViewsVisibility();
                break;
        }

        btSubmit.setOnClickListener(v -> submit());
    }

    private void handleListViewsVisibility() { hideViews(etCloseBatchId);
    }

    private CloseBatchParametersModel buildCloseBatchRootParametersBuildModel() {
        CloseBatchParametersModel closeBatchParametersModel = new CloseBatchParametersModel();

        closeBatchParametersModel.setId(etCloseBatchId.getText().toString());

        return closeBatchParametersModel;
    }

    private void submit() {
        Fragment targetFragment = getTargetFragment();

        if (targetFragment instanceof Callback) {
            Callback callback = (Callback) targetFragment;

            switch (type) {
                case CLOSE_BATCH_BY_ID:
                    CloseBatchParametersModel closeBatchParametersModel = buildCloseBatchRootParametersBuildModel();
                    callback.onSubmitCloseBatchParametersModel(closeBatchParametersModel);
                    break;
            }
        }

        dismiss();
    }

    public enum TYPE {
        CLOSE_BATCH_BY_ID
    }

    public interface Callback {
        void onSubmitCloseBatchParametersModel(CloseBatchParametersModel closeBatchParametersModel);
    }
}

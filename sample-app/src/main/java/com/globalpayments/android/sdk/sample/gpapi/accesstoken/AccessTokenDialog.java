package com.globalpayments.android.sdk.sample.gpapi.accesstoken;

import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.global.api.entities.enums.Environment;
import com.global.api.entities.enums.IntervalToExpire;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomSpinner;
import com.globalpayments.android.sdk.sample.gpapi.accesstoken.model.AccessTokenInputModel;

public class AccessTokenDialog extends BaseDialogFragment {
    private EditText etAppId;
    private EditText etAppKey;
    private EditText etSecondsToExpire;
    private CustomSpinner environmentSpinner;
    private CustomSpinner intervalToExpireSpinner;

    public static AccessTokenDialog newInstance(Fragment targetFragment) {
        AccessTokenDialog accessTokenDialog = new AccessTokenDialog();
        accessTokenDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        accessTokenDialog.setTargetFragment(targetFragment, 0);
        return accessTokenDialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_access_token;
    }

    @Override
    protected void initViews() {
        etAppId = findViewById(R.id.etAppId);
        etAppKey = findViewById(R.id.etAppKey);
        etSecondsToExpire = findViewById(R.id.etSecondsToExpire);
        environmentSpinner = findViewById(R.id.environmentSpinner);
        intervalToExpireSpinner = findViewById(R.id.intervalToExpireSpinner);

        environmentSpinner.init(Environment.values());
        intervalToExpireSpinner.init(IntervalToExpire.values());

        Button btSubmit = findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(v -> submitAccessTokenInputModel());
    }

    private AccessTokenInputModel buildAccessTokenInputModel() {
        AccessTokenInputModel accessTokenInputModel = new AccessTokenInputModel();
        accessTokenInputModel.setAppId(etAppId.getText().toString());
        accessTokenInputModel.setAppKey(etAppKey.getText().toString());
        accessTokenInputModel.setSecondsToExpire(Integer.parseInt(etSecondsToExpire.getText().toString()));
        accessTokenInputModel.setEnvironment(environmentSpinner.getSelectedOption());
        accessTokenInputModel.setIntervalToExpire(intervalToExpireSpinner.getSelectedOption());
        return accessTokenInputModel;
    }

    private void submitAccessTokenInputModel() {
        AccessTokenInputModel accessTokenInputModel = buildAccessTokenInputModel();

        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof Callback) {
            Callback callback = (Callback) targetFragment;
            callback.onSubmitAccessTokenInputModel(accessTokenInputModel);
        }

        dismiss();
    }

    public interface Callback {
        void onSubmitAccessTokenInputModel(AccessTokenInputModel accessTokenInputModel);
    }
}

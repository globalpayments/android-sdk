package com.globalpayments.android.sdk.sample.gpapi.accesstoken;

import static com.globalpayments.android.sdk.utils.ViewUtils.handleViewsVisibility;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.global.api.entities.enums.Environment;
import com.global.api.entities.enums.IntervalToExpire;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccessTokenDialog extends BaseDialogFragment {
    private EditText etAppId;
    private EditText etAppKey;
    private EditText etSecondsToExpire;
    private CustomSpinner environmentSpinner;
    private CustomSpinner intervalToExpireSpinner;
    private final List<String> permissions = new ArrayList<>();
    List<CheckBox> checkboxes;
    private CheckBox cbSetPermissions;
    // permissions
    private CheckBox cbActPostMultiple;
    private CheckBox cbActGetSingle;
    private CheckBox cbActGetList;
    private CheckBox cbAccGetList;
    private CheckBox cbAccGetSingle;
    private CheckBox cbGetSingle;
    private CheckBox cbPmtPostDetokenize;
    private CheckBox cbPmtPostSearch;
    private CheckBox cbPmtGetList;
    private CheckBox cbPmtPostCreate;
    private CheckBox cbPmtGetSingle;
    private CheckBox cbPmtPatchEdit;

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

        cbSetPermissions = findViewById(R.id.cbSetPermissions);

        // permissions
        cbActPostMultiple = findViewById(R.id.cbActPostMultiple);
        cbActGetSingle = findViewById(R.id.cbActGetSingle);
        cbActGetList = findViewById(R.id.cbActGetList);
        cbAccGetList = findViewById(R.id.cbAccGetList);
        cbAccGetSingle = findViewById(R.id.cbAccGetSingle);
        cbGetSingle = findViewById(R.id.cbGetSingle);
        cbPmtPostDetokenize = findViewById(R.id.cbPmtPostDetokenize);
        cbPmtPostSearch = findViewById(R.id.cbPmtPostSearch);
        cbPmtGetList = findViewById(R.id.cbPmtGetList);
        cbPmtPostCreate = findViewById(R.id.cbPmtPostCreate);
        cbPmtGetSingle = findViewById(R.id.cbPmtGetSingle);
        cbPmtPatchEdit = findViewById(R.id.cbPmtPatchEdit);

        environmentSpinner.init(Environment.values());
        intervalToExpireSpinner.init(IntervalToExpire.values());

        Button btSubmit = findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(v -> submitAccessTokenInputModel());

        configureViews();
    }

    private void configureViews() {
        checkboxes = Arrays.asList(
                cbActPostMultiple,
                cbActGetSingle,
                cbActGetList,
                cbAccGetList,
                cbAccGetSingle,
                cbGetSingle,
                cbPmtPostDetokenize,
                cbPmtPostSearch,
                cbPmtGetList,
                cbPmtPostCreate,
                cbPmtGetSingle,
                cbPmtPatchEdit);
        cbSetPermissions.setOnCheckedChangeListener((buttonView, isChecked) -> handleSetPermissionsViewsVisibility(isChecked));
        handleSetPermissionsViewsVisibility(false);
    }

    private void handleSetPermissionsViewsVisibility(boolean setPermissions) {
        handleViewsVisibility(setPermissions,
                cbActPostMultiple,
                cbActGetSingle,
                cbActGetList,
                cbAccGetList,
                cbAccGetSingle,
                cbGetSingle,
                cbPmtPostDetokenize,
                cbPmtPostSearch,
                cbPmtGetList,
                cbPmtPostCreate,
                cbPmtGetSingle,
                cbPmtPatchEdit
        );
        resetAllCheckbox();
    }

    private void resetAllCheckbox() {
        for (CheckBox checkBox : this.checkboxes) {
            checkBox.setChecked(false);
        }
    }

    private String[] getPermissions() {
        for (CheckBox checkBox : this.checkboxes) {
            if (checkBox.isChecked()) {
                permissions.add(checkBox.getText().toString());
            }
        }
        return permissions.toArray(new String[0]);
    }

    private void submitAccessTokenInputModel() {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof Callback) {
            Callback callback = (Callback) targetFragment;
            if (etSecondsToExpire.getText().toString().isEmpty()) {
                etSecondsToExpire.setText("0");
            }
            callback.onSubmitAccessTokenInputModel(
                    etAppId.getText().toString(),
                    etAppKey.getText().toString(),
                    environmentSpinner.getSelectedOption(),
                    Integer.parseInt(etSecondsToExpire.getText().toString()),
                    intervalToExpireSpinner.getSelectedOption(),
                    getPermissions());
        }
        dismiss();
    }

    public interface Callback {
        void onSubmitAccessTokenInputModel(String appId,
                                           String appKey,
                                           Environment environment,
                                           int secondsToExpire,
                                           IntervalToExpire intervalToExpire,
                                           String[] permissions);
    }

}
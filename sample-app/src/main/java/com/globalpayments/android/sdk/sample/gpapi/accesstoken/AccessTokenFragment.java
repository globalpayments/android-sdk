package com.globalpayments.android.sdk.sample.gpapi.accesstoken;

import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.global.api.entities.enums.Environment;
import com.global.api.entities.enums.IntervalToExpire;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;

public class AccessTokenFragment extends BaseFragment implements AccessTokenDialog.Callback {
    private ProgressBar progressBar;
    private TextView errorTextView;
    private CardView cvAccessToken;
    private AccessTokenView accessTokenView;

    private AccessTokenViewModel accessTokenViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_access_token;
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.access_token);
        customToolbar.setOnBackButtonListener(v -> close());

        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);

        cvAccessToken = findViewById(R.id.cvAccessToken);
        accessTokenView = findViewById(R.id.accessTokenView);

        Button btCreateAccessToken = findViewById(R.id.btCreateAccessToken);
        btCreateAccessToken.setOnClickListener(v -> showAccessTokenDialog());
    }

    private void showAccessTokenDialog() {
        AccessTokenDialog accessTokenDialog = AccessTokenDialog.newInstance(this);
        accessTokenDialog.show(requireFragmentManager(), AccessTokenDialog.class.getSimpleName());
    }

    @Override
    protected void initDependencies() {
        accessTokenViewModel = new ViewModelProvider(this).get(AccessTokenViewModel.class);
    }

    @Override
    protected void initSubscriptions() {
        accessTokenViewModel.getProgressStatus().observe(this, show -> {
            if (show) {
                hideView(cvAccessToken);
                hideView(errorTextView);
                showView(progressBar);
            } else {
                hideView(progressBar);
            }
        });

        accessTokenViewModel.getError().observe(this, errorMessage -> {
            hideView(cvAccessToken);
            showView(errorTextView);
            errorTextView.setText(errorMessage);
        });

        accessTokenViewModel.getAccessTokenInfoLiveData().observe(this, accessTokenInfo -> {
            hideView(errorTextView);
            showView(cvAccessToken);
            accessTokenView.bind(accessTokenInfo);
        });
    }

    @Override
    public void onSubmitAccessTokenInputModel(String appId,
                                              String appKey,
                                              Environment environment,
                                              int secondsToExpire,
                                              IntervalToExpire intervalToExpire,
                                              String[] permissions) {
        accessTokenViewModel.getAccessTokenInfo(appId, appKey, environment, secondsToExpire, intervalToExpire, permissions);
    }
}

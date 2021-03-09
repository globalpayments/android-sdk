package com.globalpayments.android.sdk.sample.gpapi.verifications;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.verifications.model.VerificationsModel;

import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

public class VerificationsFragment extends BaseFragment implements VerificationsDialog.Callback {
    private ProgressBar progressBar;
    private TextView errorTextView;
    private CardView cvVerification;
    private VerificationsView verificationsView;

    private VerificationsViewModel verificationsViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_verifications;
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.verifications);
        customToolbar.setOnBackButtonListener(v -> close());

        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);

        cvVerification = findViewById(R.id.cvVerification);
        verificationsView = findViewById(R.id.verificationsView);

        Button btInitiateVerification = findViewById(R.id.btInitiateVerification);
        btInitiateVerification.setOnClickListener(v -> showVerificationDialog());
    }

    private void showVerificationDialog() {
        VerificationsDialog verificationsDialog = VerificationsDialog.newInstance(this);
        verificationsDialog.show(requireFragmentManager(), VerificationsDialog.class.getSimpleName());
    }

    @Override
    protected void initDependencies() {
        verificationsViewModel = new ViewModelProvider(this).get(VerificationsViewModel.class);
    }

    @Override
    protected void initSubscriptions() {
        verificationsViewModel.getProgressStatus().observe(this, show -> {
            if (show) {
                hideView(cvVerification);
                hideView(errorTextView);
                showView(progressBar);
            } else {
                hideView(progressBar);
            }
        });

        verificationsViewModel.getError().observe(this, errorMessage -> {
            hideView(cvVerification);
            showView(errorTextView);
            errorTextView.setText(errorMessage);
        });

        verificationsViewModel.getTransactionLiveData().observe(this, transaction -> {
            hideView(errorTextView);
            showView(cvVerification);
            verificationsView.bind(transaction);
        });
    }

    @Override
    public void onSubmitVerificationsModel(VerificationsModel verificationsModel) {
        verificationsViewModel.executeVerification(verificationsModel);
    }
}

package com.globalpayments.android.sdk.sample.gpapi.disputes.operations;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.disputes.operations.dialog.DisputeOperationDialog;
import com.globalpayments.android.sdk.sample.gpapi.disputes.operations.model.DisputeOperationModel;

import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

public class DisputeOperationsFragment extends BaseFragment implements DisputeOperationDialog.Callback {
    private ProgressBar progressBar;
    private TextView errorTextView;
    private CardView cvDisputeOperation;
    private DisputeOperationView disputeOperationView;

    private DisputeOperationsViewModel disputeOperationsViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_disputes_operations;
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.dispute_operations);
        customToolbar.setOnBackButtonListener(v -> close());

        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);

        cvDisputeOperation = findViewById(R.id.cvDisputeOperation);
        disputeOperationView = findViewById(R.id.disputeOperationView);

        Button btInitiateDisputeOperation = findViewById(R.id.btInitiateDisputeOperation);
        btInitiateDisputeOperation.setOnClickListener(v -> showDisputeOperationDialog());
    }

    private void showDisputeOperationDialog() {
        DisputeOperationDialog disputeOperationDialog = DisputeOperationDialog.newInstance(this);
        disputeOperationDialog.show(requireFragmentManager(), DisputeOperationDialog.class.getSimpleName());
    }

    @Override
    protected void initDependencies() {
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        disputeOperationsViewModel = new ViewModelProvider(this, factory).get(DisputeOperationsViewModel.class);
    }

    @Override
    protected void initSubscriptions() {
        disputeOperationsViewModel.getProgressStatus().observe(this, show -> {
            if (show) {
                hideView(cvDisputeOperation);
                hideView(errorTextView);
                showView(progressBar);
            } else {
                hideView(progressBar);
            }
        });

        disputeOperationsViewModel.getError().observe(this, errorMessage -> {
            hideView(cvDisputeOperation);
            showView(errorTextView);
            errorTextView.setText(errorMessage);
        });

        disputeOperationsViewModel.getTransactionLiveData().observe(this, transaction -> {
            hideView(errorTextView);
            showView(cvDisputeOperation);
            disputeOperationView.bind(transaction);
        });
    }

    @Override
    public void onSubmitDisputeOperationModel(DisputeOperationModel disputeOperationModel) {
        disputeOperationsViewModel.executeDisputeOperation(disputeOperationModel);
    }
}

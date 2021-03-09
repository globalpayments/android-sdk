package com.globalpayments.android.sdk.sample.gpapi.transaction.operations;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationModel;

import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

public class TransactionOperationsFragment extends BaseFragment implements TransactionOperationDialog.Callback {
    private ProgressBar progressBar;
    private TextView errorTextView;
    private CardView cvTransaction;
    private TransactionView transactionView;

    private TransactionOperationsViewModel transactionOperationsViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_transaction_operations;
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.transaction_operations);
        customToolbar.setOnBackButtonListener(v -> close());

        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);

        cvTransaction = findViewById(R.id.cvTransaction);
        transactionView = findViewById(R.id.transactionView);

        Button btCreateTransaction = findViewById(R.id.btCreateTransaction);
        btCreateTransaction.setOnClickListener(v -> showTransactionOperationDialog());
    }

    private void showTransactionOperationDialog() {
        TransactionOperationDialog transactionOperationDialog = TransactionOperationDialog.newInstance(this);
        transactionOperationDialog.show(requireFragmentManager(), TransactionOperationDialog.class.getSimpleName());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        transactionOperationsViewModel.init();
    }

    @Override
    protected void initDependencies() {
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        transactionOperationsViewModel = new ViewModelProvider(this, factory).get(TransactionOperationsViewModel.class);
    }

    @Override
    public void onSubmitTransactionOperationModel(TransactionOperationModel transactionOperationModel) {
        transactionOperationsViewModel.executeTransactionOperation(transactionOperationModel);
    }

    @Override
    protected void initSubscriptions() {
        transactionOperationsViewModel.getProgressStatus().observe(this, show -> {
            if (show) {
                hideView(cvTransaction);
                hideView(errorTextView);
                showView(progressBar);
            } else {
                hideView(progressBar);
            }
        });

        transactionOperationsViewModel.getError().observe(this, errorMessage -> {
            hideView(cvTransaction);
            showView(errorTextView);
            errorTextView.setText(errorMessage);
        });

        transactionOperationsViewModel.getTransactionLiveData().observe(this, transaction -> {
            hideView(errorTextView);
            showView(cvTransaction);
            transactionView.bind(transaction);
        });
    }
}

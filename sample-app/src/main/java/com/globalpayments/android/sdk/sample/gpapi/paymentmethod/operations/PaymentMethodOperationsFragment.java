package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations;

import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations.model.PaymentMethodOperationModel;

public class PaymentMethodOperationsFragment extends BaseFragment implements PaymentMethodOperationDialog.Callback{
    private ProgressBar progressBar;
    private TextView errorTextView;
    private CardView cvPaymentMethodOperation;
    private PaymentMethodOperationView paymentMethodOperationView;

    private PaymentMethodOperationsViewModel paymentMethodOperationsViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_payment_method_operations;
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.payment_method_operations);
        customToolbar.setOnBackButtonListener(v -> close());

        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);

        cvPaymentMethodOperation = findViewById(R.id.cvPaymentMethodOperation);
        paymentMethodOperationView = findViewById(R.id.paymentMethodOperationView);

        Button btInitiatePaymentMethodOperation = findViewById(R.id.btInitiatePaymentMethodOperation);
        btInitiatePaymentMethodOperation.setOnClickListener(v -> showPaymentMethodOperationDialog());
    }

    private void showPaymentMethodOperationDialog() {
        PaymentMethodOperationDialog paymentMethodOperationDialog = PaymentMethodOperationDialog.newInstance(this);
        paymentMethodOperationDialog.show(requireFragmentManager(), PaymentMethodOperationDialog.class.getSimpleName());
    }

    @Override
    protected void initDependencies() {
        paymentMethodOperationsViewModel = new ViewModelProvider(this).get(PaymentMethodOperationsViewModel.class);
    }

    @Override
    protected void initSubscriptions() {
        paymentMethodOperationsViewModel.getProgressStatus().observe(this, show -> {
            if (show) {
                hideView(cvPaymentMethodOperation);
                hideView(errorTextView);
                showView(progressBar);
            } else {
                hideView(progressBar);
            }
        });

        paymentMethodOperationsViewModel.getError().observe(this, errorMessage -> {
            hideView(cvPaymentMethodOperation);
            showView(errorTextView);
            errorTextView.setText(errorMessage);
        });

        paymentMethodOperationsViewModel.getPaymentMethodOperationUIModelLiveData().observe(this, paymentMethodOperationUIModel -> {
            hideView(errorTextView);
            showView(cvPaymentMethodOperation);
            paymentMethodOperationView.bind(paymentMethodOperationUIModel);
        });
    }

    @Override
    public void onSubmitPaymentMethodOperationModel(PaymentMethodOperationModel paymentMethodOperationModel) {
        paymentMethodOperationsViewModel.executePaymentMethodOperation(paymentMethodOperationModel);
    }
}

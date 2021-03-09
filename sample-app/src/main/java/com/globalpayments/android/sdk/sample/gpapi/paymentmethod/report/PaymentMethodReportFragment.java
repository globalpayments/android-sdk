package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.global.api.entities.Transaction;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE;
import com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report.model.PaymentMethodReportParameters;

import java.util.List;

import static com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE.BY_ID;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

public class PaymentMethodReportFragment extends BaseFragment implements PaymentMethodReportDialog.Callback {
    private ProgressBar progressBar;
    private TextView errorTextView;
    private RecyclerView recyclerView;

    private PaymentMethodReportAdapter paymentMethodReportAdapter;
    private PaymentMethodReportViewModel paymentMethodReportViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_payment_method_report;
    }

    @Override
    protected void initDependencies() {
        paymentMethodReportViewModel = new ViewModelProvider(this).get(PaymentMethodReportViewModel.class);
        paymentMethodReportAdapter = new PaymentMethodReportAdapter();
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.payment_method_report);
        customToolbar.setOnBackButtonListener(v -> close());

        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(paymentMethodReportAdapter);

        Button btGetPaymentMethodsList = findViewById(R.id.btGetPaymentMethodsList);
        btGetPaymentMethodsList.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Not implemented in the sdk", Toast.LENGTH_LONG).show());

        Button btGetPaymentMethodById = findViewById(R.id.btGetPaymentMethodById);
        btGetPaymentMethodById.setOnClickListener(v -> showPaymentMethodReportDialog(BY_ID));
    }

    private void showPaymentMethodReportDialog(TYPE type) {
        PaymentMethodReportDialog paymentMethodReportDialog = PaymentMethodReportDialog.newInstance(this, type);
        paymentMethodReportDialog.show(requireFragmentManager(), PaymentMethodReportDialog.class.getSimpleName());
    }

    @Override
    protected void initSubscriptions() {
        paymentMethodReportViewModel.getProgressStatus().observe(this, show -> {
            if (show) {
                hideView(recyclerView);
                hideView(errorTextView);
                showView(progressBar);
            } else {
                hideView(progressBar);
            }
        });

        paymentMethodReportViewModel.getError().observe(this, errorMessage -> {
            hideView(recyclerView);
            showView(errorTextView);
            errorTextView.setText(errorMessage);
        });

        paymentMethodReportViewModel.getPaymentMethodsLiveData().observe(this, paymentMethods -> {
            hideView(errorTextView);
            showView(recyclerView);
            submitPaymentMethods(paymentMethods);
        });
    }

    private void submitPaymentMethods(List<Transaction> paymentMethods) {
        paymentMethodReportAdapter.submitList(paymentMethods);
    }


    @Override
    public void onSubmitPaymentMethodReportParameters(PaymentMethodReportParameters paymentMethodReportParameters) {
        paymentMethodReportViewModel.getPaymentMethodList(paymentMethodReportParameters);
    }

    @Override
    public void onSubmitPaymentMethodId(String paymentMethodId) {
        paymentMethodReportViewModel.getPaymentMethodById(paymentMethodId);
    }
}

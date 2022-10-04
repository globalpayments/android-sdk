package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import static com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE.BY_ID;
import static com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE.LIST;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

import android.app.ProgressDialog;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.api.entities.reporting.StoredPaymentMethodSummary;
import com.global.api.paymentMethods.CreditCardData;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE;
import com.globalpayments.android.sdk.sample.gpapi.transaction.report.model.TransactionReportParameters;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.List;

public class PaymentMethodReportFragment extends BaseFragment implements PaymentMethodReportDialog.Callback, PaymentMethodReportCardDialog.Callback {

    private TextView errorTextView;
    private RecyclerView recyclerViewById;
    private RecyclerView recyclerViewList;
    private MaterialButtonToggleGroup materialButtonToggleGroup;

    private PaymentMethodReportByIdAdapter paymentMethodReportByIdAdapter;
    private PaymentMethodReportListAdapter paymentMethodReportListAdapter;
    private PaymentMethodReportViewModel paymentMethodReportViewModel;

    private ProgressDialog progressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_payment_method_report;
    }

    @Override
    protected void initDependencies() {
        paymentMethodReportViewModel = new ViewModelProvider(this).get(PaymentMethodReportViewModel.class);
        paymentMethodReportByIdAdapter = new PaymentMethodReportByIdAdapter();
        paymentMethodReportListAdapter = new PaymentMethodReportListAdapter();
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.payment_method_report);
        customToolbar.setOnBackButtonListener(v -> close());

        errorTextView = findViewById(R.id.errorTextView);

        recyclerViewById = findViewById(R.id.recyclerViewById);
        recyclerViewById.setAdapter(paymentMethodReportByIdAdapter);

        recyclerViewList = findViewById(R.id.recyclerViewList);
        recyclerViewList.setAdapter(paymentMethodReportListAdapter);
        recyclerViewList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (paymentMethodReportViewModel.getProgressStatus().getValue()) return;

                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == paymentMethodReportListAdapter.getItemCount() - 1) {
                    paymentMethodReportViewModel.loadMore();
                }
            }
        });


        materialButtonToggleGroup = findViewById(R.id.btgMethods);

        Button btGetPaymentMethodsList = findViewById(R.id.btGetPaymentMethodsList);
        btGetPaymentMethodsList.setOnClickListener(v -> showPaymentMethodReportDialog(LIST));

        Button btGetPaymentMethodById = findViewById(R.id.btGetPaymentMethodById);
        btGetPaymentMethodById.setOnClickListener(v -> showPaymentMethodReportDialog(BY_ID));

        Button btGetPaymentMethodByCard = findViewById(R.id.btGetPaymentMethodByCard);
        btGetPaymentMethodByCard.setOnClickListener(v -> showPaymentMethodReportCardDialog());

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Retrieving reports");
    }

    private void showPaymentMethodReportDialog(TYPE type) {
        PaymentMethodReportDialog paymentMethodReportDialog = PaymentMethodReportDialog.newInstance(this, type);
        paymentMethodReportDialog.show(getParentFragmentManager(), PaymentMethodReportDialog.class.getSimpleName());
    }

    private void showPaymentMethodReportCardDialog() {
        PaymentMethodReportCardDialog dialog = PaymentMethodReportCardDialog.newInstance(this);
        dialog.show(getParentFragmentManager(), PaymentMethodReportCardDialog.class.getSimpleName());
    }

    @Override
    protected void initSubscriptions() {
        paymentMethodReportViewModel.getProgressStatus().observe(this, show -> {
            if (show) {
                hideView(recyclerViewById);
                hideView(errorTextView);
                progressDialog.show();
            } else {
                progressDialog.hide();
            }
        });

        paymentMethodReportViewModel.getError().observe(this, errorMessage -> {
            hideView(recyclerViewById);
            hideView(recyclerViewList);
            showView(errorTextView);
            if (errorMessage.contains("Empty") && errorMessage.contains("List")) {
                errorTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack));
                errorTextView.setText(R.string.empty_list);
            } else {
                errorTextView.setText(errorMessage);
            }
        });

        paymentMethodReportViewModel.getPaymentMethodsLiveData().observe(this, paymentMethods -> {
            hideView(errorTextView);
            hideView(recyclerViewList);
            showView(recyclerViewById);
            submitPaymentMethodsById(paymentMethods);
        });

        paymentMethodReportViewModel.getPaymentMethodsListLiveData().observe(this, storedPaymentMethodSummaryList -> {
            hideView(errorTextView);
            hideView(recyclerViewById);
            showView(recyclerViewList);
            submitPaymentMethodsList(storedPaymentMethodSummaryList);
        });
    }

    private void submitPaymentMethodsById(List<StoredPaymentMethodSummary> paymentMethods) {
        paymentMethodReportByIdAdapter.submitList(paymentMethods);
    }

    private void submitPaymentMethodsList(List<StoredPaymentMethodSummary> storedPaymentMethodSummaryList) {
        paymentMethodReportListAdapter.addItems(storedPaymentMethodSummaryList);
    }

    @Override
    public void onSubmitPaymentMethodId(String paymentMethodId) {
        paymentMethodReportListAdapter.clearItems();
        paymentMethodReportViewModel.getPaymentMethodById(paymentMethodId);
    }

    @Override
    public void onSubmitPaymentsReportParameters(TransactionReportParameters transactionReportParameters) {
        paymentMethodReportListAdapter.clearItems();
        paymentMethodReportViewModel.getPaymentMethodList(transactionReportParameters);
    }

    @Override
    public void onSubmitPaymentCardMethod(@NonNull CreditCardData card) {
        paymentMethodReportListAdapter.clearItems();
        paymentMethodReportViewModel.getPaymentMethodCard(card);
    }

    @Override
    public void onDismissWithoutSelection() {
        materialButtonToggleGroup.clearChecked();
    }
}

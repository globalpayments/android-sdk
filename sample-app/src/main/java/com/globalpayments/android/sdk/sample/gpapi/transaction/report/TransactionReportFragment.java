package com.globalpayments.android.sdk.sample.gpapi.transaction.report;

import static com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE.BY_ID;
import static com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE.LIST;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

import android.app.ProgressDialog;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.api.entities.TransactionSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE;
import com.globalpayments.android.sdk.sample.gpapi.transaction.report.model.TransactionReportParameters;

import java.util.List;

public class TransactionReportFragment extends BaseFragment implements TransactionReportDialog.Callback {
    private ProgressDialog progressDialog;
    private TextView errorTextView;
    private RecyclerView recyclerView;

    private TransactionReportAdapter transactionReportAdapter;
    private TransactionReportViewModel transactionReportViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_transaction_report;
    }

    @Override
    protected void initDependencies() {
        transactionReportViewModel = new ViewModelProvider(this).get(TransactionReportViewModel.class);
        transactionReportAdapter = new TransactionReportAdapter();
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.transaction_report);
        customToolbar.setOnBackButtonListener(v -> close());

        errorTextView = findViewById(R.id.errorTextView);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(transactionReportAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (Boolean.TRUE.equals(transactionReportViewModel.getProgressStatus().getValue()))
                    return;

                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == transactionReportAdapter.getItemCount() - 1) {
                    transactionReportViewModel.loadMore();
                }
            }
        });

        Button btGetTransactionList = findViewById(R.id.btGetTransactionList);
        btGetTransactionList.setOnClickListener(v -> showTransactionReportDialog(LIST));

        Button btGetTransactionById = findViewById(R.id.btGetPaymentMethodById);
        btGetTransactionById.setOnClickListener(v -> showTransactionReportDialog(BY_ID));

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Retrieving reports");
    }

    private void showTransactionReportDialog(TYPE type) {
        TransactionReportDialog transactionReportDialog = TransactionReportDialog.newInstance(this, type);
        transactionReportDialog.show(requireFragmentManager(), TransactionReportDialog.class.getSimpleName());
    }

    @Override
    protected void initSubscriptions() {
        transactionReportViewModel.getProgressStatus().observe(this, show -> {
            if (show) {
                hideView(errorTextView);
                progressDialog.show();
            } else {
                progressDialog.hide();
            }
        });

        transactionReportViewModel.getError().observe(this, errorMessage -> {
            hideView(recyclerView);
            showView(errorTextView);
            if (errorMessage.contains("Empty") && errorMessage.contains("List")) {
                errorTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack));
                errorTextView.setText(R.string.empty_list);
            } else {
                errorTextView.setText(errorMessage);
            }
        });

        transactionReportViewModel.getTransactionsLiveData().observe(this, transactionList -> {
            hideView(errorTextView);
            showView(recyclerView);
            submitTransactions(transactionList);
        });
    }

    private void submitTransactions(List<TransactionSummary> transactionList) {
        transactionReportAdapter.setExpandedByDefault(transactionList.size() == 1);
        transactionReportAdapter.addItems(transactionList);
    }

    @Override
    public void onSubmitTransactionReportParameters(TransactionReportParameters transactionReportParameters) {
        transactionReportAdapter.clearItems();
        transactionReportViewModel.getTransactionList(transactionReportParameters);
    }

    @Override
    public void onSubmitTransactionId(String transactionId) {
        transactionReportAdapter.clearItems();
        transactionReportViewModel.getTransactionById(transactionId);
    }
}

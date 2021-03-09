package com.globalpayments.android.sdk.sample.gpapi.deposits;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.global.api.entities.reporting.DepositSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE;
import com.globalpayments.android.sdk.sample.gpapi.deposits.model.DepositParametersModel;

import java.util.List;

import static com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE.BY_ID;
import static com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE.LIST;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

public class DepositsFragment extends BaseFragment implements DepositsDialog.Callback {
    private ProgressBar progressBar;
    private TextView errorTextView;
    private RecyclerView recyclerView;

    private DepositsAdapter depositsAdapter;
    private DepositsViewModel depositsViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_deposits;
    }

    @Override
    protected void initDependencies() {
        depositsViewModel = new ViewModelProvider(this).get(DepositsViewModel.class);
        depositsAdapter = new DepositsAdapter();
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.deposits);
        customToolbar.setOnBackButtonListener(v -> close());

        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);

        recyclerView = findViewById(R.id.recyclerView);

        Button btGetDepositsList = findViewById(R.id.btGetDepositsList);
        btGetDepositsList.setOnClickListener(v -> showDepositsDialog(LIST));

        Button btGetDepositById = findViewById(R.id.btGetDepositById);
        btGetDepositById.setOnClickListener(v -> showDepositsDialog(BY_ID));
    }

    private void showDepositsDialog(TYPE type) {
        DepositsDialog depositsDialog = DepositsDialog.newInstance(this, type);
        depositsDialog.show(requireFragmentManager(), DepositsDialog.class.getSimpleName());
    }

    @Override
    protected void initSubscriptions() {
        depositsViewModel.getProgressStatus().observe(this, show -> {
            if (show) {
                hideView(recyclerView);
                hideView(errorTextView);
                showView(progressBar);
            } else {
                hideView(progressBar);
            }
        });

        depositsViewModel.getError().observe(this, errorMessage -> {
            hideView(recyclerView);
            showView(errorTextView);
            errorTextView.setText(errorMessage);
        });

        depositsViewModel.getDepositSummaryListLiveData().observe(this, depositSummaryList -> {
            hideView(errorTextView);
            showView(recyclerView);
            submitDeposits(depositSummaryList);
        });
    }

    private void submitDeposits(List<DepositSummary> depositSummaryList) {
        recyclerView.setAdapter(null);
        recyclerView.setAdapter(depositsAdapter);
        depositsAdapter.setExpandedByDefault(depositSummaryList.size() == 1);
        depositsAdapter.submitList(depositSummaryList);
    }


    @Override
    public void onSubmitDepositParametersModel(DepositParametersModel depositParametersModel) {
        depositsViewModel.getDepositsList(depositParametersModel);
    }

    @Override
    public void onSubmitDepositId(String depositId) {
        depositsViewModel.getDepositById(depositId);
    }
}

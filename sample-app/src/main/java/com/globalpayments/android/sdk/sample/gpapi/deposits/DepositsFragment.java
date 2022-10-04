package com.globalpayments.android.sdk.sample.gpapi.deposits;

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

import com.global.api.entities.reporting.DepositSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE;
import com.globalpayments.android.sdk.sample.gpapi.deposits.model.DepositParametersModel;

import java.util.List;

public class DepositsFragment extends BaseFragment implements DepositsDialog.Callback {

    private ProgressDialog progressDialog;
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

        errorTextView = findViewById(R.id.errorTextView);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(depositsAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (Boolean.TRUE.equals(depositsViewModel.getProgressStatus().getValue()))
                    return;

                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == depositsAdapter.getItemCount() - 1) {
                    depositsViewModel.loadMore();
                }
            }
        });

        Button btGetDepositsList = findViewById(R.id.btGetDepositsList);
        btGetDepositsList.setOnClickListener(v -> showDepositsDialog(LIST));

        Button btGetDepositById = findViewById(R.id.btGetDepositById);
        btGetDepositById.setOnClickListener(v -> showDepositsDialog(BY_ID));

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Retrieving deposits");
    }

    private void showDepositsDialog(TYPE type) {
        DepositsDialog depositsDialog = DepositsDialog.newInstance(this, type);
        depositsDialog.show(requireFragmentManager(), DepositsDialog.class.getSimpleName());
    }

    @Override
    protected void initSubscriptions() {
        depositsViewModel.getProgressStatus().observe(this, show -> {
            if (show) {
                hideView(errorTextView);
                progressDialog.show();
            } else {
                progressDialog.hide();
            }
        });

        depositsViewModel.getError().observe(this, errorMessage -> {
            hideView(recyclerView);
            showView(errorTextView);
            if (errorMessage.contains("Empty") && errorMessage.contains("List")) {
                errorTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack));
                errorTextView.setText(R.string.empty_list);
            } else {
                errorTextView.setText(errorMessage);
            }
        });

        depositsViewModel.getDepositSummaryListLiveData().observe(this, depositSummaryList -> {
            hideView(errorTextView);
            showView(recyclerView);
            submitDeposits(depositSummaryList);
        });
    }

    private void submitDeposits(List<DepositSummary> depositSummaryList) {
        depositsAdapter.setExpandedByDefault(depositSummaryList.size() == 1);
        depositsAdapter.addItems(depositSummaryList);
    }


    @Override
    public void onSubmitDepositParametersModel(DepositParametersModel depositParametersModel) {
        depositsAdapter.clearItems();
        depositsViewModel.getDepositsList(depositParametersModel);
    }

    @Override
    public void onSubmitDepositId(String depositId) {
        depositsAdapter.clearItems();
        depositsViewModel.getDepositById(depositId);
    }
}

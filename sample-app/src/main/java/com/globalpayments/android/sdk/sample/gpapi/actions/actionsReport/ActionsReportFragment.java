package com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport;

import static com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport.ActionsDialog.TYPE.ACTIONS_REPORT_BY_ID;
import static com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport.ActionsDialog.TYPE.ACTIONS_REPORT_LIST;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideViews;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;
import android.app.ProgressDialog;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.global.api.entities.reporting.ActionSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport.model.ActionsReportParametersModel;
import java.util.List;

public class ActionsReportFragment extends BaseFragment implements ActionsDialog.Callback {
    private ProgressBar progressBar;
    private TextView errorTextView;
    private RecyclerView recyclerView;

    private ProgressDialog progressDialog;

    private ActionsReportAdapter actionsReportAdapter;
    private ActionsReportViewModel actionsReportViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.actions_report_list_fragment;
    }

    @Override
    protected void initDependencies() {
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        actionsReportViewModel = new ViewModelProvider(this, factory).get(ActionsReportViewModel.class);
        actionsReportAdapter = new ActionsReportAdapter();
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.actions_report);
        customToolbar.setOnBackButtonListener(v -> close());

        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);

        recyclerView = findViewById(R.id.recyclerView);

        Button btGetActionList = findViewById(R.id.btGetActionList);
        btGetActionList.setOnClickListener(v -> showActionsReportDialog(ACTIONS_REPORT_LIST));

        Button btGetActionById = findViewById(R.id.btGetActionById);
        btGetActionById.setOnClickListener(v -> showActionsReportDialog(ACTIONS_REPORT_BY_ID));
    }

    private void showActionsReportDialog(ActionsDialog.TYPE type) {
        ActionsDialog actionsDialog = ActionsDialog.newInstance(this, type);
        actionsDialog.show(requireFragmentManager(), ActionsDialog.class.getSimpleName());
    }

    @Override
    protected void initSubscriptions() {
        actionsReportViewModel.getProgressStatus().observe(this, show -> {
            if (show) {
                hideViews(recyclerView, errorTextView);
                showView(progressBar);
            } else {
                hideView(progressBar);
            }
        });

        actionsReportViewModel.getError().observe(this, errorMessage -> {
            hideViews(recyclerView);
            showView(errorTextView);
            errorTextView.setText(errorMessage);
        });

        actionsReportViewModel.getActionsLiveData().observe(this, actions -> {
            hideViews(errorTextView);
            showView(recyclerView);
            submitActions(actions);
        });
    }

    private void submitActions(List<ActionSummary> actionSummaries) {
        recyclerView.setAdapter(null);
        recyclerView.setAdapter(actionsReportAdapter);
        actionsReportAdapter.setExpandedByDefault(actionSummaries.size() == 1);
        actionsReportAdapter.submitList(actionSummaries);
    }

    @Override
    public void onSubmitActionsReportListParametersModel(ActionsReportParametersModel actionsReportParametersModel) {
        actionsReportViewModel.getActionsList(actionsReportParametersModel);
    }

    @Override
    public void onSubmitActionsReportId(String actionId) {
        actionsReportViewModel.getActionsById(actionId);
    }

    @Override
    public void onDestroyView() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroyView();
    }
}

package com.globalpayments.android.sdk.sample.gpapi.batch.closeBatch;

import static com.globalpayments.android.sdk.sample.gpapi.batch.closeBatch.CloseDialog.TYPE.CLOSE_BATCH_BY_ID;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideViews;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;
import android.app.ProgressDialog;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.global.api.entities.BatchSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.batch.closeBatch.model.CloseBatchParametersModel;
import java.util.List;

public class CloseFragment extends BaseFragment implements CloseDialog.Callback {
    private ProgressBar progressBar;
    private TextView errorTextView;
    private RecyclerView recyclerView;

    private ProgressDialog progressDialog;

    private CloseBatchAdapter closeBatchAdapter;
    private CloseViewModel closeViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.close_fragment;
    }

    @Override
    protected void initDependencies() {
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        closeViewModel = new ViewModelProvider(this, factory).get(CloseViewModel.class);
        closeBatchAdapter = new CloseBatchAdapter();
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.close_batch);
        customToolbar.setOnBackButtonListener(v -> close());

        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);

        recyclerView = findViewById(R.id.recyclerView);

        Button btGetCloseBatchById = findViewById(R.id.btGetCloseBatchId);
        btGetCloseBatchById.setOnClickListener(v -> showActionsReportDialog(CLOSE_BATCH_BY_ID));
    }

    private void showActionsReportDialog(CloseDialog.TYPE type) {
        CloseDialog closeDialog = CloseDialog.newInstance(this, type);
        closeDialog.show(requireFragmentManager(), CloseDialog.class.getSimpleName());
    }

    @Override
    protected void initSubscriptions() {
        closeViewModel.getProgressStatus().observe(this, show -> {
            if (show) {
                hideViews(recyclerView, errorTextView);
                showView(progressBar);
            } else {
                hideView(progressBar);
            }
        });

        closeViewModel.getError().observe(this, errorMessage -> {
            hideViews(recyclerView);
            showView(errorTextView);
            errorTextView.setText(errorMessage);
        });

        closeViewModel.getCloseBatchLiveData().observe(this, actions -> {
            hideViews(errorTextView);
            showView(recyclerView);
            submitCloseBatch(actions);
        });
    }

    private void submitCloseBatch(List<BatchSummary> batchSummaries) {
        recyclerView.setAdapter(null);
        recyclerView.setAdapter(closeBatchAdapter);
        closeBatchAdapter.setExpandedByDefault(batchSummaries.size() == 1);
        closeBatchAdapter.submitList(batchSummaries);
    }

    @Override
    public void onSubmitCloseBatchParametersModel(CloseBatchParametersModel closeBatchParametersModel) {
        closeViewModel.getCloseBatchById(closeBatchParametersModel.getId());
    }

    @Override
    public void onDestroyView() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroyView();
    }
}

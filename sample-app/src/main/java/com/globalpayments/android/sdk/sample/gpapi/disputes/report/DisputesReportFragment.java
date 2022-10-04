package com.globalpayments.android.sdk.sample.gpapi.disputes.report;

import static com.globalpayments.android.sdk.sample.common.Constants.CREATE_FILE_REQUEST_CODE;
import static com.globalpayments.android.sdk.sample.gpapi.disputes.report.DisputesReportDialog.TYPE.DISPUTE_BY_DEPOSIT_ID;
import static com.globalpayments.android.sdk.sample.gpapi.disputes.report.DisputesReportDialog.TYPE.DISPUTE_BY_ID;
import static com.globalpayments.android.sdk.sample.gpapi.disputes.report.DisputesReportDialog.TYPE.DISPUTE_LIST;
import static com.globalpayments.android.sdk.sample.gpapi.disputes.report.DisputesReportDialog.TYPE.DOCUMENT_BY_ID;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideViews;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.api.entities.reporting.DisputeSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.disputes.report.model.DisputesReportParametersModel;
import com.globalpayments.android.sdk.sample.gpapi.disputes.report.model.DocumentContent;
import com.globalpayments.android.sdk.sample.gpapi.disputes.report.model.DocumentReportModel;
import com.globalpayments.android.sdk.utils.IntentUtils;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.io.File;
import java.util.Date;
import java.util.List;

public class DisputesReportFragment extends BaseFragment implements DisputesReportDialog.Callback {
    private TextView errorTextView;
    private RecyclerView recyclerView;

    // Document view
    private CardView cvDocumentReport;
    private TextView tvDocumentId;
    private TextView tvBase64Content;

    private ProgressDialog progressDialog;

    private DisputesReportAdapter disputesReportAdapter;
    private DisputesReportViewModel disputesReportViewModel;
    private DocumentContent currentDocumentContent;

    private MaterialButtonToggleGroup materialButtonToggleGroup;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_disputes_report;
    }

    @Override
    protected void initDependencies() {
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        disputesReportViewModel = new ViewModelProvider(this, factory).get(DisputesReportViewModel.class);
        disputesReportAdapter = new DisputesReportAdapter();
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.disputes_report);
        customToolbar.setOnBackButtonListener(v -> close());

        errorTextView = findViewById(R.id.errorTextView);

        cvDocumentReport = findViewById(R.id.cvDocumentReport);
        tvDocumentId = findViewById(R.id.tvDocumentId);
        tvBase64Content = findViewById(R.id.tvBase64Content);

        materialButtonToggleGroup = findViewById(R.id.btgMethods);

        Button btExportAsPDF = findViewById(R.id.btExportAsPDF);
        btExportAsPDF.setOnClickListener(v -> exportAsPDF());

        Button btViewAsPDF = findViewById(R.id.btViewAsPDF);
        btViewAsPDF.setOnClickListener(v -> viewAsPDF());

        recyclerView = findViewById(R.id.recyclerView);

        Button btGetDisputesList = findViewById(R.id.btGetDisputesList);
        btGetDisputesList.setOnClickListener(v -> showDisputesReportDialog(DISPUTE_LIST));

        Button btGetDisputeById = findViewById(R.id.btGetDisputeById);
        btGetDisputeById.setOnClickListener(v -> showDisputesReportDialog(DISPUTE_BY_ID));

        Button btGetDisputeByDepositId = findViewById(R.id.btGetDisputeByDepositId);
        btGetDisputeByDepositId.setOnClickListener(v -> showDisputesReportDialog(DISPUTE_BY_DEPOSIT_ID));

        Button btGetDocumentById = findViewById(R.id.btGetDocumentById);
        btGetDocumentById.setOnClickListener(v -> showDisputesReportDialog(DOCUMENT_BY_ID));

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(null);

        recyclerView.setAdapter(disputesReportAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (Boolean.TRUE.equals(disputesReportViewModel.getProgressStatus().getValue()))
                    return;

                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == disputesReportAdapter.getItemCount() - 1) {
                    disputesReportViewModel.loadMore();
                }
            }
        });
    }

    @Override
    public void onDialogCanceled() {
        materialButtonToggleGroup.clearChecked();
    }

    private void showDisputesReportDialog(DisputesReportDialog.TYPE type) {
        DisputesReportDialog disputesReportDialog = DisputesReportDialog.newInstance(this, type);
        disputesReportDialog.show(requireFragmentManager(), DisputesReportDialog.class.getSimpleName());
    }

    @Override
    protected void initSubscriptions() {
        disputesReportViewModel.getProgressStatus().observe(this, show -> {
            if (show) {
                hideViews(errorTextView, cvDocumentReport);
                progressDialog.show();
            } else {
                progressDialog.hide();
            }
        });

        disputesReportViewModel.getError().observe(this, errorMessage -> {
            hideViews(recyclerView, cvDocumentReport);
            showView(errorTextView);
            if (errorMessage.contains("Empty") && errorMessage.contains("List")) {
                errorTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack));
                errorTextView.setText(R.string.empty_list);
            } else {
                errorTextView.setText(errorMessage);
            }
        });

        disputesReportViewModel.getDisputesLiveData().observe(this, disputes -> {
            hideViews(errorTextView, cvDocumentReport);
            showView(recyclerView);
            submitDisputes(disputes);
        });

        disputesReportViewModel.getDocumentContentLiveData().observe(this, documentContent -> {
            hideViews(errorTextView, recyclerView);
            showView(cvDocumentReport);
            submitDocumentContent(documentContent);
        });

        disputesReportViewModel.getDocumentTemporaryFileLiveData().observe(this, this::showAsPDF);
    }

    private void submitDisputes(List<DisputeSummary> disputes) {
        disputesReportAdapter.setExpandedByDefault(disputes.size() == 1);
        disputesReportAdapter.addItems(disputes);
    }

    private void submitDocumentContent(DocumentContent documentContent) {
        currentDocumentContent = documentContent;
        tvDocumentId.setText(documentContent.getDocumentId());

        // System limitation to avoid OOM
        String base64Content = documentContent.getBase64Content();
        String base64ContentPreview;

        if (base64Content.length() > 10000) {
            base64ContentPreview = base64Content.substring(0, 10000);
        } else {
            base64ContentPreview = base64Content;
        }

        tvBase64Content.setText(base64ContentPreview);
    }

    private void exportAsPDF() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        String documentOutputFilename = currentDocumentContent.getDocumentId() + ".pdf";
        intent.putExtra(Intent.EXTRA_TITLE, documentOutputFilename);

        if (IntentUtils.canBeHandled(intent, requireActivity())) {
            startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
        } else {
            Toast.makeText(requireContext(), R.string.error_no_app_to_handle_action, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == CREATE_FILE_REQUEST_CODE) {
            if (intent != null && intent.getData() != null) {
                disputesReportViewModel.exportDocumentContentToUri(intent.getData(),
                        currentDocumentContent.getBase64Content());
            } else {
                Toast.makeText(requireContext(), R.string.common_error_unknown, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void viewAsPDF() {
        handleLoading(true);
        disputesReportViewModel.exportDocumentContentToTempFile(currentDocumentContent.getBase64Content());
    }

    private void showAsPDF(File tempFile) {
        handleLoading(false);
        Context context = requireContext();

        if (tempFile == null) {
            Toast.makeText(context, R.string.common_error_unknown, Toast.LENGTH_LONG).show();
        } else {
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", tempFile);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (IntentUtils.canBeHandled(intent, requireActivity())) {
                startActivity(intent);
            } else {
                Toast.makeText(context, R.string.error_no_app_to_handle_action, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void handleLoading(boolean show) {
        if (show) {
            progressDialog.show();
        } else {
            progressDialog.hide();
        }
    }

    @Override
    public void onSubmitDisputesReportParametersModel(DisputesReportParametersModel disputesReportParametersModel) {
        disputesReportAdapter.clearItems();
        disputesReportViewModel.getDisputesList(disputesReportParametersModel);
    }

    @Override
    public void onSubmitDisputeId(String disputeId, boolean fromSettlements) {
        disputesReportAdapter.clearItems();
        disputesReportViewModel.getDisputeById(disputeId, fromSettlements);
    }

    @Override
    public void onSubmitDisputeByDepositId(String disputeId, Date currentDateAndTime) {
        disputesReportAdapter.clearItems();
        disputesReportViewModel.getDisputeByDepositId(disputeId, currentDateAndTime);
    }

    @Override
    public void onSubmitDocumentReportModel(DocumentReportModel documentReportModel) {
        disputesReportAdapter.clearItems();
        disputesReportViewModel.getDocument(documentReportModel);
    }

    @Override
    public void onDestroyView() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroyView();
    }
}

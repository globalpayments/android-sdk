package com.globalpayments.android.sdk.sample.gpapi.disputes.operations.dialog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.globalpayments.android.sdk.model.DisputeDocumentMimeType;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomSpinner;
import com.globalpayments.android.sdk.sample.gpapi.disputes.operations.model.DisputeDocument;
import com.globalpayments.android.sdk.sample.gpapi.disputes.operations.model.DisputeOperationModel;
import com.globalpayments.android.sdk.sample.gpapi.disputes.operations.model.DisputeOperationType;
import com.globalpayments.android.sdk.utils.ContextUtils;
import com.globalpayments.android.sdk.utils.IntentUtils;

import java.util.ArrayList;
import java.util.List;

import static com.globalpayments.android.sdk.sample.common.Constants.SELECT_FILE_REQUEST_CODE;
import static com.globalpayments.android.sdk.utils.Strings.EMPTY;
import static com.globalpayments.android.sdk.utils.Utils.isNotNull;
import static com.globalpayments.android.sdk.utils.Utils.isNotNullOrBlank;
import static com.globalpayments.android.sdk.utils.ViewUtils.handleViewVisibility;
import static com.globalpayments.android.sdk.utils.ViewUtils.handleViewsVisibility;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

public class DisputeOperationDialog extends BaseDialogFragment {
    private CustomSpinner operationTypeSpinner;
    private EditText etDisputeId;

    // Start - Add Document Container
    private LinearLayout addDocumentContainer;
    private Button btShowAddFileGroupView;
    private LinearLayout addFileGroupView;

    // Add Document GroupView
    private CustomSpinner documentTypeSpinner;
    private TextView tvFileSelect;
    // End - Add Document Container

    // Document List
    private TextView tvAddedDocuments;
    private RecyclerView recyclerView;

    private EditText etIdempotencyKey;

    private DisputeDocumentsAdapter disputeDocumentsAdapter;
    private final List<DisputeDocument> disputeDocumentList = new ArrayList<>();
    private Uri currentSelectedDocumentFileUri;

    public static DisputeOperationDialog newInstance(Fragment targetFragment) {
        DisputeOperationDialog disputeOperationDialog = new DisputeOperationDialog();
        disputeOperationDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        disputeOperationDialog.setTargetFragment(targetFragment, 0);
        return disputeOperationDialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_dispute_operation;
    }

    @Override
    protected void initViews() {
        operationTypeSpinner = findViewById(R.id.operationTypeSpinner);
        etDisputeId = findViewById(R.id.etDisputeId);

        addDocumentContainer = findViewById(R.id.addDocumentContainer);
        btShowAddFileGroupView = findViewById(R.id.btShowAddFileGroupView);
        addFileGroupView = findViewById(R.id.addFileGroupView);

        documentTypeSpinner = findViewById(R.id.documentTypeSpinner);
        tvFileSelect = findViewById(R.id.tvFileSelect);

        tvAddedDocuments = findViewById(R.id.tvAddedDocuments);
        recyclerView = findViewById(R.id.recyclerView);

        etIdempotencyKey = findViewById(R.id.etIdempotencyKey);

        initRecyclerView();
        initSpinners();

        btShowAddFileGroupView.setOnClickListener(v -> handleAddFileGroupViewVisibility(true));

        tvFileSelect.setOnClickListener(v -> startSelectDocument());

        Button btAdd = findViewById(R.id.btAdd);
        btAdd.setOnClickListener(v -> {
            if (isDocumentFileSelected()) {
                addDocument();
                clearAndCloseAddFileGroupView();
            }
        });

        Button btClearAndClose = findViewById(R.id.btClearAndClose);
        btClearAndClose.setOnClickListener(v -> clearAndCloseAddFileGroupView());

        Button btSubmit = findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(v -> submitDisputeOperationModel());
    }

    private void initSpinners() {
        operationTypeSpinner.init(DisputeOperationType.values(), false, this::handleOperationViewsVisibility);
        documentTypeSpinner.init(DisputeDocument.Type.values());
    }

    private void handleOperationViewsVisibility(DisputeOperationType disputeOperationType) {
        switch (disputeOperationType) {
            case ACCEPT:
                clearDocumentList();
                clearAndCloseAddFileGroupView();
                hideView(addDocumentContainer);
                break;

            case CHALLENGE:
                showView(addDocumentContainer);
                break;
        }
    }

    private void handleAddFileGroupViewVisibility(boolean show) {
        handleViewVisibility(btShowAddFileGroupView, !show);
        handleViewVisibility(addFileGroupView, show);
    }

    private void clearAndCloseAddFileGroupView() {
        documentTypeSpinner.setSelection(0);
        tvFileSelect.setText(EMPTY);
        currentSelectedDocumentFileUri = null;
        handleAddFileGroupViewVisibility(false);
    }

    private void initRecyclerView() {
        disputeDocumentsAdapter = new DisputeDocumentsAdapter(this::removeDocument);
        recyclerView.setAdapter(disputeDocumentsAdapter);
    }

    private void clearDocumentList() {
        disputeDocumentList.clear();
        reloadDocumentsList();
    }

    private void removeDocument(DisputeDocument disputeDocument) {
        disputeDocumentList.remove(disputeDocument);
        reloadDocumentsList();
    }

    private boolean isDocumentFileSelected() {
        String filename = tvFileSelect.getText().toString();
        boolean isDocumentFileSelected = isNotNullOrBlank(filename) && isNotNull(currentSelectedDocumentFileUri);

        if (!isDocumentFileSelected) {
            Toast.makeText(requireContext(), R.string.please_select_a_document_file, Toast.LENGTH_LONG).show();
        }

        return isDocumentFileSelected;
    }

    private void addDocument() {
        String filename = tvFileSelect.getText().toString();
        DisputeDocument.Type type = documentTypeSpinner.getSelectedOption();
        DisputeDocument disputeDocument = new DisputeDocument(filename, type, currentSelectedDocumentFileUri);
        disputeDocumentList.add(disputeDocument);
        reloadDocumentsList();
    }

    private void reloadDocumentsList() {
        handleViewsVisibility(!disputeDocumentList.isEmpty(), tvAddedDocuments, recyclerView);
        disputeDocumentsAdapter.submitList(disputeDocumentList);
    }

    private void startSelectDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, DisputeDocumentMimeType.getMimeTypes());

        if (IntentUtils.canBeHandled(intent, requireActivity())) {
            startActivityForResult(intent, SELECT_FILE_REQUEST_CODE);
        } else {
            Toast.makeText(requireContext(), R.string.error_no_app_to_handle_action, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_FILE_REQUEST_CODE) {
            if (intent != null && intent.getData() != null) {
                onDocumentFileSelected(intent.getData());
            } else {
                Toast.makeText(requireContext(), R.string.common_error_unknown, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void onDocumentFileSelected(Uri selectedDocumentFileUri) {
        currentSelectedDocumentFileUri = selectedDocumentFileUri;
        tvFileSelect.setText(ContextUtils.getFileNameFromUri(selectedDocumentFileUri, requireContext()));
    }

    private DisputeOperationModel buildDisputeOperationModel() {
        DisputeOperationModel disputeOperationModel = new DisputeOperationModel();
        disputeOperationModel.setDisputeOperationType(operationTypeSpinner.getSelectedOption());
        disputeOperationModel.setDisputeId(etDisputeId.getText().toString());
        disputeOperationModel.setDisputeDocumentList(disputeDocumentList);
        disputeOperationModel.setIdempotencyKey(etIdempotencyKey.getText().toString());
        return disputeOperationModel;
    }

    private void submitDisputeOperationModel() {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof Callback) {
            Callback callback = (Callback) targetFragment;
            callback.onSubmitDisputeOperationModel(buildDisputeOperationModel());
        }
        dismiss();
    }

    public interface Callback {
        void onSubmitDisputeOperationModel(DisputeOperationModel disputeOperationModel);
    }
}

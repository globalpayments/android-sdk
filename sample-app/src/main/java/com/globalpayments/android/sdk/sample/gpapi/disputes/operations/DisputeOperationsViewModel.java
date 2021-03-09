package com.globalpayments.android.sdk.sample.gpapi.disputes.operations;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.entities.Transaction;
import com.global.api.entities.reporting.DisputeSummary;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.common.base.BaseAndroidViewModel;
import com.globalpayments.android.sdk.sample.gpapi.disputes.operations.model.DisputeDocument;
import com.globalpayments.android.sdk.sample.gpapi.disputes.operations.model.DisputeOperationModel;

import java.util.ArrayList;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;
import static com.globalpayments.android.sdk.utils.Base64Utils.getBase64EncodedContent;

public class DisputeOperationsViewModel extends BaseAndroidViewModel {
    private MutableLiveData<Transaction> transactionLiveData = new MutableLiveData<>();

    public DisputeOperationsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Transaction> getTransactionLiveData() {
        return transactionLiveData;
    }

    private void showResult(Transaction transaction) {
        hideProgress();
        transactionLiveData.setValue(transaction);
    }

    public void executeDisputeOperation(DisputeOperationModel disputeOperationModel) {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<Transaction>() {
            @Override
            public Transaction executeAsync() throws Exception {
                return executeRequest(disputeOperationModel);
            }

            @Override
            public void onSuccess(Transaction value) {
                showResult(value);
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    private Transaction executeRequest(DisputeOperationModel disputeOperationModel) throws Exception {
        Transaction transaction = null;

        switch (disputeOperationModel.getDisputeOperationType()) {
            case ACCEPT:
                transaction = executeAcceptDisputeRequest(disputeOperationModel);
                break;

            case CHALLENGE:
                transaction = executeChallengeDisputeRequest(disputeOperationModel);
                break;
        }

        return transaction;
    }

    private Transaction executeAcceptDisputeRequest(DisputeOperationModel disputeOperationModel) throws Exception {
        String disputeId = disputeOperationModel.getDisputeId();

        DisputeSummary disputeSummary = new DisputeSummary();
        disputeSummary.setCaseId(disputeId);

        return disputeSummary
                .accept()
                .withIdempotencyKey(disputeOperationModel.getIdempotencyKey())
                .execute(DEFAULT_GPAPI_CONFIG);
    }

    private Transaction executeChallengeDisputeRequest(DisputeOperationModel disputeOperationModel) throws Exception {
        String disputeId = disputeOperationModel.getDisputeId();

        DisputeSummary disputeSummary = new DisputeSummary();
        disputeSummary.setCaseId(disputeId);

        ArrayList<com.global.api.entities.DisputeDocument> disputeDocuments = new ArrayList<>();

        for (DisputeDocument disputeDocument : disputeOperationModel.getDisputeDocumentList()) {
            String disputeDocumentType = disputeDocument.getType().toString();
            String base64EncodedDocumentFile = getBase64EncodedContent(disputeDocument.getUri(), getApplication());

            com.global.api.entities.DisputeDocument apiDisputeDocument = new com.global.api.entities.DisputeDocument();
            apiDisputeDocument.setType(disputeDocumentType);
            apiDisputeDocument.setBase64Content(base64EncodedDocumentFile);

            disputeDocuments.add(apiDisputeDocument);
        }

        return disputeSummary
                .challenge(disputeDocuments)
                .withIdempotencyKey(disputeOperationModel.getIdempotencyKey())
                .execute(DEFAULT_GPAPI_CONFIG);
    }
}
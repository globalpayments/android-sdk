package com.globalpayments.android.sdk.sample.gpapi.disputes.report;

import static com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG;
import static com.globalpayments.android.sdk.utils.ContextUtils.getAppDocumentsDirectory;
import static com.globalpayments.android.sdk.utils.ContextUtils.getOutputStreamForUri;

import android.app.Application;
import android.net.Uri;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.global.api.builders.TransactionReportBuilder;
import com.global.api.entities.DisputeDocument;
import com.global.api.entities.reporting.DataServiceCriteria;
import com.global.api.entities.reporting.DisputeSummary;
import com.global.api.entities.reporting.DisputeSummaryPaged;
import com.global.api.entities.reporting.SearchCriteria;
import com.global.api.services.ReportingService;
import com.globalpayments.android.sdk.TaskExecutor;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.ActionLiveData;
import com.globalpayments.android.sdk.sample.common.base.BaseAndroidViewModel;
import com.globalpayments.android.sdk.sample.gpapi.disputes.report.model.DisputesReportParametersModel;
import com.globalpayments.android.sdk.sample.gpapi.disputes.report.model.DocumentContent;
import com.globalpayments.android.sdk.sample.gpapi.disputes.report.model.DocumentReportModel;
import com.globalpayments.android.sdk.utils.FileUtils;

import java.io.File;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DisputesReportViewModel extends BaseAndroidViewModel {

    private static final int PAGE_BY_DEPOSIT_ID = 1;
    private static final int PAGE_SIZE_BY_DEPOSIT_ID = 1;
    private static final String TEMPORARY_PDF_FILE_NAME = "temporary_file.pdf";

    private final MutableLiveData<List<DisputeSummary>> disputesLiveData = new MutableLiveData<>();
    private final MutableLiveData<DocumentContent> documentContentLiveData = new MutableLiveData<>();
    private final MutableLiveData<File> documentTemporaryFileLiveData = new ActionLiveData<>();

    private DisputesReportParametersModel currentReportParameters;
    private int pageSize = 0;
    private int currentPage = 1;
    private int totalRecordCount = -1;

    public DisputesReportViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<DisputeSummary>> getDisputesLiveData() {
        return disputesLiveData;
    }

    public LiveData<DocumentContent> getDocumentContentLiveData() {
        return documentContentLiveData;
    }

    public LiveData<File> getDocumentTemporaryFileLiveData() {
        return documentTemporaryFileLiveData;
    }

    public void getDisputesList(DisputesReportParametersModel disputesReportParametersModel) {
        resetPagination();
        currentReportParameters = disputesReportParametersModel;
        pageSize = disputesReportParametersModel.getPageSize();
        getDisputesList();
    }

    public void loadMore() {
        boolean hasMore = pageSize * currentPage < totalRecordCount;
        if (!hasMore || Boolean.TRUE.equals(getProgressStatus().getValue())) return;
        currentPage += 1;
        currentReportParameters.setPage(currentPage);
        getDisputesList();
    }

    private void resetPagination() {
        currentPage = 1;
        totalRecordCount = -1;
        currentReportParameters = null;
    }

    private void getDisputesList() {
        showProgress();

        TaskExecutor.executeAsync(new TaskExecutor.Task<List<DisputeSummary>>() {
            @Override
            public List<DisputeSummary> executeAsync() throws Exception {
                return executeGetDisputesListRequest(currentReportParameters);
            }

            @Override
            public void onSuccess(List<DisputeSummary> value) {
                showResult(value);
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    public void getDisputeByDepositId(String depositId, Date currentDateAndTime) {
        showProgress();
        resetPagination();
        TaskExecutor.executeAsync(new TaskExecutor.Task<List<DisputeSummary>>() {
            @Override
            public List<DisputeSummary> executeAsync() throws Exception {
                return executeGetDisputeByDepositRequest(depositId, currentDateAndTime);
            }

            @Override
            public void onSuccess(List<DisputeSummary> value) {
                showResult(value);
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    public void getDisputeById(String disputeId, boolean fromSettlements) {
        showProgress();
        resetPagination();
        TaskExecutor.executeAsync(new TaskExecutor.Task<DisputeSummary>() {
            @Override
            public DisputeSummary executeAsync() throws Exception {
                return executeGetDisputeByIdRequest(disputeId, fromSettlements);
            }

            @Override
            public void onSuccess(DisputeSummary value) {
                showResult(Collections.singletonList(value));
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    public void getDocument(DocumentReportModel documentReportModel) {
        showProgress();
        resetPagination();
        TaskExecutor.executeAsync(new TaskExecutor.Task<DocumentContent>() {
            @Override
            public DocumentContent executeAsync() throws Exception {
                return executeGetDocumentRequest(documentReportModel);
            }

            @Override
            public void onSuccess(DocumentContent value) {
                showDocumentContent(value);
            }

            @Override
            public void onError(Exception exception) {
                showError(exception);
            }
        });
    }

    private void showDocumentContent(DocumentContent documentContent) {
        hideProgress();
        documentContentLiveData.setValue(documentContent);
    }

    private void showResult(List<DisputeSummary> disputes) {
        if (disputes == null || disputes.isEmpty()) {
            showError(new Exception("Empty Disputes List"));
        } else {
            hideProgress();
            disputesLiveData.setValue(disputes);
        }
    }

    public void exportDocumentContentToUri(Uri uri, String base64Content) {
        TaskExecutor.executeAsync(new TaskExecutor.Task<Boolean>() {
            @Override
            public Boolean executeAsync() throws Exception {
                byte[] bytesArray = Base64.decode(base64Content, Base64.DEFAULT);
                OutputStream outputStream = getOutputStreamForUri(uri, getApplication());
                return FileUtils.writeByteArrayToOutputStream(bytesArray, outputStream);
            }

            @Override
            public void onSuccess(Boolean value) {
                int message = value ? R.string.success : R.string.common_error_unknown;
                Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(getApplication(), R.string.common_error_unknown, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void exportDocumentContentToTempFile(String base64Content) {
        TaskExecutor.executeAsync(new TaskExecutor.Task<File>() {
            @Override
            public File executeAsync() throws Exception {
                byte[] bytesArray = Base64.decode(base64Content, Base64.DEFAULT);
                File file = new File(getAppDocumentsDirectory(getApplication()), TEMPORARY_PDF_FILE_NAME);
                return FileUtils.writeByteArrayToFile(bytesArray, file) ? file : null;
            }

            @Override
            public void onSuccess(File value) {
                documentTemporaryFileLiveData.setValue(value);
            }

            @Override
            public void onError(Exception exception) {
                documentTemporaryFileLiveData.setValue(null);
            }
        });
    }

    private DocumentContent executeGetDocumentRequest(DocumentReportModel documentReportModel) throws Exception {
        DocumentContent documentContent = new DocumentContent();

        String disputeId = documentReportModel.getDisputeId();
        String documentId = documentReportModel.getDocumentId();

        DisputeDocument disputeDocument = ReportingService
                .documentDisputeDetail(disputeId)
                .where(SearchCriteria.DisputeDocumentId, documentId)
                .execute(DEFAULT_GPAPI_CONFIG);

        documentContent.setBase64Content(disputeDocument.getBase64Content());
        documentContent.setDocumentId(disputeDocument.getId());

        return documentContent;
    }

    private DisputeSummary executeGetDisputeByIdRequest(String disputeId, boolean fromSettlements) throws Exception {
        TransactionReportBuilder<DisputeSummary> reportBuilder = fromSettlements
                ? ReportingService.settlementDisputeDetail(disputeId)
                : ReportingService.disputeDetail(disputeId);
        return reportBuilder.execute(DEFAULT_GPAPI_CONFIG);
    }

    private List<DisputeSummary> executeGetDisputesListRequest(DisputesReportParametersModel parametersModel) throws Exception {
        int page = parametersModel.getPage();
        int pageSize = parametersModel.getPageSize();

        TransactionReportBuilder<DisputeSummaryPaged> reportBuilder = parametersModel.isFromSettlements()
                ? ReportingService.findSettlementDisputesPaged(page, pageSize)
                : ReportingService.findDisputesPaged(page, pageSize);
        reportBuilder.orderBy(parametersModel.getOrderBy(), parametersModel.getOrder());
        reportBuilder.setDisputeOrderBy(parametersModel.getOrderBy());
        reportBuilder.where(SearchCriteria.AquirerReferenceNumber, parametersModel.getArn())
                .and(SearchCriteria.CardBrand, parametersModel.getBrand())
                .and(SearchCriteria.DisputeStatus, parametersModel.getStatus())
                .and(SearchCriteria.DisputeStage, parametersModel.getStage())
                .and(DataServiceCriteria.StartStageDate, parametersModel.getFromStageTimeCreated())
                .and(DataServiceCriteria.EndStageDate, parametersModel.getToStageTimeCreated())
                .and(DataServiceCriteria.MerchantId, parametersModel.getSystemMID())
                .and(DataServiceCriteria.SystemHierarchy, parametersModel.getSystemHierarchy());

        DisputeSummaryPaged result = reportBuilder.execute(DEFAULT_GPAPI_CONFIG);
        if (currentPage == 1) {
            totalRecordCount = result.totalRecordCount;
        }
        return result.getResults();
    }

    private List<DisputeSummary> executeGetDisputeByDepositRequest(String depositId, Date currentDateAndTime) throws Exception {
        DisputeSummaryPaged result =
                ReportingService
                        .findSettlementDisputesPaged(PAGE_BY_DEPOSIT_ID, PAGE_SIZE_BY_DEPOSIT_ID)
                        .where(DataServiceCriteria.DepositReference, depositId)
                        .execute(DEFAULT_GPAPI_CONFIG);

        return result.getResults();
    }
}
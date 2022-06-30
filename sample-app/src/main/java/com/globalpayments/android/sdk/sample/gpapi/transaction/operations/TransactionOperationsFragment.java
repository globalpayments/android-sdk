package com.globalpayments.android.sdk.sample.gpapi.transaction.operations;

import static com.globalpayments.android.sdk.sample.common.Constants.CHALLENGE_SUCCESS;
import static com.globalpayments.android.sdk.sample.common.Constants.VISA_3DS1_ENROLLED;
import static com.globalpayments.android.sdk.sample.common.Constants.VISA_3DS1_NOT_ENROLLED;
import static com.globalpayments.android.sdk.sample.common.Constants.VISA_3DS2_CHALLENGE;
import static com.globalpayments.android.sdk.sample.common.Constants.VISA_3DS2_FRICTIONLESS;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;
import static org.apache.commons.codec.CharEncoding.UTF_8;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.global.api.entities.ThreeDSecure;
import com.global.api.entities.enums.Channel;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.configuration.GPAPIConfiguration;
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationModel;
import com.globalpayments.android.sdk.sample.utils.AppPreferences;

import org.apache.http.util.EncodingUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TransactionOperationsFragment extends BaseFragment implements TransactionOperationDialog.Callback {
    private ProgressBar progressBar;
    private TextView errorTextView;
    private CardView cvTransaction;
    private TransactionView transactionView;
    private LinearLayout linearLayoutExample;
    private WebView webView;
    private WebView wvThreeDSChallenge;
    private ThreeDSecure typeMessage;
    private TransactionOperationModel operationModel;

    private TransactionOperationsViewModel transactionOperationsViewModel;

    GPAPIConfiguration gpapiConfiguration;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_transaction_operations;
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.transaction_operations);
        customToolbar.setOnBackButtonListener(v -> close());

        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);

        cvTransaction = findViewById(R.id.cvTransaction);
        transactionView = findViewById(R.id.transactionView);

        Button btCreateTransaction = findViewById(R.id.btCreateTransaction);
        btCreateTransaction.setOnClickListener(v -> showTransactionOperationDialog());

        linearLayoutExample = findViewById(R.id.linearLayoutExample);
        wvThreeDSChallenge = findViewById(R.id.wvthreeDSChallenge);

        AppPreferences appPreferences = new AppPreferences(requireContext());
        gpapiConfiguration = appPreferences.getGPAPIConfiguration();

        WebView.setWebContentsDebuggingEnabled(true);
    }

    private void showTransactionOperationDialog() {
        TransactionOperationDialog transactionOperationDialog = TransactionOperationDialog.newInstance(this);
        transactionOperationDialog.show(requireFragmentManager(), TransactionOperationDialog.class.getSimpleName());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        transactionOperationsViewModel.init();
    }

    @Override
    protected void initDependencies() {
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        transactionOperationsViewModel = new ViewModelProvider(this, factory).get(TransactionOperationsViewModel.class);
    }

    @Override
    public void onSubmitTransactionOperationModel(TransactionOperationModel transactionOperationModel) {
        if (transactionOperationModel.isUse3DS()) {
            webView.removeAllViews();
            operationModel = transactionOperationModel;
            transactionOperationsViewModel.executeTransactionOperationWith3ds(transactionOperationModel);
        } else {
            transactionOperationsViewModel.executeTransactionOperation(transactionOperationModel);
        }
    }

    @Override
    protected void initSubscriptions() {
        webView = new WebView(getContext());
        transactionOperationsViewModel.getProgressStatus().observe(this, show -> {
            if (show) {
                hideView(cvTransaction);
                hideView(errorTextView);
                showView(progressBar);
            } else {
                hideView(progressBar);
            }
        });

        transactionOperationsViewModel.getError().observe(this, errorMessage -> {
            hideView(cvTransaction);
            showView(errorTextView);
            if (errorMessage.contains("Empty") && errorMessage.contains("List")) {
                errorTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack));
                errorTextView.setText(R.string.empty_list);
            } else {
                errorTextView.setText(errorMessage);
            }
        });

        transactionOperationsViewModel.getTransactionLiveData().observe(this, transaction -> {
            hideView(errorTextView);
            hideView(webView);
            showView(cvTransaction);
            transactionView.bind(transaction);
        });

        transactionOperationsViewModel.getTypeCardSelectedOption().observe(this, result ->
                typeMessage = result
        );

        transactionOperationsViewModel.getTransactionMessageError().observe(this, result -> {
            if (result.contains("Empty") && result.contains("List")) {
                errorTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack));
                errorTextView.setText(R.string.empty_list);
            } else {
                errorTextView.setText(result);
            }
        });

        transactionOperationsViewModel.getTransactionLiveDataError().observe(this, result -> {
            showView(errorTextView);
            showView(cvTransaction);
            hideView(progressBar);
            transactionView.bind(result);
        });

        transactionOperationsViewModel.getUrlToWebView().observe(this, urls -> {
            String postData = "";
            switch (operationModel.getTypeCardOption()) {
                case VISA_3DS2_FRICTIONLESS:
                case VISA_3DS1_NOT_ENROLLED:
                    transactionOperationsViewModel.executeFinishTransactionOperationWith3ds(urls.get(0));
                    break;
                case VISA_3DS1_ENROLLED:
                    try {
                        postData = typeMessage.getMessageType() + "=" + URLEncoder.encode(urls.get(1), "UTF-8")
                                + "&" + "TermUrl=" + URLEncoder.encode(urls.get(2), "UTF-8")
                                + "&" + urls.get(3) + "=" + URLEncoder.encode(urls.get(4), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String strURL = urls.get(0) + "?";
                    String query;
                    query = postData;
                    strURL += query;
                    // Configure related browser settings
                    showView(wvThreeDSChallenge);
                    wvThreeDSChallenge.getSettings().setJavaScriptEnabled(true);
                    wvThreeDSChallenge.getSettings().setDomStorageEnabled(true);
                    wvThreeDSChallenge.getSettings().setDatabaseEnabled(true);
                    wvThreeDSChallenge.getSettings().setLoadWithOverviewMode(true);
                    wvThreeDSChallenge.getSettings().setUseWideViewPort(true);

                    wvThreeDSChallenge.getSettings().setSupportZoom(true);
                    wvThreeDSChallenge.getSettings().setBuiltInZoomControls(true);
                    wvThreeDSChallenge.getSettings().setDisplayZoomControls(false);

                    wvThreeDSChallenge.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                    wvThreeDSChallenge.setScrollbarFadingEnabled(false);

                    wvThreeDSChallenge.loadUrl(strURL);

                    wvThreeDSChallenge.setFindListener((activeMatchOrdinal, numberOfMatches, isDoneCounting) -> {
                        if (isDoneCounting) {
                            if (numberOfMatches == 1) {
                                hideView(webView);
                                transactionOperationsViewModel.executeFinishTransactionOperationWith3ds(urls.get(0));
                            }
                        } else if (numberOfMatches == 0) {
                            showView(errorTextView);
                            errorTextView.setText(getString(R.string.failed_challenge));
                        }
                    });
                    wvThreeDSChallenge.findAllAsync(CHALLENGE_SUCCESS);
                    break;
                case VISA_3DS2_CHALLENGE:
                    webView.getSettings().setJavaScriptEnabled(true);
                    postData = typeMessage.getMessageType() + "=" + urls.get(1);
                    webView.postUrl(urls.get(0), EncodingUtils.getBytes(postData, UTF_8));
                    linearLayoutExample.addView(webView);

                    webView.setFindListener((activeMatchOrdinal, numberOfMatches, isDoneCounting) -> {
                        if (isDoneCounting) {
                            if (numberOfMatches == 1) {
                                hideView(webView);
                                transactionOperationsViewModel.executeFinishTransactionOperationWith3ds(urls.get(0));
                            }
                        } else if (numberOfMatches == 0) {
                            showView(errorTextView);
                            errorTextView.setText(getString(R.string.failed_challenge));
                        }
                    });
                    webView.findAllAsync(CHALLENGE_SUCCESS);
                    break;
            }
        });
    }

}

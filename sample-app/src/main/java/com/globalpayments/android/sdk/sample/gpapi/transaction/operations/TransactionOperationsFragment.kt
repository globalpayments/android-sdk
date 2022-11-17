package com.globalpayments.android.sdk.sample.gpapi.transaction.operations

import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.webkit.WebView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.global.api.ServicesContainer
import com.global.api.entities.FraudManagementResponse
import com.global.api.entities.ThreeDSecure
import com.global.api.entities.Transaction
import com.global.api.entities.enums.Channel
import com.global.api.entities.enums.FraudFilterResult
import com.global.api.gateways.GpApiConnector
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationModel
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationType
import com.globalpayments.android.sdk.utils.ViewUtils
import com.globalpayments.android.sdk.utils.bindView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup

class TransactionOperationsFragment : BaseFragment(), TransactionOperationDialog.Callback {

    private val progressBar: ProgressBar by bindView(R.id.progressBar)
    private val errorTextView: TextView by bindView(R.id.errorTextView)
    private val cvTransaction: CardView by bindView(R.id.cvTransaction)
    private val transactionView: TransactionView by bindView(R.id.transactionView)
    private var webView: WebView? = null

    private val actionButtonLayout: MaterialButtonToggleGroup by bindView(R.id.actionButtonGroups)

    private val transactionOperationsViewModel: TransactionOperationsViewModel by viewModels()

    override fun getLayoutId() = R.layout.fragment_transaction_operations

    override fun initViews() {
        val customToolbar = findViewById<CustomToolbar>(R.id.toolbar)
        customToolbar.setTitle(R.string.transaction_operations)
        customToolbar.setOnBackButtonListener { close() }
        val btCreateTransaction = findViewById<Button>(R.id.btCreateTransaction)
        btCreateTransaction.setOnClickListener { v: View? -> showTransactionOperationDialog() }
        WebView.setWebContentsDebuggingEnabled(true)
        actionButtonLayout.addOnButtonCheckedListener { group, _, _ -> group.clearChecked() }
    }

    private fun showTransactionOperationDialog() {
        val transactionOperationDialog = TransactionOperationDialog.newInstance(this)
        transactionOperationDialog.show(
            parentFragmentManager, TransactionOperationDialog::class.java.simpleName
        )
    }

    override fun onSubmitTransactionOperationModel(transactionOperationModel: TransactionOperationModel) {
        transactionOperationsViewModel.executeTransactionOperation(transactionOperationModel)
    }

    override fun initSubscriptions() {
        webView = WebView(requireContext())
        transactionOperationsViewModel.progressStatus.observe(viewLifecycleOwner) { show: Boolean ->
            if (show) {
                ViewUtils.hideView(cvTransaction)
                ViewUtils.hideView(errorTextView)
                ViewUtils.showView(progressBar)
            } else {
                ViewUtils.hideView(progressBar)
            }
        }
        transactionOperationsViewModel.transactionType.observe(
            viewLifecycleOwner, this::onTransactionOperationTypeReceived
        )
        transactionOperationsViewModel.error.observe(viewLifecycleOwner) { errorMessage: String ->
            ViewUtils.hideView(cvTransaction)
            ViewUtils.showView(errorTextView)
            if (errorMessage.contains("Empty") && errorMessage.contains("List")) {
                errorTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.colorBlack
                    )
                )
                errorTextView.setText(R.string.empty_list)
            } else {
                errorTextView.text = errorMessage
            }
        }
        transactionOperationsViewModel.transactionLiveData.observe(viewLifecycleOwner) { transaction: Transaction ->
            ViewUtils.hideView(errorTextView)
            ViewUtils.hideView(webView)
            ViewUtils.showView(cvTransaction)
            transactionView.bind(transaction)
        }
        transactionOperationsViewModel.transactionMessageError.observe(viewLifecycleOwner) { result: String ->
            if (result.contains("Empty") && result.contains("List")) {
                errorTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.colorBlack
                    )
                )
                errorTextView.setText(R.string.empty_list)
            } else {
                errorTextView.text = result
            }
        }
        transactionOperationsViewModel.transactionLiveDataError.observe(viewLifecycleOwner) { result: ThreeDSecure ->
            ViewUtils.showView(errorTextView)
            ViewUtils.showView(cvTransaction)
            ViewUtils.hideView(progressBar)
            transactionView.bind(result)
        }
    }

    private fun onTransactionOperationTypeReceived(transactionOperationType: TransactionOperationType?) {
        actionButtonLayout.removeAllViews()
        if (transactionOperationType == null) {
            return
        }
        val fraudFilterResponse = transactionOperationsViewModel
            .transactionLiveData
            .value
            ?.fraudFilterResponse
        val transactionChildren = transactionOperationType.children.filterAgainstFraudResult(fraudFilterResponse)
        for (operation in transactionChildren) {
            val button = createButtonForTransactionOperationType(operation) ?: continue
            actionButtonLayout.addView(button)
        }
    }

    private fun List<TransactionOperationType>.filterAgainstFraudResult(fraudFilterResponse: FraudManagementResponse?): List<TransactionOperationType> {
        val filterResult = fraudFilterResponse?.fraudResponseResult?.asFraudFilterResult()
        return this
            .asSequence()
            .filter { it != TransactionOperationType.PendingReviewSale && it != TransactionOperationType.PendingReviewAuthorization }
            .filter {
                if (FraudFilterResult.PASS != filterResult) {
                    it != TransactionOperationType.HoldSale && it != TransactionOperationType.HoldAuthorization
                } else {
                    true
                }
            }
            .toList()
    }

    private fun createButtonForTransactionOperationType(transactionOperationType: TransactionOperationType): Button? {

        fun createButton(
            title: String, onClicked: () -> Unit, isEnabled: Boolean = true, hint: String? = null
        ): Button {
            return MaterialButton(requireContext(), null, R.attr.materialButtonOutlinedStyle).apply {
                layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                text = title
                setOnClickListener {
                    if (isEnabled) {
                        onClicked()
                    } else {
                        showToast(hint)
                    }
                }
            }
        }

        return when (transactionOperationType) {
            TransactionOperationType.Capture -> createButton("Capture", transactionOperationsViewModel::captureTransaction)
            TransactionOperationType.RefundCapture -> createButton("Refund", transactionOperationsViewModel::refundCapture)
            TransactionOperationType.ReverseCapture -> createButton("Reverse", transactionOperationsViewModel::reverseCapture)
            TransactionOperationType.ReverseAuthorization -> createButton("Reverse", transactionOperationsViewModel::reverseAuthorization)
            TransactionOperationType.Reauthorize -> createButton("Reauthorize", transactionOperationsViewModel::reauthorize)
            TransactionOperationType.Increment -> {
                val channel = (ServicesContainer.getInstance().getGateway(Constants.DEFAULT_GPAPI_CONFIG) as? GpApiConnector)?.gpApiConfig?.channel
                createButton(
                    title = "Increment w/10",
                    onClicked = transactionOperationsViewModel::increment,
                    isEnabled = Channel.CardPresent.value == channel,
                    hint = "Channel must be CardPresent"
                )
            }
            TransactionOperationType.CaptureIncrement -> createButton("Capture", transactionOperationsViewModel::captureIncrement)
            TransactionOperationType.ReverseIncrement -> createButton("Reverse", transactionOperationsViewModel::reverseIncrement)
            TransactionOperationType.RefundSale -> createButton("Refund", transactionOperationsViewModel::refundSale)
            TransactionOperationType.ReverseSale -> createButton("Reverse", transactionOperationsViewModel::reverseSale)
            TransactionOperationType.ReverseRefund -> createButton("Reverse", transactionOperationsViewModel::reverseRefund)

            TransactionOperationType.ReleaseHoldAuthorization -> createButton("Release", transactionOperationsViewModel::releaseAuthorizationFraud)
            TransactionOperationType.ReverseHoldAuthorization -> createButton("Reverse", transactionOperationsViewModel::reverseAuthorizationFraud)

            TransactionOperationType.ReleaseHoldSale -> createButton("Release", transactionOperationsViewModel::releaseSaleFraud)

            TransactionOperationType.HoldAuthorization -> createButton("Hold", transactionOperationsViewModel::holdAuthorization)
            TransactionOperationType.HoldSale -> createButton("Hold", transactionOperationsViewModel::holdSale)

            TransactionOperationType.HoldPendingReviewAuthorization -> createButton("Hold", transactionOperationsViewModel::holdAuthorization)
            TransactionOperationType.CapturePendingReviewAuthorization -> createButton(
                "Capture",
                transactionOperationsViewModel::captureAuthorization
            )

            TransactionOperationType.PendingReviewSale -> createButton("Hold", transactionOperationsViewModel::releaseSaleFraud)
            else -> null
        }
    }

    private fun String.asFraudFilterResult(): FraudFilterResult? {
        return FraudFilterResult.values().firstOrNull { it.value == this }
    }
}

package com.globalpayments.android.sdk.sample.gpapi.paypal

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.error.TransactionErrorDialog
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success.TransactionSuccessDialog
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success.TransactionSuccessModel
import com.globalpayments.android.sdk.utils.bindView
import com.globalpayments.android.sdk.utils.openChromeCustomTabs
import java.math.BigDecimal

class PaypalFragment : BaseFragment() {

    private lateinit var paypalViewModel: PaypalViewModel

    private val paypalHost by lazy { requireContext().getString(R.string.paypal_deep_link_host) }
    private val paypalScheme by lazy { requireContext().getString(R.string.paypal_deep_link_scheme) }

    private lateinit var progressBar: ProgressDialog

    private val llPay: LinearLayout by bindView(R.id.ll_pay)
    private val llRefundReverse: LinearLayout by bindView(R.id.ll_refund_reverse)
    private val tvTransactionId: TextView by bindView(R.id.tv_transaction_id)
    private val rgPaymentType: RadioGroup by bindView(R.id.rg_payment_type)

    private val btnRefund: AppCompatButton by bindView(R.id.btn_refund)
    private val btnReverse: AppCompatButton by bindView(R.id.btn_reverse)
    private val amountInput: EditText by bindView(R.id.et_amount)

    override fun getLayoutId(): Int = R.layout.fragment_paypal

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val customToolbar = findViewById<CustomToolbar>(R.id.toolbar)
        customToolbar.setTitle(R.string.paypal)
        customToolbar.setOnBackButtonListener { close() }

        val button = view.findViewById<AppCompatButton>(R.id.button_pay)
        button.setOnClickListener {
            val stringAmount =
                amountInput.text.toString().takeIf { it.isNotBlank() } ?: return@setOnClickListener
            val amount = BigDecimal(stringAmount)
            paypalViewModel.makePayment(paypalScheme, paypalHost, amount)
        }
        paypalViewModel.onUrlReceived.observe(viewLifecycleOwner) { urlToOpen ->
            requireContext().openChromeCustomTabs(
                urlToOpen
            )
        }
        paypalViewModel.progressStatus.observe(viewLifecycleOwner) { if (it) progressBar.show() else progressBar.show() }
        paypalViewModel.error.observe(viewLifecycleOwner, this::dismissProgressAndShowMessage)
        paypalViewModel.onTransactionSuccess.observe(
            viewLifecycleOwner,
            this::showTransactionCompletedDialog
        )
        progressBar = ProgressDialog(requireContext()).apply {
            this.setTitle("Payment in progress")
        }

        btnRefund.setOnClickListener { paypalViewModel.refundTransaction() }
        btnReverse.setOnClickListener { paypalViewModel.reverseTransaction() }

        paypalViewModel.successTransaction.observe(viewLifecycleOwner) { transactionId ->
            if (transactionId.isNullOrBlank()) {
                hideRefundReverseLayout()
                return@observe
            }
            showRefundReverse(transactionId)
        }
        rgPaymentType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_charge -> paypalViewModel.paymentType = PaypalViewModel.PaymentType.Charge
                R.id.rb_authorize -> paypalViewModel.paymentType =
                    PaypalViewModel.PaymentType.Authorize
            }
        }
    }

    override fun initDependencies() {
        paypalViewModel = ViewModelProvider(this)[PaypalViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        paypalViewModel.checkTransaction()
    }

    private fun dismissProgressAndShowMessage(message: String) {
        progressBar.dismiss()
        TransactionErrorDialog.newInstance(message)
            .show(childFragmentManager, TransactionErrorDialog.TAG)
    }

    private fun showRefundReverse(transactionId: String) {
        llPay.isVisible = false
        llRefundReverse.isVisible = true
        tvTransactionId.text = "${getString(R.string.transaction_id)} $transactionId"
        when (paypalViewModel.paymentType) {
            PaypalViewModel.PaymentType.Charge -> {
                btnReverse.isVisible = false
                btnRefund.isVisible = true
            }
            PaypalViewModel.PaymentType.Authorize -> {
                btnReverse.isVisible = true
                btnRefund.isVisible = false
            }
        }
    }

    private fun hideRefundReverseLayout() {
        llPay.isVisible = true
        llRefundReverse.isVisible = false
    }

    private fun showTransactionCompletedDialog(transaction: com.global.api.entities.Transaction) {
        progressBar.dismiss()
        val model = TransactionSuccessModel(
            id = transaction.transactionId,
            resultCode = transaction.responseCode,
            timeCreated = transaction.timestamp,
            status = transaction.responseMessage,
            amount = transaction.balanceAmount.toString(),
        )
        TransactionSuccessDialog.newInstance(model)
            .show(childFragmentManager, TransactionSuccessDialog.TAG)
    }

}

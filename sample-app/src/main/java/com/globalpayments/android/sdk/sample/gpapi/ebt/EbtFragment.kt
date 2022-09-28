package com.globalpayments.android.sdk.sample.gpapi.ebt

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.ScrollView
import androidx.appcompat.widget.Toolbar

import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.error.TransactionErrorDialog
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success.TransactionSuccessDialog
import java.math.BigDecimal
import java.util.*

class EbtFragment : BaseFragment() {

    private val viewModel: EbtViewModel by viewModels()

    private val toolbar: Toolbar by lazy { requireView().findViewById(R.id.toolbar) }

    private val svInput: ScrollView by lazy { requireView().findViewById(R.id.sv_input) }
    private val rgTransactionType: RadioGroup by lazy { requireView().findViewById(R.id.rg_transaction_type) }
    private val etAmount: EditText by lazy { requireView().findViewById(R.id.et_amount) }
    private val etCardNumber: EditText by lazy { requireView().findViewById(R.id.et_card_number) }
    private val etCardExpirationDate: EditText by lazy { requireView().findViewById(R.id.et_card_expiration_date) }
    private val etPinBlock: EditText by lazy { requireView().findViewById(R.id.et_pinblock) }
    private val etAccountHolder: EditText by lazy { requireView().findViewById(R.id.et_account_holder) }


    private val llRefundReverse: View by lazy { requireView().findViewById(R.id.ll_refund_reverse) }
    private val rgRefundReverse: RadioGroup by lazy { requireView().findViewById(R.id.rg_refund_reverse) }
    private val etTransactionId: EditText by lazy { requireView().findViewById(R.id.et_transaction_id) }
    private val etRefundAmount: EditText by lazy { requireView().findViewById(R.id.et_refund_amount) }
    private val btnPlaceOrder: View by lazy { requireView().findViewById(R.id.btn_place_order) }

    private lateinit var progressBar: ProgressDialog

    override fun getLayoutId(): Int = R.layout.fragment_ebt

    override fun initViews() {
        super.initViews()
        progressBar = ProgressDialog(requireContext()).apply {
            this.setTitle("Operation in progress")
        }
        btnPlaceOrder.setOnClickListener { makeTransaction() }
        etCardExpirationDate.setOnClickListener { openCalendar() }

        rgRefundReverse.setOnCheckedChangeListener { _, checkedId ->
            etRefundAmount.isVisible = checkedId == R.id.rb_refund_transaction
        }
    }

    override fun initSubscriptions() {
        super.initSubscriptions()
        viewModel.progressStatus.observe(viewLifecycleOwner) { if (it) progressBar.show() else progressBar.dismiss() }

        viewModel.transactionToShow.observe(viewLifecycleOwner) { transaction ->
            TransactionSuccessDialog.newInstance(transaction)
                .show(childFragmentManager, TransactionSuccessDialog.TAG)
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            TransactionErrorDialog.newInstance(errorMessage)
                .show(childFragmentManager, TransactionErrorDialog.TAG)
        }
        viewModel.transactionToRefund.observe(viewLifecycleOwner) { transaction ->
            if (transaction == null) {
                llRefundReverse.isVisible = false
                svInput.isVisible = true
                toolbar.navigationIcon = null
                return@observe
            }
            toolbar.apply {
                navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.back_arrow, null)
                setNavigationOnClickListener { viewModel.transactionToRefund.postValue(null) }
            }
            svInput.isVisible = false
            etTransactionId.setText(transaction.transactionId)
            llRefundReverse.isVisible = true
        }
    }

    private fun openCalendar() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val timePickerDialog = DatePickerDialog(
            requireContext(),
            { _, y, m, d -> showDate(y, m + 1) },
            year, month, dayOfMonth
        )
        timePickerDialog.show()
    }

    private fun showDate(year: Int, month: Int) {
        etCardExpirationDate.setText("$month/$year")
    }

    private fun makeTransaction() {
        if (!areFieldsCompleted()) return
        if (viewModel.transactionToRefund.value == null) {
            chargeRefund()
        } else {
            refundReverse()
        }
    }

    private fun areFieldsCompleted(): Boolean {
        return etAmount.isFieldCompleted() &&
                etCardNumber.isFieldCompleted() &&
                etCardExpirationDate.isFieldCompleted() &&
                etPinBlock.isFieldCompleted() &&
                etAccountHolder.isFieldCompleted()
    }

    private fun EditText.isFieldCompleted(): Boolean {
        return if (text.isBlank()) {
            error = "Mandatory"
            false
        } else {
            error = null
            true
        }
    }

    private fun chargeRefund() {
        val birtDate = etCardExpirationDate.text.toString()
        if (birtDate.isBlank()) return
        val amount = etAmount.text.toString()
        val cardNumber = etCardNumber.text.toString()
        val (expMonth, expYear) = birtDate.split("/").map { it.toInt() }
        val pinBlock = etPinBlock.text.toString()
        val cardHolderName = etAccountHolder.text.toString()
        when (rgTransactionType.checkedRadioButtonId) {
            R.id.rb_charge -> viewModel.chargeAmount(
                cardNumber,
                expMonth,
                expYear,
                pinBlock,
                cardHolderName,
                amount
            )
            R.id.rb_refund -> viewModel.refundAmount(
                cardNumber,
                expMonth,
                expYear,
                pinBlock,
                cardHolderName,
                amount
            )
        }
    }

    private fun refundReverse() {
        when (rgRefundReverse.checkedRadioButtonId) {
            R.id.rb_refund_transaction -> {
                val amount = etRefundAmount.text.toString().let {
                    if (it.isNotBlank()) {
                        BigDecimal(it)
                    } else null
                }
                viewModel.refundTransaction(amount)
            }
            R.id.rb_reverse_transaction -> viewModel.reverseTransaction()
        }
    }

}
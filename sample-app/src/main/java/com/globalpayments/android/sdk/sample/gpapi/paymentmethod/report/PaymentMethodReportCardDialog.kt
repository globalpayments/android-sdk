package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.global.api.paymentMethods.CreditCardData
import com.globalpayments.android.sdk.sample.R
import java.util.*


class PaymentMethodReportCardDialog : DialogFragment(R.layout.dialog_payment_method_report_card) {

    private val etCardExpirationDate: EditText by lazy { requireView().findViewById(R.id.et_card_expiration_date) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.btn_submit).setOnClickListener {
            submitData()
        }
        etCardExpirationDate.setOnClickListener { openCalendar() }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        (targetFragment as? Callback)?.onDismissWithoutSelection()
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

    private fun submitData() {
        val cardNumber = requireView().findViewById<EditText>(R.id.et_card_number).text.toString()
        val expDate = etCardExpirationDate.text.toString()
        if (expDate.isBlank()) return
        val (expMonth, expYear) = expDate.split("/").map { it.toInt() }
        val cvv = requireView().findViewById<EditText>(R.id.et_cvv).text.toString()
        val cardHolder =
            requireView().findViewById<EditText>(R.id.et_account_holder).text.toString()

        val card = CreditCardData().apply {
            number = cardNumber
            setExpMonth(expMonth)
            setExpYear(expYear)
            cvn = cvv
            cardHolderName = cardHolder
        }
        (targetFragment as? Callback)?.onSubmitPaymentCardMethod(card)
        dismiss()
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.apply {
            val params = attributes.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            attributes = params
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(targetFragment: Fragment): PaymentMethodReportCardDialog {
            return PaymentMethodReportCardDialog().apply {
                setStyle(STYLE_NO_TITLE, 0)
                setTargetFragment(targetFragment, 0)
            }
        }
    }

    interface Callback {
        fun onSubmitPaymentCardMethod(card: CreditCardData)
        fun onDismissWithoutSelection()
    }
}
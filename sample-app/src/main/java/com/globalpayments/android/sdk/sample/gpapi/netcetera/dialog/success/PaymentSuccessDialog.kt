package com.globalpayments.android.sdk.sample.gpapi.netcetera.dialog.success

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.globalpayments.android.sdk.sample.R

class PaymentSuccessDialog : DialogFragment(R.layout.dialog_success_payment) {

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

        val model = arguments?.getParcelable<PaymentSuccessModel>(ModelKey) ?: return

        with(view) {
            findViewById<TextView>(R.id.transaction_id).text = model.id
            findViewById<TextView>(R.id.result_code).text = model.resultCode
            findViewById<TextView>(R.id.time_created).text = model.timeCreated
            findViewById<TextView>(R.id.status).text = model.status
            findViewById<TextView>(R.id.amount).text = model.amount
            findViewById<View>(R.id.ok_button).setOnClickListener { dismiss() }
        }
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
        private const val ModelKey = "model.key"
        fun newInstance(paymentSuccessModel: PaymentSuccessModel): PaymentSuccessDialog {
            val args = bundleOf(ModelKey to paymentSuccessModel)
            val fragment = PaymentSuccessDialog()
            fragment.arguments = args
            return fragment
        }
    }

}
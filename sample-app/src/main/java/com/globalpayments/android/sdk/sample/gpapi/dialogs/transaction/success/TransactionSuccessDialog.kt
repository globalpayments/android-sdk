package com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.globalpayments.android.sdk.sample.R

class TransactionSuccessDialog : DialogFragment(R.layout.dialog_success_transaction) {

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

        val model = arguments?.getParcelable<TransactionSuccessModel>(ModelKey) ?: return

        with(view) {
            findViewById<TextView>(R.id.transaction_id).apply {
                text = model.id
                inputType = InputType.TYPE_NULL
                setTextIsSelectable(true)
            }
            findViewById<TextView>(R.id.result_code).apply {
                text = model.resultCode
                inputType = InputType.TYPE_NULL
                setTextIsSelectable(true)
            }
            findViewById<TextView>(R.id.time_created).apply {
                text = model.timeCreated
                inputType = InputType.TYPE_NULL
                setTextIsSelectable(true)
            }
            findViewById<TextView>(R.id.status).apply {
                text = model.status
                inputType = InputType.TYPE_NULL
                setTextIsSelectable(true)
            }
            findViewById<TextView>(R.id.amount).apply {
                text = model.amount
                inputType = InputType.TYPE_NULL
                setTextIsSelectable(true)
            }
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
        const val TAG = "TransactionSuccessDialog"
        private const val ModelKey = "model.key"
        fun newInstance(transactionSuccessModel: TransactionSuccessModel): TransactionSuccessDialog {
            val args = bundleOf(ModelKey to transactionSuccessModel)
            val fragment = TransactionSuccessDialog()
            fragment.arguments = args
            return fragment
        }
    }

}
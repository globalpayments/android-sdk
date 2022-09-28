package com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.error

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.globalpayments.android.sdk.sample.R

class TransactionErrorDialog : DialogFragment(R.layout.dialog_error_transaction) {

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
        view.findViewById<TextView>(R.id.error_message).text =
            arguments?.getString(ErrorKey) ?: "Generic error"
        view.findViewById<AppCompatButton>(R.id.ok_button).setOnClickListener { dismiss() }
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
        const val TAG = "TransactionErrorDialog"
        private const val ErrorKey = "Error.Key"
        fun newInstance(errorMessage: String): TransactionErrorDialog {
            val args = bundleOf(ErrorKey to errorMessage)
            val fragment = TransactionErrorDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
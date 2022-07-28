package com.globalpayments.android.sdk.ui.cardform

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import com.global.api.paymentMethods.CreditCardData
import com.globalpayments.android.sdk.R


class CardFormDialogFragment : DialogFragment(R.layout.fragment_card_form) {

    var onSubmitClicked: ((CreditCardData) -> Unit)? = null

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
        view.findViewById<CardFormView>(R.id.view_card_form).apply {
            this.onSubmitClicked = {
                this@CardFormDialogFragment.onSubmitClicked?.invoke(it)
                dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.apply {
            val params = attributes.apply {
                width = MATCH_PARENT
                height = WRAP_CONTENT
            }
            attributes = params
        }
    }

    companion object {

        private const val ConfigNameKey = "configName"

        fun newInstance(configName: String): CardFormDialogFragment {
            val args = Bundle().apply {
                putString(ConfigNameKey, configName)
            }
            val fragment = CardFormDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
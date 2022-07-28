package com.globalpayments.android.sdk.utils

import android.text.Editable
import android.text.TextWatcher

abstract class EndTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //no-op
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //no-op
    }

    override fun afterTextChanged(s: Editable) {
        textChanged(s)
    }

    abstract fun textChanged(s: Editable)
}
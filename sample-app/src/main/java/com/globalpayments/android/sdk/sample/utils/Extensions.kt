package com.globalpayments.android.sdk.sample.utils

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun BaseViewModel.launchPrintingError(block: suspend () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
        showProgress()
        try {
            block()
            hideProgress()
        } catch (exception: Exception) {
            showError(exception)
        }
    }
}

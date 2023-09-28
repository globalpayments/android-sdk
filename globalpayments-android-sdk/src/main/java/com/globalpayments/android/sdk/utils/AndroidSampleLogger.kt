package com.globalpayments.android.sdk.utils

import android.util.Log
import com.global.api.logging.IRequestLogger

class AndroidSampleLogger : IRequestLogger {
    override fun RequestSent(request: String?) {
        Log.d("GP", request ?: "")
    }

    override fun ResponseReceived(response: String?) {
        Log.d("GP", response ?: "")
    }
}

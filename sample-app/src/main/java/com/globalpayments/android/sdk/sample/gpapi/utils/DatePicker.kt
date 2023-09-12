package com.globalpayments.android.sdk.sample.gpapi.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR
import java.util.Date

fun showDatePicker(context: Context, onDateSelected: (Date) -> Unit) {
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    val year = calendar.get(YEAR)
    val month = calendar.get(MONTH)
    val day = calendar.get(DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context, { _: DatePicker, y: Int, m: Int, d: Int ->
            calendar.set(y, m, d)
            onDateSelected(calendar.time)
        },
        year,
        month,
        day
    )
    datePickerDialog.show()
}

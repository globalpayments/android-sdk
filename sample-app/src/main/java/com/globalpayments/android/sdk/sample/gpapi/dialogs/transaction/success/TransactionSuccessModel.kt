package com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success

import android.os.Parcelable
import com.global.api.entities.PayerDetails
import com.global.api.entities.Transaction
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionSuccessModel(
    val id: String,
    val resultCode: String,
    val timeCreated: String,
    val status: String,
    val amount: String,
    val payerDetails: String = ""
) : Parcelable

fun Transaction.asInternalModel() = TransactionSuccessModel(
    id = transactionId,
    resultCode = responseCode,
    timeCreated = timestamp,
    status = responseMessage,
    amount = balanceAmount?.toString() ?: "",
    payerDetails = payerDetails?.prettyPrint() ?: ""
)

private fun PayerDetails.prettyPrint() = buildString {
    if (firstName.isNotBlank()) append("$firstName ")
    if (lastName.isNotBlank()) append("$lastName ")
    if (email.isNotBlank()) append("/ $email")
}

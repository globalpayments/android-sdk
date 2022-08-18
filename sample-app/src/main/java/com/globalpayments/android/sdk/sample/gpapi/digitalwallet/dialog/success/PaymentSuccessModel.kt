package com.globalpayments.android.sdk.sample.gpapi.digitalwallet.dialog.success

import android.os.Parcel
import android.os.Parcelable

data class PaymentSuccessModel(
    val id: String,
    val resultCode: String,
    val timeCreated: String,
    val status: String,
    val amount: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(resultCode)
        parcel.writeString(timeCreated)
        parcel.writeString(status)
        parcel.writeString(amount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentSuccessModel> {
        override fun createFromParcel(parcel: Parcel): PaymentSuccessModel {
            return PaymentSuccessModel(parcel)
        }

        override fun newArray(size: Int): Array<PaymentSuccessModel?> {
            return arrayOfNulls(size)
        }
    }

}

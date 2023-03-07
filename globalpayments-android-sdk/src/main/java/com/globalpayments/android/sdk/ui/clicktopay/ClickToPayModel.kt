package com.globalpayments.android.sdk.ui.clicktopay

import android.os.Parcel
import android.os.Parcelable

data class ClickToPayModel(
    val amount: String,
    val accessKey: String,
    val domain: String = "example.com",
    val pathHandler: String = "/assets/",
    val htmlLocalAddress: String = "https://example.com/assets/ctp.html",
    val javaScriptToEvaluate: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(amount)
        parcel.writeString(accessKey)
        parcel.writeString(domain)
        parcel.writeString(pathHandler)
        parcel.writeString(htmlLocalAddress)
        parcel.writeString(javaScriptToEvaluate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClickToPayModel> {
        override fun createFromParcel(parcel: Parcel): ClickToPayModel {
            return ClickToPayModel(parcel)
        }

        override fun newArray(size: Int): Array<ClickToPayModel?> {
            return arrayOfNulls(size)
        }
    }
}

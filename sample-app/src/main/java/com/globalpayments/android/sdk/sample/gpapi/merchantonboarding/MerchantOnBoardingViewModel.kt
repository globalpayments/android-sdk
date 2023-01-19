package com.globalpayments.android.sdk.sample.gpapi.merchantonboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.global.api.entities.User
import com.global.api.services.PayFacService
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.common.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MerchantOnBoardingViewModel : BaseViewModel() {

    private val payService = PayFacService()

    val merchants: MutableLiveData<User> = MutableLiveData()

    fun searchMerchant(merchantId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val merchant = payService
                .getMerchantInfo(merchantId)
                .execute(Constants.DEFAULT_GPAPI_CONFIG)
            merchants.postValue(merchant)
        } catch (exception: Exception) {
            showError(exception)
        }
    }

}

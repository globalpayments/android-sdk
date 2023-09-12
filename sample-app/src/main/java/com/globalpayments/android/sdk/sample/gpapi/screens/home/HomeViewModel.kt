package com.globalpayments.android.sdk.sample.gpapi.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globalpayments.android.sdk.sample.gpapi.navigation.NavigationManager
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.CreateAccessTokenDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.ExpandYourIntegrationDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.ProcessAPaymentDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.ReportingMenuDirection
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    fun goToAccessToken() {
        viewModelScope.launch {
            NavigationManager.navigate(CreateAccessTokenDirection)
        }
    }

    fun goToProcessAPayment() {
        viewModelScope.launch {
            NavigationManager.navigate(ProcessAPaymentDirection)
        }
    }

    fun goToExpandIntegration() {
        viewModelScope.launch { NavigationManager.navigate(ExpandYourIntegrationDirection) }
    }

    fun goToReporting() {
        viewModelScope.launch { NavigationManager.navigate(ReportingMenuDirection) }
    }
}

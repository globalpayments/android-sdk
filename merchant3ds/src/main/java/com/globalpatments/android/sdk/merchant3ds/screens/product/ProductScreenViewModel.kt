package com.globalpatments.android.sdk.merchant3ds.screens.product

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globalpatments.android.sdk.merchant3ds.BuildConfig
import com.globalpatments.android.sdk.merchant3ds.navigation.DirectionBack
import com.globalpatments.android.sdk.merchant3ds.navigation.NavigationManager
import com.globalpatments.android.sdk.merchant3ds.networking.Merchant3DSApi
import com.globalpatments.android.sdk.merchant3ds.networking.models.request.AccessTokenRequest
import com.globalpatments.android.sdk.merchant3ds.screens.card.HostedFieldDirection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductScreenViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val merchant3DSApi: Merchant3DSApi,
) : ViewModel() {

    var state by mutableStateOf(ProductScreenModel())

    fun goBack() = viewModelScope.launch {
        navigationManager.navigate(DirectionBack)
    }

    fun onSizeSelected(size: String) {
        state = state.copy(selectedSize = size)
    }

    fun closeDialog() {
        state = state.copy(error = null)
    }

    fun getAccessToken() = viewModelScope.launch(Dispatchers.IO) {
        state = state.copy(isLoading = true)
        try {
            val token = merchant3DSApi.accessToken(
                AccessTokenRequest(
                    appId = BuildConfig.APP_ID,
                    appKey = BuildConfig.APP_KEY,
                    permissions = emptyList()
                )
            ).accessToken
            navigationManager.navigate(HostedFieldDirection(token))
        } catch (exception: Exception) {
            state = state.copy(error = exception.message)
        }
        state = state.copy(isLoading = false)
    }
}
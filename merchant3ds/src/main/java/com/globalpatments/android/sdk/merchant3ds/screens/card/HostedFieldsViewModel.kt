package com.globalpatments.android.sdk.merchant3ds.screens.card

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globalpatments.android.sdk.merchant3ds.navigation.NavigationManager
import com.globalpatments.android.sdk.merchant3ds.screens.processing.ProcessingDirection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HostedFieldsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val navigationManager: NavigationManager
) : ViewModel() {

    var state by mutableStateOf(HostedFieldsScreenModel())

    val accessToken = savedStateHandle.get<String>(HostedFieldDirection.AccessToken) ?: ""

    fun goBack() = viewModelScope.launch {
        navigationManager.navigateBack()
    }

    fun dismissError() {
        state = state.copy(error = "")
    }

    fun onCardTokenReceived(
        cardToken: String,
        cardBrandReceived: String
    ) = viewModelScope.launch {
        navigationManager.navigate(ProcessingDirection(cardToken, cardBrandReceived))
    }

    fun onLoading() {
        state = state.copy(isLoading = true)
    }

    fun onError(errorMessage: String) = viewModelScope.launch {
        state = state.copy(isLoading = false, error = errorMessage)
    }
}
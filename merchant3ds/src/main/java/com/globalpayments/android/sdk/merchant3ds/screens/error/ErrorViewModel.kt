package com.globalpayments.android.sdk.merchant3ds.screens.error

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globalpayments.android.sdk.merchant3ds.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ErrorViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : ViewModel() {

    fun navigateBack() = viewModelScope.launch {
        navigationManager.navigateBack()
    }
}

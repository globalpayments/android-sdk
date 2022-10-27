package com.globalpatments.android.sdk.merchant3ds.screens.purchase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globalpatments.android.sdk.merchant3ds.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel
@Inject constructor(
    private val navigationManager: NavigationManager
) : ViewModel() {

    fun donePressed() = viewModelScope.launch {
        navigationManager.navigateBack()
    }
}
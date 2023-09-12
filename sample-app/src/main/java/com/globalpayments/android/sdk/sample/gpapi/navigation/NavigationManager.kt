package com.globalpayments.android.sdk.sample.gpapi.navigation

import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.DirectionBack
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.NavigationDirection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

object NavigationManager {

    private val _command = MutableSharedFlow<NavigationDirection>()
    val commands: Flow<NavigationDirection>
        get() = _command

    suspend fun navigate(direction: NavigationDirection) {
        _command.emit(direction)
    }

    suspend fun navigateBack() {
        _command.emit(DirectionBack)
    }
}

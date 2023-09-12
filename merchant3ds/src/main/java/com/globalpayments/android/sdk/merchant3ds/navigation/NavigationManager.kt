package com.globalpayments.android.sdk.merchant3ds.navigation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {

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

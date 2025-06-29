package com.elearn.presentation.ui.screens.home

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface HomeEvent {
    data object CreatedClass : HomeEvent
    data object CreatedMaterial : HomeEvent
    data object JoinedClass : HomeEvent
    data object EditedMaterial : HomeEvent
}

object HomeEventBus {
    private val _events = MutableSharedFlow<HomeEvent>()
    val events = _events.asSharedFlow()

    suspend fun homeEventEmit(event: HomeEvent) {
        _events.emit(event)
    }
}
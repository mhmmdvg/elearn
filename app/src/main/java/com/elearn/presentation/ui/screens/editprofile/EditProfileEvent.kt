package com.elearn.presentation.ui.screens.editprofile

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface EditProfileEvent {
    data object UpdateUserName : EditProfileEvent
    data object UpdateDescription : EditProfileEvent
    data object UpdateProfileImage: EditProfileEvent
}

object EditProfileEventBus {
    private val _events = MutableSharedFlow<EditProfileEvent>()
    val events = _events.asSharedFlow()

    suspend fun editProfileEventEmit(event: EditProfileEvent) {
        _events.emit(event)
    }
}
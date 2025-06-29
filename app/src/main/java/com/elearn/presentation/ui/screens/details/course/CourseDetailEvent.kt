package com.elearn.presentation.ui.screens.details.course

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface CourseDetailEvent {
    data object UpdateCourse : CourseDetailEvent
}

object CourseDetailEventBus {
    private val _events = MutableSharedFlow<CourseDetailEvent>()

    val events = _events.asSharedFlow()

    suspend fun editCourseEventEmit(event: CourseDetailEvent) {
        _events.emit(event)
    }
}
package com.elearn.presentation.ui.model

import java.time.LocalDateTime

data class AttendanceFormState(
    val title: String = "",
    val description: String = "",
    val startTime: String = LocalDateTime.now().toString(),
    val endTime: String = LocalDateTime.now().plusHours(1).toString(),
    val requireLocation: Boolean = false
)
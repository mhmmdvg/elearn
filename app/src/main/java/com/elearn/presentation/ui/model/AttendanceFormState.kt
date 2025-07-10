package com.elearn.presentation.ui.model

data class AttendanceFormState(
    val title: String = "",
    val description: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val requireLocation: Boolean = false
)
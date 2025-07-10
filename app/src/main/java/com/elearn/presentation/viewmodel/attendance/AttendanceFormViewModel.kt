package com.elearn.presentation.viewmodel.attendance

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.elearn.presentation.ui.model.AttendanceFormState
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AttendanceFormViewModel @Inject constructor() : ViewModel() {
    private val _state = mutableStateOf(AttendanceFormState())
    val state: State<AttendanceFormState> = _state

    fun onTitleChanged(title: String) {
        _state.value = state.value.copy(title = title)
    }

    fun onDescriptionChanged(description: String) {
        _state.value = state.value.copy(description = description)
    }

    fun onStartTimeChanged(startTime: String) {
        _state.value = state.value.copy(startTime = startTime)
    }

    fun onEndTimeChanged(endTime: String) {
        _state.value = state.value.copy(endTime = endTime)
    }

    fun onRequireLocationChanged(requireLocation: Boolean) {
        _state.value = state.value.copy(requireLocation = requireLocation)
    }

    // Validation methods
    fun getTitleError(): String? {
        return if (state.value.title.isBlank()) {
            "Title is required"
        } else null
    }

    fun getStartTimeError(): String? {
        return if (state.value.startTime.isBlank()) {
            "Start time is required"
        } else null
    }

    fun getEndTimeError(): String? {
        return if (state.value.endTime.isBlank()) {
            "End time is required"
        } else null
    }

    fun getTimeValidationError(): String? {
        if (state.value.startTime.isNotBlank() && state.value.endTime.isNotBlank()) {
            return try {
                val startDateTime = LocalDateTime.parse(state.value.startTime)
                val endDateTime = LocalDateTime.parse(state.value.endTime)

                when {
                    endDateTime.isBefore(startDateTime) -> "End time must be after start time"
                    endDateTime.isEqual(startDateTime) -> "End time must be different from start time"
                    else -> null
                }
            } catch (e: Exception) {
                "Invalid date time format"
            }
        }
        return null
    }

    // Convert LocalDateTime to ISO format for backend
    fun formatForBackend(localDateTime: String): String? {
        return try {
            val dateTime = LocalDateTime.parse(localDateTime)
            val zonedDateTime = dateTime.atZone(ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ISO_INSTANT
            zonedDateTime.toInstant().let { formatter.format(it) }
        } catch (e: Exception) {
            null
        }
    }

    // Get formatted times for backend
    fun getFormattedStartTime(): String? {
        return if (state.value.startTime.isNotBlank()) {
            formatForBackend(state.value.startTime)
        } else null
    }

    fun getFormattedEndTime(): String? {
        return if (state.value.endTime.isNotBlank()) {
            formatForBackend(state.value.endTime)
        } else null
    }

    fun resetState() {
        _state.value = AttendanceFormState()
    }
}
package com.elearn.presentation.viewmodel.attendance

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.elearn.presentation.ui.model.AttendanceFormState
import java.time.LocalDateTime
import javax.inject.Inject

class AttendanceFormViewModel @Inject constructor() : ViewModel() {
    private val _state = mutableStateOf(AttendanceFormState())
    val state: State<AttendanceFormState> = _state

    fun onTitleChanged(query: String) {
        _state.value = state.value.copy(title = query)
    }

    fun onDescriptionChanged(query: String) {
        _state.value = state.value.copy(description = query)
    }

    fun onStartTimeChanged(query: String) {
        _state.value = state.value.copy(startTime = query)
    }

    fun onEndTimeChanged(query: String) {
        _state.value = state.value.copy(endTime = query)
    }

    fun onRequireLocationChanged(query: Boolean) {
        _state.value = state.value.copy(requireLocation = query)
    }

    fun resetState() {
        _state.value = AttendanceFormState()
    }
}
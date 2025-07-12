package com.elearn.presentation.ui.screens.attendancesession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elearn.data.remote.repository.AttendanceRepository
import com.elearn.domain.model.AttendanceSessionDetailRes
import com.elearn.domain.model.HTTPResponse
import com.elearn.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceSessionDetailViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {
    private val _attendanceDetails =
        MutableStateFlow<Resource<HTTPResponse<AttendanceSessionDetailRes>>>(
            Resource.Success(null)
        )
    val attendanceDetails: StateFlow<Resource<HTTPResponse<AttendanceSessionDetailRes>>> =
        _attendanceDetails.asStateFlow()

    fun fetchAttendanceSessionDetail(
        classId: String,
        sessionId: String,
        forceRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            _attendanceDetails.value = Resource.Loading()

            try {
                if (forceRefresh) attendanceRepository.invalidateAttendanceSessionDetailCache(
                    sessionId
                )

                attendanceRepository.fetchAttendanceSessionDetail(classId, sessionId).fold(
                    onSuccess = { success ->
                        _attendanceDetails.value = Resource.Success(success)
                    },
                    onFailure = { error ->
                        _attendanceDetails.value = Resource.Error(
                            message = error.message ?: "Unknown error",
                            data = _attendanceDetails.value.data
                        )
                    }
                )
            } catch (error: Exception) {
                _attendanceDetails.value = Resource.Error(
                    message = error.message ?: "Unknown error",
                    data = _attendanceDetails.value.data
                )
            }
        }
    }
}
package com.elearn.presentation.viewmodel.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elearn.data.remote.repository.AttendanceRepository
import com.elearn.domain.model.AttendanceCheckinData
import com.elearn.domain.model.AttendanceCheckinReq
import com.elearn.domain.model.AttendanceSessionsData
import com.elearn.domain.model.AttendanceSessionsReq
import com.elearn.domain.model.HTTPResponse
import com.elearn.presentation.ui.screens.details.course.CourseDetailEvent
import com.elearn.presentation.ui.screens.details.course.CourseDetailEventBus
import com.elearn.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {
    private val _attendanceSessions =
        MutableStateFlow<Resource<HTTPResponse<List<AttendanceSessionsData>>>>(Resource.Success(null))
    val attendanceSessions: StateFlow<Resource<HTTPResponse<List<AttendanceSessionsData>>>> =
        _attendanceSessions.asStateFlow()

    private val _attendanceSessionCreated =
        MutableStateFlow<Resource<HTTPResponse<AttendanceSessionsData>>>(Resource.Success(null))
    val attendanceSessionCreated: StateFlow<Resource<HTTPResponse<AttendanceSessionsData>>> =
        _attendanceSessionCreated.asStateFlow()

    private val _attendanceCheckinCreated =
        MutableStateFlow<Resource<HTTPResponse<AttendanceCheckinData>>>(Resource.Success(null))
    val attendanceCheckinCreated: StateFlow<Resource<HTTPResponse<AttendanceCheckinData>>> =
        _attendanceCheckinCreated.asStateFlow()

    fun fetchAttendanceSessions(classId: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _attendanceSessions.value = Resource.Loading()

            try {
                if (forceRefresh) attendanceRepository.invalidateAttendanceSessionCache(classId)

                attendanceRepository.fetchAttendanceSessions(classId).fold(
                    onSuccess = {
                        _attendanceSessions.value = Resource.Success(it)
                    },
                    onFailure = { fail ->
                        _attendanceSessions.value = Resource.Error(
                            message = fail.message ?: "Unknown Error",
                            data = _attendanceSessions.value.data
                        )
                    }
                )
            } catch (error: Exception) {
                _attendanceSessions.value = Resource.Error(error.message ?: "Unknown Error")
            }
        }
    }

    fun createAttendanceSession(payload: AttendanceSessionsReq) {
        viewModelScope.launch {
            _attendanceSessionCreated.value = Resource.Loading()

            try {
                attendanceRepository.createAttendanceSession(payload).fold(
                    onSuccess = { success ->
                        _attendanceSessionCreated.value = Resource.Success(success)
                        attendanceRepository.invalidateAttendanceSessionCache(payload.classId)
                        CourseDetailEventBus.editCourseEventEmit(CourseDetailEvent.CreateAttendanceSession)
                        _attendanceSessionCreated.value = Resource.Success(null)
                    },
                    onFailure = { fail ->
                        _attendanceSessionCreated.value =
                            Resource.Error(fail.message ?: "Create attendance session failed", _attendanceSessionCreated.value.data)
                    }
                )
            } catch (error: Exception) {
                _attendanceSessionCreated.value =
                    Resource.Error(error.message ?: "Create attendance session failed")
            }
        }
    }

    fun createCheckinAttendance(payload: AttendanceCheckinReq) {
        viewModelScope.launch {
            _attendanceCheckinCreated.value = Resource.Loading()

            try {
                attendanceRepository.createAttendanceCheckin(payload).fold(
                    onSuccess = { success ->
                        _attendanceCheckinCreated.value = Resource.Success(success)
                        CourseDetailEventBus.editCourseEventEmit(CourseDetailEvent.CreateCheckinAttendance)
                        _attendanceCheckinCreated.value = Resource.Success(null)
                    },
                    onFailure = { fail ->
                        _attendanceCheckinCreated.value =
                            Resource.Error(fail.message ?: "Create attendance checkin failed", attendanceCheckinCreated.value.data)
                    }
                )
            } catch (error: Exception) {
                _attendanceCheckinCreated.value =
                    Resource.Error(error.message ?: "Create attendance checkin failed")
            }
        }
    }

}
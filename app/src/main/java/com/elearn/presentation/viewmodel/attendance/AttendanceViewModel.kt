package com.elearn.presentation.viewmodel.attendance

import android.content.Context
import android.location.Location
import android.util.Log
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
import com.elearn.utils.LocationHelper
import com.elearn.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val locationHelper = LocationHelper(context)

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

    private val _currentSession = MutableStateFlow<AttendanceSessionsData?>(null)
    val currentSession: StateFlow<AttendanceSessionsData?> = _currentSession.asStateFlow()

    private val _locationState = MutableStateFlow<Resource<Location>>(Resource.Success(null))
    val locationState: StateFlow<Resource<Location>> = _locationState.asStateFlow()

    private var cachedLocation: Location? = null
    private var locationFetchTime: Long = 0L
    private val locationCacheTimeout = 5 * 60 * 1000L

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

    fun getCurrentLocation() {
        viewModelScope.launch {

            val currentTime = System.currentTimeMillis()
            if (cachedLocation != null && (currentTime - locationFetchTime) < locationCacheTimeout) {
                _locationState.value = Resource.Success(cachedLocation)
                return@launch
            }

            _locationState.value = Resource.Loading()

            try {
                val res = locationHelper.getCurrentLocation()
                res.fold(
                    onSuccess = {
                        cachedLocation = it
                        locationFetchTime = currentTime
                        _locationState.value = Resource.Success(it)
                    },
                    onFailure = {
                        _locationState.value = Resource.Error(it.message ?: "Failed to get location")
                    }
                )
            } catch (error: Exception) {
                _locationState.value = Resource.Error(
                    message = error.message ?: "Failed to get location"
                )
            }
        }
    }

    fun createCheckinAttendance(sessionId: String, sessionsData: AttendanceSessionsData, notes: String? = null) {
        viewModelScope.launch {
            _currentSession.value = sessionsData
            _attendanceCheckinCreated.value = Resource.Loading()

            try {
                attendanceRepository.createAttendanceCheckin(
                    payload = locationState.value.let {
                        val location = it.data
                        AttendanceCheckinReq(
                            sessionId = sessionId,
                            latitude = location?.latitude ?: 0.0,
                            longitude = location?.longitude ?: 0.0,
                            accuracy = 0f,
                            notes = notes
                        )
                    }
                ).fold(
                    onSuccess = { success ->
                        _attendanceCheckinCreated.value = Resource.Success(success)
                        attendanceRepository.invalidateAllAttendanceSessionCache()
                        CourseDetailEventBus.editCourseEventEmit(CourseDetailEvent.CreateCheckinAttendance)
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

    fun clearCheckinState() {
        _attendanceCheckinCreated.value = Resource.Success(null)
    }


    fun clearLocationCache() {
        cachedLocation = null
        locationFetchTime = 0L
        _locationState.value = Resource.Success(null)
    }

}
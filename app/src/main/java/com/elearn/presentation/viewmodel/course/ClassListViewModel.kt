package com.elearn.presentation.viewmodel.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elearn.data.remote.local.TokenManager
import com.elearn.data.remote.repository.CourseRepository
import com.elearn.domain.model.CourseJoinResponse
import com.elearn.domain.model.CourseResponse
import com.elearn.domain.model.CreateCourseRequest
import com.elearn.domain.model.CreateCourseResponse
import com.elearn.presentation.ui.screens.home.HomeEvent
import com.elearn.presentation.ui.screens.home.HomeEventBus
import com.elearn.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassListViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {
    private val _classes = MutableStateFlow<Resource<CourseResponse>>(Resource.Success(null))
    val classes: StateFlow<Resource<CourseResponse>> = _classes

    private val _createClass =
        MutableStateFlow<Resource<CreateCourseResponse>>(Resource.Success(null))
    val createClass: StateFlow<Resource<CreateCourseResponse>> = _createClass

    private val _joinClass = MutableStateFlow<Resource<CourseJoinResponse>>(Resource.Success(null))
    val joinClass: StateFlow<Resource<CourseJoinResponse>> = _joinClass

    init {
        fetchClasses()

        viewModelScope.launch {
            HomeEventBus.events.collectLatest {
                when (it) {
                    is HomeEvent.CreatedClass -> fetchClasses()
                    is HomeEvent.JoinedClass -> fetchClasses()
                    is HomeEvent.NavigateDetail -> {}
                }
            }
        }
    }

    fun getToken(): String? {
        return tokenManager.getToken()
    }

    fun fetchClasses() {
        courseRepository.getCacheCourseList()?.let {
            _classes.value = Resource.Success(it)
            return
        }

        viewModelScope.launch {
            _classes.value = Resource.Loading()

            try {
                courseRepository.fetchCourse().fold(
                    onSuccess = {
                        _classes.value = Resource.Success(it)
                    },
                    onFailure = {
                        _classes.value = Resource.Error(
                            message = it.message ?: "Unknown Error",
                            data = _classes.value.data
                        )
                    }
                )
            } catch (error: Exception) {
                _classes.value = Resource.Error(error.message ?: "Unknown Error")
            }
        }
    }

    fun createClass(payload: CreateCourseRequest) {
        viewModelScope.launch {
            _createClass.value = Resource.Loading()
            try {
                courseRepository.createCourse(payload).fold(
                    onSuccess = {
                        courseRepository.invalidateCourseCache()
                        _createClass.value = Resource.Success(it)
                        HomeEventBus.homeEventEmit(HomeEvent.CreatedClass)
                        delay(300)
                        _createClass.value = Resource.Success(null)
                    },
                    onFailure = {
                        _createClass.value = Resource.Error(it.message ?: "Unknown Error")
                    }
                )
            } catch (error: Exception) {
                _createClass.value = Resource.Error(error.message ?: "Unknown Error")
            }
        }
    }

    fun resetJoinClassState() {
        _joinClass.value = Resource.Success(null)
    }

    fun joinClass(code: String) {
        viewModelScope.launch {
            _joinClass.value = Resource.Loading()

            try {
                courseRepository.postJoinCourse(code).fold(
                    onSuccess = {
                        courseRepository.invalidateCourseCache()
                        _joinClass.value = Resource.Success(it)
                        HomeEventBus.homeEventEmit(HomeEvent.JoinedClass)
                    },
                    onFailure = {
                        _joinClass.value = Resource.Error(it.message ?: "Unknown Error")
                    }
                )
            } catch (error: Exception) {
                _joinClass.value = Resource.Error(error.message ?: "Unknown Error")
            }
        }
    }

}
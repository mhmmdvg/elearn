package com.elearn.presentation.viewmodel.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elearn.data.remote.local.TokenManager
import com.elearn.data.remote.repository.CourseRepository
import com.elearn.domain.model.CourseJoinResponse
import com.elearn.domain.model.CourseResponse
import com.elearn.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassListViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _classes = MutableStateFlow<Resource<CourseResponse>>(Resource.Success(null))
    val classes: StateFlow<Resource<CourseResponse>> = _classes

    private val _joinClass = MutableStateFlow<Resource<CourseJoinResponse>>(Resource.Success(null))
    val joinClass: StateFlow<Resource<CourseJoinResponse>> = _joinClass

    init {
        fetchClasses()
    }

    fun getToken(): String? {
        return tokenManager.getToken()
    }

    fun fetchClasses() {
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

    fun joinClass(code: String) {
        viewModelScope.launch {
            _joinClass.value = Resource.Loading()

            try {
                courseRepository.postJoinCourse(code).fold(
                    onSuccess = {
                        _joinClass.value = Resource.Success(it)
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
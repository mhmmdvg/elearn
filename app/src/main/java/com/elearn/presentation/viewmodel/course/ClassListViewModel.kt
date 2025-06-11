package com.elearn.presentation.viewmodel.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elearn.data.remote.repository.CourseRepository
import com.elearn.domain.model.CourseResponse
import com.elearn.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassListViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {
    private val _classes = MutableStateFlow<Resource<CourseResponse>>(Resource.Success(null))
    val classes: StateFlow<Resource<CourseResponse>> = _classes

    init {
        fetchClasses()
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

}
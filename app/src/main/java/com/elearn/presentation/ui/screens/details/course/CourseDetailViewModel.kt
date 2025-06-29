package com.elearn.presentation.ui.screens.details.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elearn.data.remote.repository.CourseRepository
import com.elearn.data.remote.repository.MaterialRepository
import com.elearn.domain.model.CourseData
import com.elearn.domain.model.CourseResponse
import com.elearn.domain.model.CreateCourseRequest
import com.elearn.domain.model.MaterialResponse
import com.elearn.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val materialRepository: MaterialRepository
) : ViewModel() {
    private val _materialClassState =
        MutableStateFlow<Resource<MaterialResponse>>(Resource.Success(null))
    val materialClassState: StateFlow<Resource<MaterialResponse>> = _materialClassState.asStateFlow()

    val _courseNameUpdated = MutableStateFlow<Resource<Any>>(Resource.Success(null))
    val courseNameUpdated: StateFlow<Resource<Any>> = _courseNameUpdated.asStateFlow()

    val _courseDescriptionUpdated = MutableStateFlow<Resource<Any>>(Resource.Success(null))
    val courseDescriptionUpdated: StateFlow<Resource<Any>> = _courseDescriptionUpdated.asStateFlow()

    private val _courseDetailState =
        MutableStateFlow<Resource<CourseResponse<CourseData>>>(Resource.Success(null))
    val courseDetailState: StateFlow<Resource<CourseResponse<CourseData>>> = _courseDetailState.asStateFlow()

    fun fetchMaterialByClass(id: String) {
        viewModelScope.launch {
            _materialClassState.value = Resource.Loading()

            try {
                materialRepository.fetchMaterial(id).fold(
                    onSuccess = {
                        materialRepository.invalidateMaterialCache()
                        _materialClassState.value = Resource.Success(it)
                    },
                    onFailure = {
                        _materialClassState.value =
                            Resource.Error(it.message ?: "Fetch material failed")
                    }
                )
            } catch (error: Exception) {
                _materialClassState.value = Resource.Error(error.message ?: "Unknown Error")
            }
        }
    }

    fun fetchCourseDetail(id: String) {
        viewModelScope.launch {
            _courseDetailState.value = Resource.Loading()

            try {
                courseRepository.fetchCourseDetail(id).fold(
                    onSuccess = {
                        _courseDetailState.value = Resource.Success(it)
                    },
                    onFailure = {
                        _courseDetailState.value =
                            Resource.Error(it.message ?: "Fetch course detail failed")
                    }
                )
            } catch (error: Exception) {
                _courseDetailState.value = Resource.Error(error.message ?: "Unknown Error")
            }
        }
    }

    fun updateCourseName(id: String, name: String) {
        viewModelScope.launch {
            _courseNameUpdated.value = Resource.Loading()

           try {
               courseRepository.updateCourse(id, CreateCourseRequest(name = name)).fold(
                   onSuccess = {
                       _courseNameUpdated.value = Resource.Success(it)
                       CourseDetailEventBus.editCourseEventEmit(CourseDetailEvent.UpdateCourse)
                       _courseNameUpdated.value = Resource.Success(null)
                   },
                   onFailure = {
                       _courseNameUpdated.value =
                           Resource.Error(it.message ?: "Update course name failed")
                   }
               )
           } catch (error: Exception) {
               _courseNameUpdated.value = Resource.Error(error.message ?: "Unknown Error")
           }
        }
    }

    fun updateCourseDescription(id: String, description: String) {
        viewModelScope.launch {
            _courseDescriptionUpdated.value = Resource.Loading()

            try {
                courseRepository.updateCourse(id, CreateCourseRequest(description = description)).fold(
                    onSuccess = {
                        _courseDescriptionUpdated.value = Resource.Success(it)
                        CourseDetailEventBus.editCourseEventEmit(CourseDetailEvent.UpdateCourse)
                        delay(300)
                        _courseDescriptionUpdated.value = Resource.Success(null)
                    },
                    onFailure = {
                        _courseDescriptionUpdated.value =
                            Resource.Error(it.message ?: "Update course name failed")
                    }
                )
            } catch (error: Exception) {
                _courseDescriptionUpdated.value = Resource.Error(error.message ?: "Unknown Error")
            }
        }
    }
}
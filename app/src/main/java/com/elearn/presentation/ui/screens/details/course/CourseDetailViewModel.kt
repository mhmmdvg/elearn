package com.elearn.presentation.ui.screens.details.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elearn.data.remote.repository.MaterialRepository
import com.elearn.domain.model.MaterialResponse
import com.elearn.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val materialRepository: MaterialRepository
) : ViewModel() {
    private val _materialClassState = MutableStateFlow<Resource<MaterialResponse>>(Resource.Success(null))
    val materialClassState: StateFlow<Resource<MaterialResponse>> = _materialClassState

    fun fetchCourseByClass(id: String) {
        viewModelScope.launch {
            _materialClassState.value = Resource.Loading()

            try {
                materialRepository.fetchMaterial(id).fold(
                    onSuccess = {
                        materialRepository.invalidateMaterialCache()
                        _materialClassState.value = Resource.Success(it)
                    },
                    onFailure = {
                        _materialClassState.value = Resource.Error(it.message ?: "Fetch material failed")
                    }
                )
            } catch (error: Exception) {
                _materialClassState.value = Resource.Error(error.message ?: "Unknown Error")
            }
        }
    }
}
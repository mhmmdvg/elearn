package com.elearn.presentation.ui.screens.details.material

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elearn.data.remote.repository.MaterialRepository
import com.elearn.domain.model.HTTPResponse
import com.elearn.domain.model.MaterialData
import com.elearn.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaterialDetailViewModel @Inject constructor(
    private val materialRepository: MaterialRepository
) : ViewModel() {
    private val _materialDetailState = MutableStateFlow<Resource< HTTPResponse<MaterialData>>>(Resource.Success(null))
    val materialDetailState: StateFlow<Resource<HTTPResponse<MaterialData>>> = _materialDetailState.asStateFlow()

    fun fetchMaterialDetail(id: String) {
        viewModelScope.launch {
            _materialDetailState.value = Resource.Loading()

            try {
                materialRepository.fetchMaterialDetail(id).fold(
                    onSuccess = {
                        _materialDetailState.value = Resource.Success(it)
                    },
                    onFailure = {
                        _materialDetailState.value = Resource.Error(it.message ?: "Fetch material detail failed")
                    }
                )
            } catch (error: Exception) {
                _materialDetailState.value = Resource.Error(error.message ?: "Unknown Error")
            }
        }
    }

}
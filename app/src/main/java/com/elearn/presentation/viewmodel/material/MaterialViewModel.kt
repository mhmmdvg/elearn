package com.elearn.presentation.viewmodel.material

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elearn.data.remote.repository.MaterialRepository
import com.elearn.domain.model.MaterialResponse
import com.elearn.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaterialViewModel @Inject constructor(
    private val materialRepository: MaterialRepository,
) : ViewModel() {
    private val _materials = MutableStateFlow<Resource<MaterialResponse>>(Resource.Success(null))
    val materials: StateFlow<Resource<MaterialResponse>> = _materials


    init {
        fetchMaterials()

    }

    fun fetchMaterials() {
        materialRepository.getCacheMaterialList()?.let {
            _materials.value = Resource.Success(it)
            return
        }

        viewModelScope.launch {
            _materials.value = Resource.Loading()

            try {
                materialRepository.fetchMaterial().fold(
                    onSuccess = {
                        _materials.value = Resource.Success(it)
                        Log.d("checkit", it.toString())
                    },
                    onFailure = {
                        _materials.value = Resource.Error(
                            message = it.message ?: "Unknown Error",
                            data = _materials.value.data
                        )
                    }
                )
            } catch (error: Exception) {
                _materials.value = Resource.Error(error.message ?: "Unknown Error")
            }
        }
    }
}
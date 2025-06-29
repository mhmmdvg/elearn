package com.elearn.presentation.viewmodel.material

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elearn.data.remote.repository.MaterialRepository
import com.elearn.domain.model.CreateMaterialResponse
import com.elearn.domain.model.HTTPResponse
import com.elearn.domain.model.MaterialData
import com.elearn.domain.model.MaterialResponse
import com.elearn.presentation.ui.screens.home.HomeEvent
import com.elearn.presentation.ui.screens.home.HomeEventBus
import com.elearn.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaterialViewModel @Inject constructor(
    private val materialRepository: MaterialRepository,
) : ViewModel() {
    private val _materials = MutableStateFlow<Resource<MaterialResponse>>(Resource.Success(null))
    val materials: StateFlow<Resource<MaterialResponse>> = _materials.asStateFlow()

    private val _createMaterialState = MutableStateFlow<Resource<CreateMaterialResponse>>(Resource.Success(null))
    val createMaterialState: StateFlow<Resource<CreateMaterialResponse>> = _createMaterialState.asStateFlow()

    private val _editMaterialState = MutableStateFlow<Resource<CreateMaterialResponse>>(Resource.Success(null))
    val editMaterialState: StateFlow<Resource<CreateMaterialResponse>> = _editMaterialState.asStateFlow()

    private val _materialDetailState = MutableStateFlow<Resource<HTTPResponse<MaterialData>>>(Resource.Success(null))
    val materialDetailState: StateFlow<Resource<HTTPResponse<MaterialData>>> = _materialDetailState.asStateFlow()

    init {
        fetchMaterials()

        viewModelScope.launch {
            HomeEventBus.events.collectLatest {
                when (it) {
                    is HomeEvent.CreatedMaterial -> fetchMaterials()
                    else -> {}
                }
            }
        }
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

    fun refreshMaterials() {

        viewModelScope.launch {
            _materials.value = Resource.Loading()

            try {
                materialRepository.fetchMaterial().fold(
                    onSuccess = {
                        _materials.value = Resource.Success(it)
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

    fun createMaterial(
        context: Context,
        fileUri: Uri,
        name: String,
        description: String?,
        classId: String
    ) {
        viewModelScope.launch {
            _createMaterialState.value = Resource.Loading()

            try {
                materialRepository.createMaterial(
                    context = context,
                    fileUri = fileUri,
                    name = name,
                    description = description,
                    classId = classId
                ).fold(
                    onSuccess = {
                        _createMaterialState.value = Resource.Success(it)
                        HomeEventBus.homeEventEmit(HomeEvent.CreatedMaterial)
                        delay(300)
                        _createMaterialState.value = Resource.Success(null)
                    },
                    onFailure = {
                        _createMaterialState.value = Resource.Error(
                            message = it.message ?: "Failed to create material",
                            data = null
                        )
                    }
                )
            } catch (error: Exception) {
                _createMaterialState.value = Resource.Error(
                    message = error.message ?: "Unknown error occurred",
                    data = null
                )
            }
        }
    }

    fun putMaterial(
        materialId: String,
        context: Context,
        fileUri: Uri,
        name: String,
        description: String?,
    ) {
        viewModelScope.launch {
            _editMaterialState.value = Resource.Loading()

            try {
                materialRepository.putMaterial(
                    materialId = materialId,
                    context = context,
                    fileUri = fileUri,
                    name = name,
                    description = description,
                ).fold(
                    onSuccess = {
                        _editMaterialState.value = Resource.Success(it)
                        HomeEventBus.homeEventEmit(HomeEvent.EditedMaterial)
                        delay(300)
                        _editMaterialState.value = Resource.Success(null)
                    },
                    onFailure = {
                        _editMaterialState.value = Resource.Error(
                            message = it.message ?: "Failed to create material",
                            data = null
                        )
                    }
                )
            } catch (error: Exception) {
                _editMaterialState.value = Resource.Error(
                    message = error.message ?: "Unknown error occurred",
                    data = null
                )
            }
        }
    }
}
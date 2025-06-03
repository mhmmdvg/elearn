package com.elearn.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.elearn.presentation.ui.model.MaterialFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MaterialFormViewModel @Inject constructor() : ViewModel() {
    private val _state = mutableStateOf(MaterialFormState())
    val state: State<MaterialFormState> = _state

    fun onClassNameChanged(query: String) {
        _state.value = _state.value.copy(className = query)
    }

    fun onMaterialNameChanged(query: String) {
        _state.value = _state.value.copy(materialName =  query)
    }

    fun onDescriptionChanged(query: String) {
        _state.value = _state.value.copy(description = query)
    }

    fun onSelectedFileChanged(query: String) {
        _state.value = _state.value.copy(selectedFileName = query)
    }
}
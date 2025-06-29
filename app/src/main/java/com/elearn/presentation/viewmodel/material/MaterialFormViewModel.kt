package com.elearn.presentation.viewmodel.material

import android.net.Uri
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

    fun getMaterialNameError(): String? {
        val materialName = _state.value.materialName
        return when {
            materialName.isBlank() -> "Material name is required"
            materialName.length < 3 -> "Material name must be at least 3 characters"
            materialName.length > 100 -> "Material name must be less than 100 characters"
            !materialName.matches(Regex("^[a-zA-Z0-9\\s\\-_.,()]+$")) -> "Material name contains invalid characters"
            else -> null
        }
    }

    fun getDescriptionError(): String? {
        val description = _state.value.description
        return when {
            description.isBlank() -> "Description is required"
            description.length < 10 -> "Description must be at least 10 characters"
            description.length > 500 -> "Description must be less than 500 characters"
            else -> null
        }
    }

    fun getClassError(): String? {
        return when {
            _state.value.selectedClass.isNullOrBlank() -> "Please select a class"
            _state.value.selectedClassId.isNullOrBlank() -> "Invalid class selection"
            else -> null
        }
    }

    fun getFileError(): String? {
        return when {
            _state.value.selectedFileUri == null -> "Please select a file"
            _state.value.selectedFileName.isBlank() -> "Invalid file selection"
            else -> null
        }
    }

    fun isFormValid(): Boolean {
        return getMaterialNameError() == null &&
                getDescriptionError() == null &&
                getClassError() == null &&
                getFileError() == null
    }

    fun onClassChanged(selectedClass: String?, classId: String?) {
        _state.value = _state.value.copy(
            selectedClass = selectedClass,
            selectedClassId = classId
        )
    }

    fun onMaterialNameChanged(query: String) {
        _state.value = _state.value.copy(materialName =  query)
    }

    fun onDescriptionChanged(query: String) {
        _state.value = _state.value.copy(description = query)
    }

    fun onSelectedFileChanged(uri: Uri?, fileName: String) {
        _state.value = _state.value.copy(
            selectedFileUri = uri,
            selectedFileName = fileName
        )
    }

    fun resetState() {
        _state.value = MaterialFormState()
    }
}
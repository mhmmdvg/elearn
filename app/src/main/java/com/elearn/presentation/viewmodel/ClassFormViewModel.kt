package com.elearn.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.elearn.presentation.ui.model.ClassFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ClassFormViewModel @Inject constructor() : ViewModel() {
    private val _state = mutableStateOf(ClassFormState())
    val state: State<ClassFormState> = _state

    fun onClassNameChanged(query: String) {
        _state.value = state.value.copy(className = query)
    }

    fun onDescriptionChanged(query: String) {
        _state.value = state.value.copy(description = query)
    }
}
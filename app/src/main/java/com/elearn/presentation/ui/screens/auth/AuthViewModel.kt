package com.elearn.presentation.ui.screens.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elearn.data.remote.repository.AuthRepository
import com.elearn.domain.model.LoginResponse
import com.elearn.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = mutableStateOf(AuthState())
    val state: State<AuthState> = _state

    private val _authState = MutableStateFlow<Resource<LoginResponse>>(Resource.Success(null))
    val authState: StateFlow<Resource<LoginResponse>> = _authState.asStateFlow()


    fun onEmailChanged(email: String) {
        _state.value = _state.value.copy(email = email, error = null)
    }

    fun checkEmail() {
        val email = _state.value.email.trim()

        if (!isValidEmail(email)) {
            _state.value = _state.value.copy(error = "Please enter a valid email address")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                authRepository.emailCheck(email).fold(
                    onSuccess = {
                        _state.value = _state.value.copy(isLoading = false, isEmailValid = true)
                        _authState.value = Resource.Success(it)
                    },
                    onFailure = {
                        _state.value = _state.value.copy(isLoading = false, error = it.message)
                        _authState.value = Resource.Error(it.message ?: "Unknown Error")
                    }
                )
            } catch (error: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = error.message)
                _authState.value = Resource.Error(error.message ?: "Unknown Error")
            }
        }
    }

    fun onPasswordChanged(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun togglePasswordVisibility() {
        _state.value = _state.value.copy(isPasswordVisible = !_state.value.isPasswordVisible)
    }

    fun login() {
        val email = _state.value.email.trim()
        val password = _state.value.password

        if (!isValidEmail(email)) {
            _state.value = _state.value.copy(error = "Please enter a valid email address")
            return
        }

        if (password.isEmpty()) {
            _state.value = _state.value.copy(error = "Please enter a password")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                authRepository.login(email, password).fold(
                    onSuccess = {
                        _state.value = _state.value.copy(isLoading = false)
                        _authState.value = Resource.Success(it)
                    },
                    onFailure = {
                        _state.value = _state.value.copy(isLoading = false, error = it.message)
                        _authState.value = Resource.Error(it.message ?: "Unknown Error")
                    }
                )
            } catch (error: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = error.message)
                _authState.value = Resource.Error(error.message ?: "Unknown Error")
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
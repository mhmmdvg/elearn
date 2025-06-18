package com.elearn.presentation.ui.screens.auth

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elearn.data.remote.local.TokenManager
import com.elearn.data.remote.repository.AuthRepository
import com.elearn.domain.model.LoginResponse
import com.elearn.domain.model.LogoutResponse
import com.elearn.utils.JwtConvert.decodeToken
import com.elearn.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _state = mutableStateOf(AuthState())
    val state: State<AuthState> = _state

    private val _authState = MutableStateFlow<Resource<LoginResponse>>(Resource.Success(null))
    val authState: StateFlow<Resource<LoginResponse>> = _authState.asStateFlow()

    private val _authLogoutState = MutableStateFlow<Resource<LogoutResponse>>(Resource.Success(null))
    val authLogoutState: StateFlow<Resource<LogoutResponse>> = _authLogoutState.asStateFlow()


    private var _cachedUserInfo: JSONObject? = null
    private var _lastTokenHash: Int? = null

    fun getToken(): String? {
        return tokenManager.getToken()
    }

    fun getUserInfo(): JSONObject? {
        val token = getToken()
        val currentTokenHash = token.hashCode()

        if (_cachedUserInfo == null || _lastTokenHash != currentTokenHash) {
            _cachedUserInfo = token?.let { decodeToken(it) }
            _lastTokenHash = currentTokenHash
        }

        return _cachedUserInfo
    }


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

    fun logout() {
       viewModelScope.launch {
           _authLogoutState.value = Resource.Loading()

           try {
               authRepository.logout().fold(
                   onSuccess = {
                       _authLogoutState.value = Resource.Success(it)
                   },
                   onFailure = {
                       _authLogoutState.value = Resource.Error(it.message ?: "Unknown Error")
                   }
               )
           } catch (error: Exception) {
               _authLogoutState.value = Resource.Error(error.message ?: "Unknown Error")
           }
       }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
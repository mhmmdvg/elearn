package com.elearn.presentation.ui.screens.editprofile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elearn.data.remote.repository.UserRepository
import com.elearn.domain.model.UserResponse
import com.elearn.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = mutableStateOf(EditProfileState())
    val state: State<EditProfileState> = _state

    private val _userInfoState = MutableStateFlow<Resource<UserResponse>>(Resource.Success(null))
    val userInfoState: StateFlow<Resource<UserResponse>> = _userInfoState

    fun onFirstNameChanged(query: String) {
        _state.value = _state.value.copy(firstName = query)
    }

    fun onLastNameChanged(query: String) {
        _state.value = _state.value.copy(lastName = query)
    }

    fun getUserInfo(id: String) {
        userRepository.getCachedUserInfo(id)?.let {
            _userInfoState.value = Resource.Success(it)
            populateFormFields(it)
            return
        }

        viewModelScope.launch {

            if (_userInfoState.value.data == null) {
                _userInfoState.value = Resource.Loading()
            }

            try {
                userRepository.fetchUserInfo(id).fold(onSuccess = { userResponse ->
                    _userInfoState.value = Resource.Success(userResponse)
                    populateFormFields(userResponse)
                }, onFailure = { exception ->
                    _userInfoState.value = Resource.Error(
                        message = exception.message ?: "Unknown Error",
                        data = _userInfoState.value.data
                    )
                })
            } catch (error: Exception) {
                _userInfoState.value = Resource.Error(
                    message = error.message ?: "Unknown Error", data = _userInfoState.value.data
                )
            }
        }
    }

    private fun populateFormFields(userResponse: UserResponse) {
        _state.value = _state.value.copy(
            firstName = userResponse.data.firstName ?: "",
            lastName = userResponse.data.lastName ?: ""
        )
    }


}
package com.elearn.presentation.ui.screens.editprofile

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elearn.data.remote.repository.UserRepository
import com.elearn.domain.model.UserDescriptionReq
import com.elearn.domain.model.UserDescriptionRes
import com.elearn.domain.model.UserNameRequest
import com.elearn.domain.model.UserNameResponse
import com.elearn.domain.model.UserResponse
import com.elearn.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
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

    private val _updateUserName = MutableStateFlow<Resource<UserNameResponse>>(Resource.Success(null))
    val updateUserName: StateFlow<Resource<UserNameResponse>> = _updateUserName

    private val _updateDescription = MutableStateFlow<Resource<UserDescriptionRes>>(Resource.Success(null))
    val updateDescription: StateFlow<Resource<UserDescriptionRes>> = _updateDescription


    fun onFirstNameChanged(query: String) {
        _state.value = _state.value.copy(firstName = query)
    }

    fun onLastNameChanged(query: String) {
        _state.value = _state.value.copy(lastName = query)
    }

    fun onDescriptionChanged(query: String) {
        _state.value = _state.value.copy(description = query)
    }

//    fun getUserInfo(id: String) {
//        userRepository.getCachedUserInfo(id)?.let {
//            _userInfoState.value = Resource.Success(it)
//            populateFormFields(it)
//            return
//        }
//
//        viewModelScope.launch {
//
//            if (_userInfoState.value.data == null) {
//                _userInfoState.value = Resource.Loading()
//            }
//
//            try {
//                userRepository.fetchUserInfo(id).fold(onSuccess = { userResponse ->
//                    _userInfoState.value = Resource.Success(userResponse)
//                    populateFormFields(userResponse)
//                }, onFailure = { exception ->
//                    _userInfoState.value = Resource.Error(
//                        message = exception.message ?: "Unknown Error",
//                        data = _userInfoState.value.data
//                    )
//                })
//            } catch (error: Exception) {
//                _userInfoState.value = Resource.Error(
//                    message = error.message ?: "Unknown Error", data = _userInfoState.value.data
//                )
//            }
//        }
//    }

    fun updateUserName(id: String) {

        val payload = UserNameRequest(
            firstName = _state.value.firstName,
            lastName = _state.value.lastName
        )

        viewModelScope.launch {
            _updateUserName.value = Resource.Loading()

            try {
                userRepository.putUserName(id, payload).fold(
                    onSuccess = {
                        userRepository.invalidateUserCache(id)
                        _updateUserName.value = Resource.Success(it)
                        EditProfileEventBus.editProfileEventEmit(EditProfileEvent.UpdateUserName)
                        delay(300)
                        _updateUserName.value = Resource.Success(null)
                    },
                    onFailure = {
                        _updateUserName.value = Resource.Error(it.message ?: "Unknown Error")
                    }
                )
            } catch (error: Exception) {
                _updateUserName.value = Resource.Error(error.message ?: "Unknown Error")
            }
        }
    }

    fun updateUserDescription(id: String) {

        val payload = UserDescriptionReq(
            description = _state.value.description
        )

        viewModelScope.launch {
            _updateDescription.value = Resource.Loading()

            try {
                userRepository.putUserDescription(id, payload).fold(
                    onSuccess = {
                        userRepository.invalidateUserCache(id)
                        _updateDescription.value = Resource.Success(it)
                        EditProfileEventBus.editProfileEventEmit(EditProfileEvent.UpdateDescription)
                    },
                    onFailure = {
                        _updateDescription.value = Resource.Error(it.message ?: "Unknown Error")
                    }
                )
            } catch (error: Exception) {
                _updateDescription.value = Resource.Error(error.message ?: "Unknown Error")
            }
        }
    }

    fun populateFormFields(userResponse: UserResponse) {
        _state.value = _state.value.copy(
            firstName = userResponse.data.firstName ?: "",
            lastName = userResponse.data.lastName ?: "",
            description = userResponse.data.description ?: ""
        )
    }


}
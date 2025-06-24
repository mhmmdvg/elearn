package com.elearn.presentation.ui.screens.editprofile.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elearn.presentation.ui.components.CustomButton
import com.elearn.presentation.ui.screens.editprofile.EditProfileViewModel
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryColor
import androidx.compose.runtime.getValue
import com.elearn.utils.Resource

@Composable
fun EditNameForm(
    viewModel: EditProfileViewModel = hiltViewModel(),
    userId: String,
    onSuccess: () -> Unit
) {
    val state = viewModel.state.value
    val updateUserName by viewModel.updateUserName.collectAsState()

    LaunchedEffect(updateUserName) {
        if (updateUserName is Resource.Success && updateUserName.data != null) {
            onSuccess()
        }
    }

    // Validation logic
    val firstNameError = remember(state.firstName) {
        when {
            state.firstName.isBlank() -> "First name is required"
            state.firstName.length < 2 -> "First name must be at least 2 characters"
            state.firstName.length > 50 -> "First name must be less than 50 characters"
            !state.firstName.matches(Regex("^[a-zA-Z\\s]+$")) -> "First name can only contain letters and spaces"
            else -> null
        }
    }

    val lastNameError = remember(state.lastName) {
        when {
            state.lastName.isBlank() -> "Last name is required"
            state.lastName.length < 2 -> "Last name must be at least 2 characters"
            state.lastName.length > 50 -> "Last name must be less than 50 characters"
            !state.lastName.matches(Regex("^[a-zA-Z\\s]+$")) -> "Last name can only contain letters and spaces"
            else -> null
        }
    }

    val isFormValid = remember(firstNameError, lastNameError) {
        derivedStateOf {
            firstNameError == null && lastNameError == null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "First Name",
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = state.firstName,
                onValueChange = remember { { viewModel.onFirstNameChanged(it) } },
                placeholder = { Text("Enter your first name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22),
                isError = firstNameError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryColor,
                    focusedBorderColor = if (firstNameError != null) Color.Red else PrimaryColor,
                    unfocusedBorderColor = if (firstNameError != null) Color.Red else MutedColor,
                    unfocusedTextColor = PrimaryColor,
                    errorBorderColor = Color.Red,
                    errorTextColor = PrimaryColor
                )
            )
            // Error message for first name
            firstNameError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Last Name",
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = state.lastName,
                onValueChange = remember { { viewModel.onLastNameChanged(it) } },
                placeholder = { Text("Enter your last name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22),
                isError = lastNameError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryColor,
                    focusedBorderColor = if (lastNameError != null) Color.Red else PrimaryColor,
                    unfocusedBorderColor = if (lastNameError != null) Color.Red else MutedColor,
                    unfocusedTextColor = PrimaryColor,
                    errorBorderColor = Color.Red,
                    errorTextColor = PrimaryColor
                )
            )
            // Error message for last name
            lastNameError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        CustomButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Save",
            enabled = isFormValid.value && updateUserName !is Resource.Loading,
            isLoading = updateUserName is Resource.Loading,
            onClick = {
                if (isFormValid.value) {
                    viewModel.updateUserName(userId)
                }
            }
        )
    }
}
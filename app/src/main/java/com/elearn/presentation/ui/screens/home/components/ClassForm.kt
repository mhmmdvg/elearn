package com.elearn.presentation.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elearn.domain.model.CreateCourseRequest
import com.elearn.presentation.ui.components.CustomButton
import com.elearn.presentation.ui.screens.home.HomeEvent
import com.elearn.presentation.ui.screens.home.HomeEventBus
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.viewmodel.course.ClassFormViewModel
import com.elearn.presentation.viewmodel.course.ClassListViewModel
import com.elearn.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ClassForm(
    viewModel: ClassFormViewModel = hiltViewModel(),
    classListViewModel: ClassListViewModel = hiltViewModel(),
    onDismiss: () -> Unit = {}
) {

    /* Form State */
    val formState = viewModel.state.value
    val createClassState by classListViewModel.createClass.collectAsState()
    val isLoading = createClassState is Resource.Loading

    // Validation states
    var classNameError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    // Validation functions
    fun validateClassName(className: String): String? {
        return when {
            className.isBlank() -> "Class name is required"
            className.length < 3 -> "Class name must be at least 3 characters"
            className.length > 50 -> "Class name must not exceed 50 characters"
            else -> null
        }
    }

    fun validateDescription(description: String): String? {
        return when {
            description.isBlank() -> "Description is required"
            description.length < 10 -> "Description must be at least 10 characters"
            description.length > 500 -> "Description must not exceed 500 characters"
            else -> null
        }
    }

    // Real-time validation when form has been submitted at least once
    LaunchedEffect(formState.className, hasAttemptedSubmit) {
        if (hasAttemptedSubmit) {
            classNameError = validateClassName(formState.className)
        }
    }

    LaunchedEffect(formState.description, hasAttemptedSubmit) {
        if (hasAttemptedSubmit) {
            descriptionError = validateDescription(formState.description)
        }
    }

    // Form validity check
    val isFormValid by remember {
        derivedStateOf {
            validateClassName(formState.className) == null &&
                    validateDescription(formState.description) == null
        }
    }

    LaunchedEffect(Unit) {
        HomeEventBus.events.collectLatest { event ->
            when (event) {
                is HomeEvent.CreatedClass -> {
                    onDismiss()
                    viewModel.resetState()
                    // Reset validation states
                    classNameError = null
                    descriptionError = null
                    hasAttemptedSubmit = false
                }
                else -> {}
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Class Name")
            OutlinedTextField(
                value = formState.className,
                onValueChange = remember {
                    { newValue ->
                        viewModel.onClassNameChanged(newValue)
                        // Clear error when user starts typing (if they've attempted submit)
                        if (hasAttemptedSubmit) {
                            classNameError = validateClassName(newValue)
                        }
                    }
                },
                placeholder = { Text("Enter class name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryColor,
                    focusedBorderColor = if (classNameError != null) Color.Red else PrimaryColor,
                    unfocusedBorderColor = if (classNameError != null) Color.Red else MutedColor,
                ),
                isError = classNameError != null,
                supportingText = if (classNameError != null) {
                    { Text(classNameError!!, color = Color.Red) }
                } else null
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Description")
            OutlinedTextField(
                value = formState.description,
                onValueChange = remember {
                    { newValue ->
                        viewModel.onDescriptionChanged(newValue)
                        // Clear error when user starts typing (if they've attempted submit)
                        if (hasAttemptedSubmit) {
                            descriptionError = validateDescription(newValue)
                        }
                    }
                },
                placeholder = { Text("Enter description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                singleLine = false,
                shape = RoundedCornerShape(18),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryColor,
                    focusedBorderColor = if (descriptionError != null) Color.Red else PrimaryColor,
                    unfocusedBorderColor = if (descriptionError != null) Color.Red else MutedColor,
                ),
                isError = descriptionError != null,
                supportingText = if (descriptionError != null) {
                    { Text(descriptionError!!, color = Color.Red) }
                } else null
            )
        }

        CustomButton(
            modifier = Modifier.fillMaxWidth(),
            isLoading = isLoading,
            enabled = !isLoading,
            onClick = {
                hasAttemptedSubmit = true

                // Validate all fields
                val classNameValidation = validateClassName(formState.className)
                val descriptionValidation = validateDescription(formState.description)

                classNameError = classNameValidation
                descriptionError = descriptionValidation

                // Only submit if form is valid
                if (classNameValidation == null && descriptionValidation == null) {
                    classListViewModel.createClass(
                        CreateCourseRequest(
                            name = formState.className,
                            description = formState.description
                        )
                    )
                }
            },
            text = "Save"
        )
    }
}
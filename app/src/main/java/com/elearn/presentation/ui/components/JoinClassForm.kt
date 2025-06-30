package com.elearn.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.viewmodel.course.ClassListViewModel

@Composable
fun JoinClassForm(
    viewModel: ClassListViewModel = hiltViewModel(),
    isLoading: Boolean = false,
    onResetState: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Validate code format
    val isValidCode by remember {
        derivedStateOf {
            code.length == 6 && code.all { it.isLetterOrDigit() }
        }
    }

    // Check if form is valid
    val isFormValid by remember {
        derivedStateOf {
            isValidCode && code.isNotBlank()
        }
    }

    LaunchedEffect(onResetState) {
        code = ""
        errorMessage = ""
    }

    // Validate code on change
    LaunchedEffect(code) {
        errorMessage = when {
            code.isEmpty() -> ""
            code.length > 6 -> "Class code must be exactly 6 characters"
            code.length < 6 -> "Class code must be exactly 6 characters"
            !code.all { it.isLetterOrDigit() } -> "Class code must contain only letters and numbers"
            else -> ""
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Join Class",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Class Code")
            OutlinedTextField(
                value = code,
                onValueChange = { newValue ->
                    // Only allow alphanumeric characters and limit to 6 characters
                    if (newValue.length <= 6 && newValue.all { it.isLetterOrDigit() }) {
                        code = newValue.uppercase() // Convert to uppercase for consistency
                    }
                },
                placeholder = { Text("Enter 6-digit class code") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (errorMessage.isNotEmpty()) Color.Red else PrimaryColor,
                    unfocusedBorderColor = if (errorMessage.isNotEmpty()) Color.Red else MutedColor,
                    errorBorderColor = Color.Red
                ),
                isError = errorMessage.isNotEmpty(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Characters
                ),
                supportingText = {
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    } else {
                        Text(
                            text = "${code.length}/6 characters",
                            color = MutedColor,
                            fontSize = 12.sp
                        )
                    }
                }
            )
        }

        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            isLoading = isLoading,
            enabled = isFormValid && !isLoading,
            onClick = {
                if (isFormValid) {
                    viewModel.joinClass(code)
                }
            },
            text = "Join"
        )
    }
}
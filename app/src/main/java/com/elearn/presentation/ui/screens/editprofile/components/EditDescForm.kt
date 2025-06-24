package com.elearn.presentation.ui.screens.editprofile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elearn.presentation.ui.components.CustomButton
import com.elearn.presentation.ui.screens.editprofile.EditProfileViewModel
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryColor
import androidx.compose.runtime.getValue
import com.elearn.utils.Resource

@Composable
fun EditDescForm(
    viewModel: EditProfileViewModel = hiltViewModel(),
    userId: String?,
    onSuccess: () -> Unit
) {
    /* Form State */
    val state = viewModel.state.value
    val updateDescState by viewModel.updateDescription.collectAsState()

    LaunchedEffect(updateDescState) {
        if (updateDescState is Resource.Success && updateDescState.data != null) {
            onSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {

        Column {

            Text(
                text = "Description",
                style = MaterialTheme.typography.labelMedium,
                color = PrimaryColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = remember { { viewModel.onDescriptionChanged(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text(
                        text = "Tell us about yourself...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                maxLines = 5,
                singleLine = false,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryColor,
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = MutedColor,
                    unfocusedTextColor = PrimaryColor
                )
            )

            Text(
                text = "${state.description.length}/500",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

        }

        CustomButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = updateDescState !is Resource.Loading,
            isLoading = updateDescState is Resource.Loading,
            text = "Save",
            onClick = { viewModel.updateUserDescription(userId ?: "") }
        )
    }
}
package com.elearn.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elearn.presentation.ui.screens.home.HomeEvent
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.viewmodel.course.ClassListViewModel

@Composable
fun JoinClassForm(
    viewModel: ClassListViewModel = hiltViewModel(),
    isLoading: Boolean = false
) {
    var code by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            Text("Class Code")
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                placeholder = { Text("Enter class code") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor, unfocusedBorderColor = MutedColor
                )
            )
        }

        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            isLoading = isLoading,
            enabled = !isLoading,
            onClick = {
                viewModel.joinClass(code)
            },
            text = "Join"
        )
    }
}
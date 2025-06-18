package com.elearn.presentation.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

    LaunchedEffect(Unit) {
        HomeEventBus.events.collectLatest { event ->
            when (event) {
                is HomeEvent.CreatedClass -> onDismiss()
                else -> {}
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Class Name")
            OutlinedTextField(
                value = formState.className,
                onValueChange = remember { { viewModel.onClassNameChanged(it) } },
                placeholder = { Text("Enter class name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor, unfocusedBorderColor = MutedColor
                )
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Description")
            OutlinedTextField(
                value = formState.description,
                onValueChange = remember { { viewModel.onDescriptionChanged(it) } },
                placeholder = { Text("Enter description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                singleLine = false,
                shape = RoundedCornerShape(18),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor, unfocusedBorderColor = MutedColor
                )
            )
        }

        CustomButton(
            modifier = Modifier.fillMaxWidth(),
            isLoading = isLoading,
            enabled = !isLoading,
            onClick = {
                classListViewModel.createClass(
                    CreateCourseRequest(
                        name = formState.className,
                        description = formState.description
                    )
                )
            },
            text = "Save"
        )
    }
}
package com.elearn.presentation.ui.components

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elearn.presentation.ui.screens.home.HomeEvent
import com.elearn.presentation.ui.screens.home.HomeEventBus
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.presentation.viewmodel.course.ClassListViewModel
import com.elearn.presentation.viewmodel.material.MaterialFormViewModel
import com.elearn.presentation.viewmodel.material.MaterialViewModel
import com.elearn.utils.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialForm(
    viewModel: MaterialFormViewModel = hiltViewModel(),
    materialViewModel: MaterialViewModel = hiltViewModel(),
    isInClass: Boolean = false,
    classId: String? = null,
    onSuccess: () -> Unit = {}
) {
    /* State Config */
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val createdMaterial by materialViewModel.createMaterialState.collectAsState()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    /* Form */
    val formState = viewModel.state.value

    /* Sheet */
    var selectClass by remember { mutableStateOf(false) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val context = LocalContext.current
    val filePickerLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onSelectedFileChanged(
                uri = it,
                fileName = it.lastPathSegment ?: "Unknown file"
            )
        }
    }

    LaunchedEffect(Unit) {
        HomeEventBus.events.collectLatest {
            when (it) {
                is HomeEvent.CreatedMaterial -> {
                    onSuccess()
                    viewModel.resetState()
                }
                else -> {}
            }
        }
    }

    /* Bottom Sheet */
    if (selectClass) {
        ModalBottomSheet(
            onDismissRequest = { selectClass = false },
            sheetState = sheetState,
            containerColor = PrimaryForegroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.6f)
            ) {
                SelectClass(
                    onClassSelected = { id, name ->
                        scope.launch {
                            viewModel.onClassChanged(
                                selectedClass = name,
                                classId = id
                            )
                            sheetState.hide()
                            selectClass = false
                        }
                    }
                )
            }
        }
    }


    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        if (!isInClass && classId?.isEmpty() ?: true) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text("Class")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp, color = MutedColor, shape = RoundedCornerShape(22)
                        )
                        .padding(16.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { selectClass = !selectClass }
                        )
                ) {
                    Text(
                        text = formState.selectedClass ?: "", fontSize = 16.sp
                    )
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Material Name")
            OutlinedTextField(
                value = formState.materialName,
                onValueChange = remember { { viewModel.onMaterialNameChanged(it) } },
                placeholder = { Text("Enter material name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryColor,
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = MutedColor
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
                shape = RoundedCornerShape(16),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryColor,
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = MutedColor
                )
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "Uploaded File: ${formState.selectedFileName}",
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryColor
            )

            CustomButton(
                onClick = {
                    filePickerLaunch.launch("*/*")
                },
                text = "Upload Material File"
            )
        }

        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = {
                materialViewModel.createMaterial(
                    context = context,
                    fileUri = formState.selectedFileUri!!,
                    name = formState.materialName,
                    description = formState.description.ifBlank { null },
                    classId = formState.selectedClassId ?: classId ?: ""
                )
            },
            isLoading = createdMaterial is Resource.Loading,
            enabled = createdMaterial !is Resource.Loading,
            text = "Save"
        )
    }
}

@Composable
fun SelectClass(
    classListViewModel: ClassListViewModel = hiltViewModel(),
    onClassSelected: (String, String) -> Unit = { _, _ -> }
) {

    val classes by classListViewModel.classes.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val filteredClasses = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            classes.data?.data
        } else {
            classes.data?.data?.let {
                it.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        item {
            Box(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                SearchInput(
                    query = searchQuery,
                    placeholder = "Search Class",
                    onQueryChanged = { searchQuery = it }
                )
            }
        }

        when (classes) {
            is Resource.Success -> {
                filteredClasses?.let {
                    items(
                        items = it,
                        key = { it.id }
                    ) { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { onClassSelected(item.id, item.name) }
                                )
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                modifier = Modifier.padding(18.dp),
                                text = item.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        HorizontalDivider(color = MutedColor, thickness = 1.dp)
                    }
                }
            }

            is Resource.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            else -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = classes.message ?: "Unknown error occured",
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(
                                onClick = { classListViewModel.fetchClasses() }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.size(52.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MaterialFormPreview() {
    MaterialForm()
}
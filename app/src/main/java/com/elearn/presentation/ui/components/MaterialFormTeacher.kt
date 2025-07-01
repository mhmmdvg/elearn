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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    isEdit: Boolean = false,
    materialId: String? = null,
    onSuccess: () -> Unit = {}
) {
    /* State Config */
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val createdMaterial by materialViewModel.createMaterialState.collectAsState()
    val editedMaterial by materialViewModel.editMaterialState.collectAsState()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    /* Form */
    val formState = viewModel.state.value

    /* Validation */
    var materialNameTouched by remember { mutableStateOf(false) }
    var descriptionTouched by remember { mutableStateOf(false) }
    var classTouched by remember { mutableStateOf(false) }
    var fileTouched by remember { mutableStateOf(false) }

    val materialNameError = remember(formState.materialName, materialNameTouched) {
        if (materialNameTouched) viewModel.getMaterialNameError() else null
    }

    val descriptionError = remember(formState.description, descriptionTouched) {
        if (descriptionTouched) viewModel.getDescriptionError() else null
    }

    val classError = remember(
        formState.selectedClass,
        formState.selectedClassId,
        isInClass,
        classId,
        classTouched
    ) {
        if (!isInClass && classId?.isEmpty() ?: true && classTouched) {
            viewModel.getClassError()
        } else null
    }

    val fileError =
        remember(formState.selectedFileUri, formState.selectedFileName, fileTouched, isEdit) {
            if (fileTouched && !isEdit) viewModel.getFileError() else null
        }

    val isFormValid = remember(
        formState.materialName,
        formState.description,
        formState.selectedClass,
        formState.selectedClassId,
        formState.selectedFileUri,
        formState.selectedFileName
    ) {
        derivedStateOf {
            val basicValidation = viewModel.getMaterialNameError() == null &&
                    viewModel.getDescriptionError() == null &&
                    (if (!isInClass && (classId?.isEmpty() ?: true)) {
                        viewModel.getClassError() == null
                    } else {
                        true
                    })

            val fileValidation = if (isEdit) {
                true
            } else {
                viewModel.getFileError() == null && formState.selectedFileUri != null
            }

            basicValidation && fileValidation
        }
    }


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

                is HomeEvent.EditedMaterial -> {
                    onSuccess()
                    viewModel.resetState()
                    materialViewModel.fetchMaterialDetail(materialId ?: "")
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
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = if (isEdit) "Edit Material" else "Create Material",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
        )
        // Class Selection (only show if not in class and no classId provided)
        if (!isInClass && classId?.isEmpty() ?: true) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "Class",
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (classError != null) Color.Red else MutedColor,
                            shape = RoundedCornerShape(22)
                        )
                        .padding(16.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                classTouched = true
                                selectClass = !selectClass
                            }
                        )
                ) {
                    Text(
                        text = formState.selectedClass ?: "Select a class",
                        fontSize = 16.sp,
                        color = if (formState.selectedClass == null) MutedColor else PrimaryColor
                    )
                }
                // Error message for class
                classError?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }

        // Material Name Field
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Material Name",
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = formState.materialName,
                onValueChange = remember {
                    { value ->
                        materialNameTouched = true
                        viewModel.onMaterialNameChanged(value)
                    }
                },
                placeholder = { Text("Enter material name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22),
                isError = materialNameError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryColor,
                    focusedBorderColor = if (materialNameError != null) Color.Red else PrimaryColor,
                    unfocusedBorderColor = if (materialNameError != null) Color.Red else MutedColor,
                    unfocusedTextColor = PrimaryColor,
                    errorBorderColor = Color.Red,
                    errorTextColor = PrimaryColor
                )
            )
            // Error message for material name
            materialNameError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        // Description Field
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Description",
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = formState.description,
                onValueChange = remember {
                    { value ->
                        descriptionTouched = true
                        viewModel.onDescriptionChanged(value)
                    }
                },
                placeholder = { Text("Enter description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                singleLine = false,
                shape = RoundedCornerShape(16),
                isError = descriptionError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryColor,
                    focusedBorderColor = if (descriptionError != null) Color.Red else PrimaryColor,
                    unfocusedBorderColor = if (descriptionError != null) Color.Red else MutedColor,
                    unfocusedTextColor = PrimaryColor,
                    errorBorderColor = Color.Red,
                    errorTextColor = PrimaryColor
                )
            )
            // Error message for description
            descriptionError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        // File Upload Field
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "File Upload",
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Uploaded File: " + formState.selectedFileName.ifBlank { "No file selected" },
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryColor,
                modifier = Modifier.padding(start = 16.dp)
            )

            CustomButton(
                onClick = {
                    fileTouched = true
                    filePickerLaunch.launch("*/*")
                },
                text = if (formState.selectedFileName.isBlank()) "Upload Material File" else "Change File"
            )

            // Error message for file
            fileError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = {
                materialNameTouched = true
                descriptionTouched = true
                if (!isInClass && classId?.isEmpty() ?: true) {
                    classTouched = true
                }
                fileTouched = true

                if (!isFormValid.value) return@CustomButton

                if (isEdit) {
                    materialViewModel.putMaterial(
                        materialId = materialId ?: "",
                        context = context,
                        fileUri = formState.selectedFileUri,
                        name = formState.materialName,
                        description = formState.description.ifBlank { null },
                    )
                    return@CustomButton
                }

                materialViewModel.createMaterial(
                    context = context,
                    fileUri = formState.selectedFileUri!!,
                    name = formState.materialName,
                    description = formState.description.ifBlank { null },
                    classId = formState.selectedClassId ?: classId ?: ""
                )
            },
            isLoading = createdMaterial is Resource.Loading || editedMaterial is Resource.Loading,
            enabled = isFormValid.value && (createdMaterial !is Resource.Loading && editedMaterial !is Resource.Loading),
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
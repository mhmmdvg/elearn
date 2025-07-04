package com.elearn.presentation.ui.screens.details.course

import ActionBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.elearn.domain.model.CourseData
import com.elearn.domain.model.CourseResponse
import com.elearn.presentation.Screen
import com.elearn.presentation.ui.components.ButtonVariant
import com.elearn.presentation.ui.components.CustomButton
import com.elearn.presentation.ui.components.MaterialForm
import com.elearn.presentation.ui.components.shimmerEffect
import com.elearn.presentation.ui.screens.auth.AuthViewModel
import com.elearn.presentation.ui.screens.details.course.components.CourseDetailSkeleton
import com.elearn.presentation.ui.screens.details.course.components.EmptyMaterialsState
import com.elearn.presentation.ui.screens.details.course.components.EnhancedMaterialCard
import com.elearn.presentation.ui.screens.details.course.components.MaterialCardSkeleton
import com.elearn.presentation.ui.screens.home.HomeEvent
import com.elearn.presentation.ui.screens.home.HomeEventBus
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.presentation.viewmodel.material.MaterialFormViewModel
import com.elearn.utils.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class EditType {
    TITLE, DESCRIPTION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    modifier: Modifier = Modifier,
    courseId: String,
    navController: NavController,
    materialFormViewModel: MaterialFormViewModel = hiltViewModel(),
    courseDetailViewModel: CourseDetailViewModel = hiltViewModel(),
    userViewModel: AuthViewModel = hiltViewModel()
) {

    /* State */
    val courseDetailState by courseDetailViewModel.courseDetailState.collectAsState()
    val materialByClassState by courseDetailViewModel.materialClassState.collectAsState()
    val courseNameUpdated by courseDetailViewModel.courseNameUpdated.collectAsState()
    val courseDescriptionUpdated by courseDetailViewModel.courseDescriptionUpdated.collectAsState()
    val userInfo by userViewModel.userInfoState.collectAsState()
    var addMaterial by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    // Bottom sheet states
    var showBottomSheet by remember { mutableStateOf(false) }
    var editType by remember { mutableStateOf(EditType.TITLE) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    // Initial fetch
    LaunchedEffect(courseId) {
        courseDetailViewModel.fetchCourseDetail(courseId)
        courseDetailViewModel.fetchMaterialByClass(courseId)
    }

    LaunchedEffect(courseNameUpdated) {
        when (courseNameUpdated) {
            is Resource.Success -> {
                courseDetailViewModel.fetchCourseDetail(courseId)
            }

            is Resource.Error -> {}
            else -> {}
        }
    }

    LaunchedEffect(courseDescriptionUpdated) {
        when (courseDescriptionUpdated) {
            is Resource.Success -> {
                courseDetailViewModel.fetchCourseDetail(courseId)
            }

            is Resource.Error -> {}
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        HomeEventBus.events.collectLatest {
            if (it is HomeEvent.CreatedMaterial || it is HomeEvent.DeletedMaterial) {
                courseDetailViewModel.fetchMaterialByClass(courseId)
            }
        }
    }

    LaunchedEffect(courseDetailState) {
        if (courseDetailState !is Resource.Loading) {
            isRefreshing = false
        }
    }


    Column {
        ActionBar(
            title = courseDetailState.data?.data?.name ?: "Class",
            onBackClick = { navController.popBackStack() }
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    courseDetailViewModel.fetchCourseDetail(courseId, isRefreshing)
                    courseDetailViewModel.fetchMaterialByClass(courseId, isRefreshing)
                }
            ) {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    item {
                        when (courseDetailState) {
                            is Resource.Loading -> {
                                CourseDetailSkeleton()
                            }

                            is Resource.Success -> {
                                CourseDetailCard(
                                    courseDetailState = courseDetailState,
                                    role = userInfo.data?.data?.role?.name ?: "teacher",
                                    onEditTitle = {
                                        editType = EditType.TITLE
                                        showBottomSheet = true
                                    },
                                    onEditDescription = {
                                        editType = EditType.DESCRIPTION
                                        showBottomSheet = true
                                    }
                                )
                            }

                            is Resource.Error -> {
                                CourseDetailCard(
                                    courseDetailState = courseDetailState,
                                    role = userInfo.data?.data?.role?.name ?: "teacher",
                                    onEditTitle = {
                                        editType = EditType.TITLE
                                        showBottomSheet = true
                                    },
                                    onEditDescription = {
                                        editType = EditType.DESCRIPTION
                                        showBottomSheet = true
                                    }
                                )
                            }

                            else -> {
                                CourseDetailSkeleton()
                            }
                        }
                    }

                    // Section Title
                    item {
                        when (materialByClassState) {
                            is Resource.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(24.dp)
                                        .shimmerEffect()
                                )
                            }

                            else -> {
                                Text(
                                    text = "Course Materials",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    // Materials List
                    when (materialByClassState) {
                        is Resource.Loading -> {
                            items(3) { // Show 3 skeleton cards
                                MaterialCardSkeleton()
                            }
                        }

                        is Resource.Success -> {
                            val materials = materialByClassState.data?.data?.materials
                            if (materials.isNullOrEmpty()) {
                                // Empty State
                                item {
                                    EmptyMaterialsState()
                                }
                            } else {
                                items(
                                    items = materials,
                                    key = { it.id }
                                ) { item ->
                                    EnhancedMaterialCard(
                                        material = item,
                                        onClick = {
                                            navController.navigate(
                                                Screen.MaterialDetail.createRoute(
                                                    item.id
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        is Resource.Error -> {
                            item {
                                EmptyMaterialsState(
                                    title = "Failed to Load Materials",
                                    description = "Something went wrong while loading course materials. Please try again.",
                                    isError = true
                                )
                            }
                        }
                    }
                }
            }

            when (userInfo.data?.data?.role?.name) {
                "teacher" -> {
                    FloatingActionButton(
                        onClick = { addMaterial = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        containerColor = PrimaryColor,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Lucide.Plus,
                            contentDescription = "Add",
                            tint = PrimaryForegroundColor
                        )
                    }
                }

                else -> {}
            }
        }
    }

    // Bottom Sheet for Editing
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState,
            containerColor = PrimaryForegroundColor
        ) {
            EditBottomSheetContent(
                editType = editType,
                currentTitle = courseDetailState.data?.data?.name ?: "",
                currentDescription = courseDetailState.data?.data?.description ?: "",
                onSave = { newTitle, newDescription ->
                    when (editType) {
                        EditType.TITLE -> courseDetailViewModel.updateCourseName(courseId, newTitle)
                        EditType.DESCRIPTION -> courseDetailViewModel.updateCourseDescription(
                            courseId,
                            newDescription
                        )
                    }
                    scope.launch {
                        bottomSheetState.hide()
                        showBottomSheet = false
                    }
                },
                onCancel = {
                    scope.launch {
                        bottomSheetState.hide()
                        showBottomSheet = false
                    }
                }
            )
        }
    }

    if (addMaterial) {
        ModalBottomSheet(
            onDismissRequest = {
                addMaterial = false
                materialFormViewModel.resetState()
            },
            sheetState = bottomSheetState,
            containerColor = Color.White
        ) {
            MaterialForm(
                isInClass = true,
                classId = courseDetailState.data?.data?.id,
                onSuccess = {
                    scope.launch {
                        bottomSheetState.hide()
                        addMaterial = false
                        materialFormViewModel.resetState()
                    }
                }
            )
        }
    }
}


@Composable
private fun CourseDetailCard(
    courseDetailState: Resource<CourseResponse<CourseData>>,
    onEditTitle: () -> Unit,
    onEditDescription: () -> Unit,
    role: String = "teacher"
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PrimaryForegroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = MutedColor,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            courseDetailState.data?.data?.code?.let { code ->
                EnhancedCourseCode(
                    courseCode = code
                )
            }
            // Course Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = (courseDetailState as? Resource.Success<CourseResponse<CourseData>>)?.data?.data?.name
                        ?: "Course Title",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (role == "teacher") {
                    IconButton(onClick = onEditTitle) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Title",
                            tint = MutedForegroundColor
                        )
                    }
                }
            }

            // Course Description
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Description",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    if (role == "teacher") {
                        IconButton(onClick = onEditDescription) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Description",
                                tint = MutedForegroundColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Text(
                    text = (courseDetailState as? Resource.Success<CourseResponse<CourseData>>)?.data?.data?.description
                        ?: "",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MutedForegroundColor,
                    style = TextStyle(
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both
                        )
                    )
                )
            }

            // Course Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CourseStatItem(
                    icon = Icons.Default.Person,
                    label = "Students",
                    value = (courseDetailState as? Resource.Success<CourseResponse<CourseData>>)?.data?.data?._count?.enrollments.toString()
                        ?: "0"
                )
            }
        }
    }
}

@Composable
private fun EditBottomSheetContent(
    editType: EditType,
    currentTitle: String,
    currentDescription: String,
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var editableTitle by remember(currentTitle) { mutableStateOf(currentTitle) }
    var editableDescription by remember(currentDescription) { mutableStateOf(currentDescription) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (editType) {
                    EditType.TITLE -> "Edit Course Title"
                    EditType.DESCRIPTION -> "Edit Course Description"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Input Field
        when (editType) {
            EditType.TITLE -> {
                Column {
                    Text(
                        text = "Course Title",
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = editableTitle,
                        onValueChange = { editableTitle = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        placeholder = {
                            Text(
                                text = "Enter course title...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                onSave(editableTitle, currentDescription)
                            }
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
                        text = "${editableTitle.length}/100",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            EditType.DESCRIPTION -> {
                Column {
                    Text(
                        text = "Course Description",
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = editableDescription,
                        onValueChange = { editableDescription = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .focusRequester(focusRequester),
                        placeholder = {
                            Text(
                                text = "Tell us about this course...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        maxLines = 5,
                        singleLine = false,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                onSave(currentTitle, editableDescription)
                            }
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
                        text = "${editableDescription.length}/500",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                text = "Cancel",
                variant = ButtonVariant.Outline
            )

            CustomButton(
                onClick = {
                    focusManager.clearFocus()
                    when (editType) {
                        EditType.TITLE -> onSave(editableTitle, currentDescription)
                        EditType.DESCRIPTION -> onSave(currentTitle, editableDescription)
                    }
                },
                modifier = Modifier.weight(1f),
                text = "Save"
            )
        }

        // Add some bottom padding for better UX
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CourseStatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MutedColor.copy(alpha = 0.2f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(20.dp),
                    tint = MutedForegroundColor
                )
            }
        }
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MutedForegroundColor
        )
    }
}

@Composable
private fun EnhancedCourseCode(
    courseCode: String,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                clipboardManager.setText(AnnotatedString(courseCode))
                // You can add a snackbar or toast here to show copy confirmation
            }
            .background(
                color = PrimaryColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Class Code",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryColor,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = courseCode,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor,
                    letterSpacing = 1.sp
                )
            }

            Surface(
                shape = CircleShape,
                color = PrimaryColor.copy(alpha = 0.2f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Copy,
                        contentDescription = "Copy Code",
                        modifier = Modifier.size(18.dp),
                        tint = PrimaryColor
                    )
                }
            }
        }
    }
}
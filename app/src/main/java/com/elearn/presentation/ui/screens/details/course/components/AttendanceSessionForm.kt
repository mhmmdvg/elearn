package com.elearn.presentation.ui.screens.details.course.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.elearn.domain.model.AttendanceSessionsReq
import com.elearn.presentation.ui.components.CustomButton
import com.elearn.presentation.ui.components.TimePickerDialog
import com.elearn.presentation.ui.screens.details.course.CourseDetailEvent
import com.elearn.presentation.ui.screens.details.course.CourseDetailEventBus
import com.elearn.presentation.ui.theme.AccentColor
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.presentation.viewmodel.attendance.AttendanceFormViewModel
import com.elearn.presentation.viewmodel.attendance.AttendanceViewModel
import com.elearn.utils.Resource
import com.elearn.utils.TimeUtils
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceSessionForm(
    modifier: Modifier = Modifier,
    formViewModel: AttendanceFormViewModel = hiltViewModel(),
    viewModel: AttendanceViewModel = hiltViewModel(),
    classId: String,
    onSuccess: () -> Unit
) {
    val state = formViewModel.state.value
    val scrollState = rememberScrollState()
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val attendanceSessionCreated by viewModel.attendanceSessionCreated.collectAsState()

    // Get screen configuration for responsive design
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isCompact = screenWidth < 600.dp
    val isTablet = screenWidth >= 600.dp

    // Date and Time picker states
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // Form validation states
    var titleTouched by remember { mutableStateOf(false) }
    var startTimeTouched by remember { mutableStateOf(false) }
    var endTimeTouched by remember { mutableStateOf(false) }

    // Responsive values
    val horizontalPadding = if (isTablet) 24.dp else 16.dp
    val verticalSpacing = if (isTablet) 20.dp else 16.dp
    val titleFontSize = if (isTablet) 24.sp else 20.sp
    val labelFontSize = if (isTablet) 18.sp else 16.sp
    val descriptionHeight = if (isTablet) 140.dp else 120.dp
    val cornerRadius = if (isTablet) 28.dp else 18.dp
    val iconSize = if (isTablet) 24.dp else 20.dp
    val buttonSpacing = if (isTablet) 24.dp else 16.dp

    // Validation logic
    val titleError = remember(state.title, titleTouched) {
        if (titleTouched && state.title.isBlank()) "Title is required" else null
    }

    val startTimeError = remember(state.startTime, startTimeTouched) {
        if (startTimeTouched && state.startTime.isBlank()) "Start time is required" else null
    }

    val endTimeError = remember(state.endTime, endTimeTouched) {
        if (endTimeTouched && state.endTime.isBlank()) "End time is required" else null
    }

    val timeValidationError = remember(state.startTime, state.endTime) {
        if (state.startTime.isNotBlank() && state.endTime.isNotBlank()) {
            try {
                val startDateTime = TimeUtils.parseUtcToLocal(state.startTime)
                val endDateTime = TimeUtils.parseUtcToLocal(state.endTime)

                if (startDateTime != null && endDateTime != null) {
                    if (endDateTime.isBefore(startDateTime) || endDateTime.isEqual(startDateTime)) {
                        "End time must be after start time"
                    } else null
                } else null
            } catch (e: Exception) {
                null
            }
        } else null
    }

    val isFormValid = remember(
        state.title,
        state.startTime,
        state.endTime,
        titleError,
        startTimeError,
        endTimeError,
        timeValidationError
    ) {
        derivedStateOf {
            state.title.isNotBlank() &&
                    state.startTime.isNotBlank() &&
                    state.endTime.isNotBlank() &&
                    titleError == null &&
                    startTimeError == null &&
                    endTimeError == null &&
                    timeValidationError == null
        }
    }

    LaunchedEffect(Unit) {
        CourseDetailEventBus.events.collectLatest {
            when (it) {
                is CourseDetailEvent.CreateAttendanceSession -> {
                    onSuccess()
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .padding(vertical = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        // Header
        Text(
            text = "Create Attendance Session",
            fontSize = titleFontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Title Field
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Session Title",
                fontSize = labelFontSize,
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = state.title,
                onValueChange = { value ->
                    titleTouched = true
                    formViewModel.onTitleChanged(value)
                },
                placeholder = { Text("e.g., Week 1 - Introduction") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(cornerRadius),
                isError = titleError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryColor,
                    focusedBorderColor = if (titleError != null) Color.Red else PrimaryColor,
                    unfocusedBorderColor = if (titleError != null) Color.Red else MutedColor,
                    unfocusedTextColor = PrimaryColor,
                    errorBorderColor = Color.Red,
                    errorTextColor = PrimaryColor
                )
            )
            titleError?.let { error ->
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
                fontSize = labelFontSize,
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = { formViewModel.onDescriptionChanged(it) },
                placeholder = { Text("Session description...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(descriptionHeight),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryColor,
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = MutedColor,
                    unfocusedTextColor = PrimaryColor
                )
            )
        }

        // Start Time Section
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Start Time",
                fontSize = labelFontSize,
                fontWeight = FontWeight.Medium
            )

            if (isCompact) {
                // Vertical layout for compact screens
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Start Date
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (startTimeError != null) Color.Red else MutedColor,
                                shape = RoundedCornerShape(cornerRadius)
                            )
                            .padding(16.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    startTimeTouched = true
                                    showStartDatePicker = true
                                }
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Calendar,
                                contentDescription = "Calendar",
                                modifier = Modifier.size(iconSize),
                                tint = if (state.startTime.isNotBlank()) PrimaryColor else MutedColor
                            )
                            Text(
                                text = if (state.startTime.isNotBlank()) {
                                    try {
                                        TimeUtils.parseUtcToLocal(state.startTime)
                                            ?.format(dateFormatter) ?: "Select Date"
                                    } catch (e: Exception) {
                                        "Select Date"
                                    }
                                } else "Select Date",
                                color = if (state.startTime.isNotBlank()) PrimaryColor else MutedColor
                            )
                        }
                    }

                    // Start Time
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (startTimeError != null) Color.Red else MutedColor,
                                shape = RoundedCornerShape(cornerRadius)
                            )
                            .padding(16.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    startTimeTouched = true
                                    showStartTimePicker = true
                                }
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Clock,
                                contentDescription = "Clock",
                                modifier = Modifier.size(iconSize),
                                tint = if (state.startTime.isNotBlank()) PrimaryColor else MutedColor
                            )
                            Text(
                                text = if (state.startTime.isNotBlank()) {
                                    try {
                                        TimeUtils.parseUtcToLocal(state.startTime)
                                            ?.format(timeFormatter) ?: "Select Time"
                                    } catch (e: Exception) {
                                        "Select Time"
                                    }
                                } else "Select Time",
                                color = if (state.startTime.isNotBlank()) PrimaryColor else MutedColor
                            )
                        }
                    }
                }
            } else {
                // Horizontal layout for larger screens
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Start Date
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = if (startTimeError != null) Color.Red else MutedColor,
                                    shape = RoundedCornerShape(cornerRadius)
                                )
                                .padding(16.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        startTimeTouched = true
                                        showStartDatePicker = true
                                    }
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Calendar,
                                    contentDescription = "Calendar",
                                    modifier = Modifier.size(iconSize),
                                    tint = if (state.startTime.isNotBlank()) PrimaryColor else MutedColor
                                )
                                Text(
                                    text = if (state.startTime.isNotBlank()) {
                                        try {
                                            TimeUtils.parseUtcToLocal(state.startTime)
                                                ?.format(dateFormatter) ?: "Select Date"
                                        } catch (e: Exception) {
                                            "Select Date"
                                        }
                                    } else "Select Date",
                                    color = if (state.startTime.isNotBlank()) PrimaryColor else MutedColor
                                )
                            }
                        }
                    }

                    // Start Time
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = if (startTimeError != null) Color.Red else MutedColor,
                                    shape = RoundedCornerShape(cornerRadius)
                                )
                                .padding(16.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        startTimeTouched = true
                                        showStartTimePicker = true
                                    }
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Clock,
                                    contentDescription = "Clock",
                                    modifier = Modifier.size(iconSize),
                                    tint = if (state.startTime.isNotBlank()) PrimaryColor else MutedColor
                                )
                                Text(
                                    text = if (state.startTime.isNotBlank()) {
                                        try {
                                            TimeUtils.parseUtcToLocal(state.startTime)
                                                ?.format(timeFormatter) ?: "Select Time"
                                        } catch (e: Exception) {
                                            "Select Time"
                                        }
                                    } else "Select Time",
                                    color = if (state.startTime.isNotBlank()) PrimaryColor else MutedColor
                                )
                            }
                        }
                    }
                }
            }

            startTimeError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        // End Time Section
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "End Time",
                fontSize = labelFontSize,
                fontWeight = FontWeight.Medium
            )

            if (isCompact) {
                // Vertical layout for compact screens
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // End Date
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (endTimeError != null) Color.Red else MutedColor,
                                shape = RoundedCornerShape(cornerRadius)
                            )
                            .padding(16.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    endTimeTouched = true
                                    showEndDatePicker = true
                                }
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Calendar,
                                contentDescription = "Calendar",
                                modifier = Modifier.size(iconSize),
                                tint = if (state.endTime.isNotBlank()) PrimaryColor else MutedColor
                            )
                            Text(
                                text = if (state.endTime.isNotBlank()) {
                                    try {
                                        TimeUtils.parseUtcToLocal(state.endTime)
                                            ?.format(dateFormatter) ?: "Select Date"
                                    } catch (e: Exception) {
                                        "Select Date"
                                    }
                                } else "Select Date",
                                color = if (state.endTime.isNotBlank()) PrimaryColor else MutedColor
                            )
                        }
                    }

                    // End Time
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (endTimeError != null) Color.Red else MutedColor,
                                shape = RoundedCornerShape(cornerRadius)
                            )
                            .padding(16.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    endTimeTouched = true
                                    showEndTimePicker = true
                                }
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Clock,
                                contentDescription = "Clock",
                                modifier = Modifier.size(iconSize),
                                tint = if (state.endTime.isNotBlank()) PrimaryColor else MutedColor
                            )
                            Text(
                                text = if (state.endTime.isNotBlank()) {
                                    try {
                                        TimeUtils.parseUtcToLocal(state.endTime)
                                            ?.format(timeFormatter) ?: "Select Time"
                                    } catch (e: Exception) {
                                        "Select Time"
                                    }
                                } else "Select Time",
                                color = if (state.endTime.isNotBlank()) PrimaryColor else MutedColor
                            )
                        }
                    }
                }
            } else {
                // Horizontal layout for larger screens
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // End Date
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = if (endTimeError != null) Color.Red else MutedColor,
                                    shape = RoundedCornerShape(cornerRadius)
                                )
                                .padding(16.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        endTimeTouched = true
                                        showEndDatePicker = true
                                    }
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Calendar,
                                    contentDescription = "Calendar",
                                    modifier = Modifier.size(iconSize),
                                    tint = if (state.endTime.isNotBlank()) PrimaryColor else MutedColor
                                )
                                Text(
                                    text = if (state.endTime.isNotBlank()) {
                                        try {
                                            TimeUtils.parseUtcToLocal(state.endTime)
                                                ?.format(dateFormatter) ?: "Select Date"
                                        } catch (e: Exception) {
                                            "Select Date"
                                        }
                                    } else "Select Date",
                                    color = if (state.endTime.isNotBlank()) PrimaryColor else MutedColor
                                )
                            }
                        }
                    }

                    // End Time
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = if (endTimeError != null) Color.Red else MutedColor,
                                    shape = RoundedCornerShape(cornerRadius)
                                )
                                .padding(16.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        endTimeTouched = true
                                        showEndTimePicker = true
                                    }
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Clock,
                                    contentDescription = "Clock",
                                    modifier = Modifier.size(iconSize),
                                    tint = if (state.endTime.isNotBlank()) PrimaryColor else MutedColor
                                )
                                Text(
                                    text = if (state.endTime.isNotBlank()) {
                                        try {
                                            TimeUtils.parseUtcToLocal(state.endTime)
                                                ?.format(timeFormatter) ?: "Select Time"
                                        } catch (e: Exception) {
                                            "Select Time"
                                        }
                                    } else "Select Time",
                                    color = if (state.endTime.isNotBlank()) PrimaryColor else MutedColor
                                )
                            }
                        }
                    }
                }
            }

            endTimeError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            timeValidationError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        // Location Requirement
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Lucide.MapPin,
                    contentDescription = "Location",
                    modifier = Modifier.size(iconSize)
                )
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Require Location",
                        fontSize = labelFontSize,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Students must be at specific location to check in",
                        fontSize = if (isTablet) 14.sp else 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                colors = SwitchDefaults.colors(
                    uncheckedTrackColor = PrimaryForegroundColor,
                    checkedTrackColor = AccentColor,
                    uncheckedThumbColor = MutedForegroundColor,
                    uncheckedBorderColor = MutedForegroundColor
                ),
                checked = state.requireLocation,
                onCheckedChange = { formViewModel.onRequireLocationChanged(it) }
            )
        }

        Spacer(modifier = Modifier.height(buttonSpacing))

        // Action Buttons
        CustomButton(
            onClick = {
                titleTouched = true
                startTimeTouched = true
                endTimeTouched = true

                if (!isFormValid.value) return@CustomButton

                val startTimeUtc = try {
                    val localStartTime = LocalDateTime.parse(state.startTime)
                    TimeUtils.localDateTimeToUtcIso(localStartTime)
                } catch (e: Exception) {
                    state.startTime
                }

                val endTimeUtc = try {
                    val localEndTime = LocalDateTime.parse(state.endTime)
                    TimeUtils.localDateTimeToUtcIso(localEndTime)
                } catch (e: Exception) {
                    state.endTime
                }

                // TODO: Call API to create attendance session
                viewModel.createAttendanceSession(
                    AttendanceSessionsReq(
                        classId = classId,
                        title = state.title,
                        description = state.description,
                        startTime = startTimeUtc,
                        endTime = endTimeUtc,
                        requireLocation = state.requireLocation
                    )
                )
            },
            isLoading = attendanceSessionCreated is Resource.Loading,
            enabled = isFormValid.value && (attendanceSessionCreated !is Resource.Loading),
            text = "Create Session",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(buttonSpacing))
    }

    // Date and Time Pickers (unchanged)
    if (showStartDatePicker) {
        val currentDateTime = TimeUtils.parseUtcToLocal(state.startTime) ?: LocalDateTime.now()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = currentDateTime.atZone(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()
        )

        DatePickerDialog(
            colors = DatePickerDefaults.colors(
                containerColor = PrimaryForegroundColor,
            ),
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()

                            val currentDate = if (state.startTime.isNotBlank()) {
                                try {
                                    LocalDateTime.parse(state.startTime)
                                } catch (e: Exception) {
                                    LocalDateTime.now()
                                }
                            } else {
                                LocalDateTime.now()
                            }

                            val newDateTime =
                                LocalDateTime.of(selectedDate, currentDate.toLocalTime())
                            formViewModel.onStartTimeChanged(newDateTime.toString())
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState, colors = DatePickerDefaults.colors(
                    containerColor = PrimaryForegroundColor,
                    selectedDayContainerColor = AccentColor,
                    selectedDayContentColor = PrimaryForegroundColor,
                    selectedYearContainerColor = AccentColor,
                    selectedYearContentColor = PrimaryForegroundColor
                )
            )
        }
    }

    if (showStartTimePicker) {
        val currentDateTime = TimeUtils.parseUtcToLocal(state.startTime) ?: LocalDateTime.now()
        val timePickerState = rememberTimePickerState(
            initialHour = currentDateTime.hour,
            initialMinute = currentDateTime.minute
        )

        TimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedTime =
                            LocalTime.of(timePickerState.hour, timePickerState.minute)

                        val currentTime = if (state.startTime.isNotBlank()) {
                            try {
                                LocalDateTime.parse(state.startTime)
                            } catch (e: Exception) {
                                LocalDateTime.now()
                            }
                        } else {
                            LocalDateTime.now()
                        }

                        val newDateTime =
                            LocalDateTime.of(currentTime.toLocalDate(), selectedTime)
                        formViewModel.onStartTimeChanged(newDateTime.toString())
                        showStartTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    containerColor = MutedColor,
                    clockDialColor = MutedColor,
                    selectorColor = AccentColor,
                    timeSelectorSelectedContainerColor = AccentColor,
                    timeSelectorSelectedContentColor = PrimaryForegroundColor,
                    periodSelectorSelectedContainerColor = AccentColor,
                    periodSelectorSelectedContentColor = PrimaryForegroundColor,
                    periodSelectorBorderColor = MutedColor
                )
            )
        }
    }

    if (showEndDatePicker) {
        val currentDateTime = TimeUtils.parseUtcToLocal(state.endTime) ?: LocalDateTime.now()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = currentDateTime.atZone(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()

                            val currentDate = if (state.endTime.isNotBlank()) {
                                try {
                                    LocalDateTime.parse(state.endTime)
                                } catch (e: Exception) {
                                    LocalDateTime.now().plusHours(1)
                                }
                            } else {
                                LocalDateTime.now().plusHours(1)
                            }

                            val newDateTime =
                                LocalDateTime.of(selectedDate, currentDate.toLocalTime())
                            formViewModel.onEndTimeChanged(newDateTime.toString())
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = PrimaryForegroundColor,
                    selectedDayContainerColor = AccentColor,
                    selectedDayContentColor = PrimaryForegroundColor,
                    selectedYearContainerColor = AccentColor,
                    selectedYearContentColor = PrimaryForegroundColor
                )
            )
        }
    }

    if (showEndTimePicker) {
        val currentDateTime = TimeUtils.parseUtcToLocal(state.endTime) ?: LocalDateTime.now()
        val timePickerState = rememberTimePickerState(
            initialHour = currentDateTime.hour,
            initialMinute = currentDateTime.minute
        )

        TimePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedTime =
                            LocalTime.of(timePickerState.hour, timePickerState.minute)

                        val currentTime = if (state.endTime.isNotBlank()) {
                            try {
                                LocalDateTime.parse(state.endTime)
                            } catch (e: Exception) {
                                LocalDateTime.now().plusHours(1)
                            }
                        } else {
                            LocalDateTime.now().plusHours(1)
                        }

                        val newDateTime =
                            LocalDateTime.of(currentTime.toLocalDate(), selectedTime)
                        formViewModel.onEndTimeChanged(newDateTime.toString())
                        showEndTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    containerColor = MutedColor,
                    clockDialColor = MutedColor,
                    selectorColor = AccentColor,
                    timeSelectorSelectedContainerColor = AccentColor,
                    timeSelectorSelectedContentColor = PrimaryForegroundColor,
                    periodSelectorSelectedContainerColor = AccentColor,
                    periodSelectorSelectedContentColor = PrimaryForegroundColor,
                    periodSelectorBorderColor = MutedColor
                )
            )
        }
    }
}
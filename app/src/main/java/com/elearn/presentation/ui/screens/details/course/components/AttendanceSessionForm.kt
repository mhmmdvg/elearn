package com.elearn.presentation.ui.screens.details.course.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.elearn.presentation.ui.components.CustomButton
import com.elearn.presentation.viewmodel.attendance.AttendanceFormViewModel
import java.time.format.DateTimeFormatter

@Composable
fun AttendanceSessionForm(
    modifier: Modifier = Modifier,
    formViewModel: AttendanceFormViewModel = hiltViewModel(),
    classId: String,
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
//    var title by remember { mutableStateOf("") }
//    var description by remember { mutableStateOf("") }
//    var startTime by remember { mutableStateOf(LocalDateTime.now()) }
//    var endTime by remember { mutableStateOf(LocalDateTime.now().plusHours(1)) }
//    var requireLocation by remember { mutableStateOf(false) }
//    var isLoading by remember { mutableStateOf(false) }
    val state = formViewModel.state.value

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Create Attendance Session",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Title Field
        OutlinedTextField(
            value = state.title,
            onValueChange = remember { { formViewModel.onTitleChanged(it) } },
            label = { Text("Session Title") },
            placeholder = { Text("e.g., Week 1 - Introduction") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Description Field
        OutlinedTextField(
            value = state.description,
            onValueChange = remember { { formViewModel.onDescriptionChanged(it) } },
            label = { Text("Description (Optional)") },
            placeholder = { Text("Session description...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        // Start Time Section
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Start Time",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Start Date
                OutlinedTextField(
                    value = state.startTime.format(dateFormatter),
                    onValueChange = remember { { formViewModel.onStartTimeChanged(it) } },
                    label = { Text("Date") },
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            imageVector = Lucide.Calendar,
                            contentDescription = "Calendar",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                // Start Time
                OutlinedTextField(
                    value = state.startTime.format(timeFormatter),
                    onValueChange = { },
                    label = { Text("Time") },
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            imageVector = Lucide.Clock,
                            contentDescription = "Clock",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }

        // End Time Section
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "End Time",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // End Date
                OutlinedTextField(
                    value = state.endTime.format(dateFormatter),
                    onValueChange = remember { { formViewModel.onEndTimeChanged(it) } },
                    label = { Text("Date") },
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            imageVector = Lucide.Calendar,
                            contentDescription = "Calendar",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                // End Time
                OutlinedTextField(
                    value = state.endTime.format(timeFormatter),
                    onValueChange = { },
                    label = { Text("Time") },
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            imageVector = Lucide.Clock,
                            contentDescription = "Clock",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    )
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Lucide.MapPin,
                    contentDescription = "Location",
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "Require Location",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Students must be at specific location to check in",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = state.requireLocation,
                onCheckedChange = remember { { formViewModel.onRequireLocationChanged(it) } }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
//            OutlinedButton(
//                onClick = onCancel,
//                modifier = Modifier.weight(1f),
//                enabled = !isLoading
//            ) {
//                Text("Cancel")
//            }

            CustomButton(
                onClick = {
                    if (state.title.isNotBlank()) {
//                        isLoading = true
                        // TODO: Call API to create attendance session
                        // For now, just simulate success
                        onSuccess()
                    }
                },
                enabled = state.title.isNotBlank(),
                text = "Create Session"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
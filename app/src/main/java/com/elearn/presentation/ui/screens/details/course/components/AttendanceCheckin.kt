package com.elearn.presentation.ui.screens.details.course.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.TriangleAlert
import com.composables.icons.lucide.UserCheck
import com.composables.icons.lucide.X
import com.elearn.domain.model.AttendanceSessionsData
import com.elearn.presentation.Screen
import com.elearn.presentation.ui.screens.details.course.CourseDetailEvent
import com.elearn.presentation.ui.screens.details.course.CourseDetailEventBus
import com.elearn.presentation.ui.theme.AccentColor
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.presentation.viewmodel.attendance.AttendanceViewModel
import com.elearn.utils.LocationPermissionHandler
import com.elearn.utils.Resource
import com.elearn.utils.TimeUtils
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AttendanceCheckinBottomSheet(
    attendanceViewModel: AttendanceViewModel = hiltViewModel(),
    session: AttendanceSessionsData,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    navController: NavController,
    isLoading: Boolean = false
) {
    var notes by remember { mutableStateOf("") }
    var showLocationPermission by remember { mutableStateOf(false) }
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var isLocationReady by remember { mutableStateOf(!session.requireLocation) }

    val attendanceCheckinCreated by attendanceViewModel.attendanceCheckinCreated.collectAsState()
    val locationState by attendanceViewModel.locationState.collectAsState()
    val context = LocalContext.current

    val startTime = TimeUtils.parseUtcToLocal(session.startTime)
    val endTime = TimeUtils.parseUtcToLocal(session.endTime)

    val currentTime = LocalDateTime.now()
    val isLate = startTime?.let {
        currentTime.isAfter(it.plusMinutes(30))
    } ?: false

    if (showLocationPermission) {
        LocationPermissionHandler(
            onPermissionGranted = {
                locationPermissionGranted = true
                showLocationPermission = false
                if (session.requireLocation) {
                    attendanceViewModel.getCurrentLocation()
                }
            },
            onPermissionDenied = {
                showLocationPermission = false
            }
        )
    }

    LaunchedEffect(locationPermissionGranted, session.requireLocation) {
        if (locationPermissionGranted && session.requireLocation) {
            attendanceViewModel.getCurrentLocation()
        }
    }

    LaunchedEffect(locationState) {
        if (session.requireLocation) {
            when (locationState) {
                is Resource.Success -> {
                    isLocationReady = locationState.data != null
                }

                is Resource.Error -> {
                    isLocationReady = false
                }

                else -> {
                    isLocationReady = false
                }
            }
        }
    }

    LaunchedEffect(session.requireLocation) {
        if (session.requireLocation) {
            showLocationPermission = true
        }
    }

    LaunchedEffect(Unit) {
        CourseDetailEventBus.events.collectLatest {
            when (it) {
                is CourseDetailEvent.CreateCheckinAttendance -> {
                    navController.navigate(Screen.AttendanceSuccess.route)
                    onSuccess()
                }

                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Check-in Attendance",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = Lucide.X,
                    contentDescription = "Close"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Session Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PrimaryForegroundColor
            ),
            border = BorderStroke(1.dp, MutedColor)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = session.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                if (!session.description.isNullOrBlank()) {
                    Text(
                        text = session.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Time Information
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.Clock,
                            contentDescription = "Time",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${startTime?.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${
                                endTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
                            }",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.Calendar,
                            contentDescription = "Date",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = startTime?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                                ?: "",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status indicator
                if (isLate) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.TriangleAlert,
                            contentDescription = "Late",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFF9800)
                        )
                        Text(
                            text = "You're checking in late",
                            fontSize = 14.sp,
                            color = Color(0xFFFF9800),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Location requirement
                if (session.requireLocation) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.MapPin,
                            contentDescription = "Location Required",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Location will be recorded",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notes Section
        Text(
            text = "Notes (Optional)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("Add any additional notes...") },
            maxLines = 4,
            enabled = !isLoading,
            shape = RoundedCornerShape(14),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PrimaryColor,
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = MutedColor,
                unfocusedTextColor = PrimaryColor,
                errorBorderColor = Color.Red,
                errorTextColor = PrimaryColor
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Check-in Button
        Button(
            onClick = {
                if (isLocationReady || !session.requireLocation) {
                    attendanceViewModel.createCheckinAttendance(
                        sessionId = session.id,
                        sessionsData = session,
                        notes = notes
                    )
                } else if (session.requireLocation && !locationPermissionGranted) {
                    showLocationPermission = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentColor,
                disabledContainerColor = MutedColor
            ),
            enabled = attendanceCheckinCreated !is Resource.Loading &&
                    (isLocationReady || !session.requireLocation)
        ) {
            if (attendanceCheckinCreated is Resource.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.UserCheck,
                        contentDescription = "Check In",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = when {
                            session.requireLocation && !locationPermissionGranted -> "Allow Location"
                            session.requireLocation && !isLocationReady -> "Getting Location..."
                            else -> "Check In Now"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
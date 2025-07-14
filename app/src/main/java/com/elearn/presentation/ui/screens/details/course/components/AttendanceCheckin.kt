package com.elearn.presentation.ui.screens.details.course.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
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

@OptIn(ExperimentalLayoutApi::class)
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

    val startTime = TimeUtils.parseUtcToLocal(session.startTime)
    val endTime = TimeUtils.parseUtcToLocal(session.endTime)

    val currentTime = LocalDateTime.now()
    val isLate = startTime?.let {
        currentTime.isAfter(it.plusMinutes(30))
    } ?: false

    // Responsive values based on screen configuration
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isCompact = screenWidth < 600.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Adaptive spacing and sizing
    val horizontalPadding = if (isCompact) 16.dp else (screenWidth * 0.1f).coerceAtMost(32.dp)
    val verticalSpacing = if (isCompact) 16.dp else 20.dp
    val cardPadding = if (isCompact) 16.dp else 20.dp
    val buttonHeight = if (isCompact) 48.dp else 52.dp
    val textFieldHeight = if (isLandscape) 100.dp else 120.dp

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
            .padding(horizontal = horizontalPadding, vertical = 16.dp)
            .navigationBarsPadding()
            .let {
                if (isLandscape) {
                    it.verticalScroll(rememberScrollState())
                } else {
                    it
                }
            }
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Check-in Attendance",
                fontSize = if (isCompact) 20.sp else 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = Lucide.X,
                    contentDescription = "Close",
                    modifier = Modifier.size(if (isCompact) 24.dp else 28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(verticalSpacing))

        // Session Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PrimaryForegroundColor
            ),
            border = BorderStroke(1.dp, MutedColor)
        ) {
            Column(
                modifier = Modifier.padding(cardPadding),
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 16.dp)
            ) {
                Text(
                    text = session.title,
                    fontSize = if (isCompact) 18.sp else 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor,
                    lineHeight = if (isCompact) 24.sp else 28.sp
                )

                if (session.description.isNotBlank()) {
                    Text(
                        text = session.description,
                        fontSize = if (isCompact) 14.sp else 16.sp,
                        color = MutedForegroundColor,
                        lineHeight = if (isCompact) 20.sp else 24.sp
                    )
                }

                // Time Information - Stack vertically on small screens
                if (isCompact && !isLandscape) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TimeInfoRow(
                            icon = Lucide.Clock,
                            text = "${startTime?.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${
                                endTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
                            }",
                            isCompact = isCompact
                        )
                        TimeInfoRow(
                            icon = Lucide.Calendar,
                            text = startTime?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "",
                            isCompact = isCompact
                        )
                    }
                } else {
                    // Side by side layout for larger screens or landscape
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TimeInfoRow(
                            icon = Lucide.Clock,
                            text = "${startTime?.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${
                                endTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
                            }",
                            isCompact = isCompact
                        )
                        TimeInfoRow(
                            icon = Lucide.Calendar,
                            text = startTime?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "",
                            isCompact = isCompact
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
                            modifier = Modifier.size(if (isCompact) 16.dp else 18.dp),
                            tint = Color(0xFFFF9800)
                        )
                        Text(
                            text = "You're checking in late",
                            fontSize = if (isCompact) 14.sp else 16.sp,
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
                            modifier = Modifier.size(if (isCompact) 16.dp else 18.dp),
                            tint = MutedForegroundColor
                        )
                        Text(
                            text = "Location will be recorded",
                            fontSize = if (isCompact) 14.sp else 16.sp,
                            color = MutedForegroundColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(verticalSpacing))

        // Notes Section
        Text(
            text = "Notes (Optional)",
            fontSize = if (isCompact) 16.sp else 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = if (isCompact) 8.dp else 12.dp)
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(textFieldHeight),
            placeholder = {
                Text(
                    "Add any additional notes...",
                    fontSize = if (isCompact) 14.sp else 16.sp
                )
            },
            maxLines = if (isLandscape) 3 else 4,
            enabled = !isLoading,
            shape = RoundedCornerShape(14),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PrimaryColor,
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = MutedColor,
                unfocusedTextColor = PrimaryColor,
                errorBorderColor = Color.Red,
                errorTextColor = PrimaryColor
            ),
            textStyle = LocalTextStyle.current.copy(
                fontSize = if (isCompact) 14.sp else 16.sp
            )
        )

        Spacer(modifier = Modifier.height(if (isCompact) 20.dp else 24.dp))

        // Check-in Button
        Button(
            onClick = {
                if (isLocationReady || !session.requireLocation) {
                    attendanceViewModel.createCheckinAttendance(
                        sessionId = session.id,
                        sessionsData = session,
                        notes = notes
                    )
                } else if (!locationPermissionGranted) {
                    showLocationPermission = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentColor,
                disabledContainerColor = MutedColor
            ),
            enabled = attendanceCheckinCreated !is Resource.Loading &&
                    (isLocationReady || !session.requireLocation)
        ) {
            if (attendanceCheckinCreated is Resource.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(if (isCompact) 20.dp else 24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.UserCheck,
                        contentDescription = "Check In",
                        modifier = Modifier.size(if (isCompact) 20.dp else 24.dp),
                        tint = PrimaryForegroundColor
                    )
                    Text(
                        text = when {
                            session.requireLocation && !locationPermissionGranted -> "Allow Location"
                            session.requireLocation && !isLocationReady -> "Getting Location..."
                            else -> "Check In Now"
                        },
                        fontSize = if (isCompact) 16.sp else 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryForegroundColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 20.dp))
    }
}

@Composable
private fun TimeInfoRow(
    icon: ImageVector,
    text: String,
    isCompact: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(if (isCompact) 16.dp else 18.dp),
            tint = PrimaryColor.copy(0.8f)
        )
        Text(
            text = text,
            fontSize = if (isCompact) 14.sp else 16.sp,
            color = PrimaryColor.copy(0.8f)
        )
    }
}
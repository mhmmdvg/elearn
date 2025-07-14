package com.elearn.presentation.ui.screens.success.attendance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.TriangleAlert
import com.composables.icons.lucide.X
import com.elearn.presentation.ui.components.LottieSuccess
import com.elearn.presentation.ui.theme.AccentColor
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.presentation.viewmodel.attendance.AttendanceViewModel
import com.elearn.utils.TimeUtils
import java.time.format.DateTimeFormatter

@Composable
fun AttendanceCheckinSuccessScreen(
    viewModel: AttendanceViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onViewDetails: () -> Unit = {}
) {
    val attendanceCheckinCreated by viewModel.attendanceCheckinCreated.collectAsState()
    val currentSession by viewModel.currentSession.collectAsState()

    val checkinTime =
        TimeUtils.parseUtcToLocal(attendanceCheckinCreated.data?.data?.checkInTime ?: "")

    // Check if late
    val isLate = attendanceCheckinCreated.data?.data?.status == "LATE"

    // Get screen configuration for responsive design
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isTablet = screenWidth > 600.dp
    val isLandscape = screenWidth > screenHeight

    // Responsive values
    val contentPadding = if (isTablet) 32.dp else 16.dp
    val maxContentWidth = if (isTablet) 600.dp else screenWidth
    val lottieSize = when {
        isTablet -> 240.dp
        isLandscape -> 140.dp
        screenHeight < 600.dp -> 120.dp
        else -> 180.dp
    }
    val titleFontSize = if (isTablet) 28.sp else 22.sp
    val bodyFontSize = if (isTablet) 18.sp else 16.sp

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearCheckinState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .padding(horizontal = contentPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Lucide.X,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(if (isTablet) 28.dp else 24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp))

            // Lottie Animation
            LottieSuccess(
                modifier = Modifier.size(lottieSize)
            )

            Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 24.dp))

            // Success Message
            Text(
                text = "Check-in Successful!",
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isLate) "You've checked in late, but your attendance has been recorded."
                else "Your attendance has been recorded successfully.",
                fontSize = bodyFontSize,
                color = PrimaryColor,
                textAlign = TextAlign.Center,
                lineHeight = if (isTablet) 26.sp else 22.sp,
                modifier = Modifier.padding(horizontal = if (isTablet) 32.dp else 16.dp)
            )

            Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 32.dp))

            // Session Details Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = PrimaryForegroundColor,
                        shape = RoundedCornerShape(if (isTablet) 20.dp else 16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MutedColor,
                        shape = RoundedCornerShape(if (isTablet) 20.dp else 16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = if (isTablet) 28.dp else 20.dp,
                        vertical = if (isTablet) 28.dp else 20.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(if (isTablet) 20.dp else 16.dp)
                ) {
                    // Session Title
                    Text(
                        text = currentSession?.title ?: "",
                        fontSize = if (isTablet) 22.sp else 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )

                    // Check-in Details
                    Column(
                        verticalArrangement = Arrangement.spacedBy(if (isTablet) 16.dp else 12.dp)
                    ) {
                        // Check-in Time
                        DetailRow(
                            icon = Lucide.Clock,
                            label = "Check-in Time",
                            value = checkinTime?.format(DateTimeFormatter.ofPattern("HH:mm, MMM dd"))
                                ?: "N/A",
                            iconColor = Color(0xFF4CAF50),
                            isTablet = isTablet
                        )

                        // Status
                        DetailRow(
                            icon = if (isLate) Lucide.TriangleAlert else Lucide.CircleCheck,
                            label = "Status",
                            value = if (isLate) "Late" else "On Time",
                            iconColor = if (isLate) Color(0xFFFF9800) else Color(0xFF4CAF50),
                            isTablet = isTablet
                        )

                        // Location (if recorded)
                        if (attendanceCheckinCreated.data?.data?.latitude != 0.0 && attendanceCheckinCreated.data?.data?.longitude != 0.0) {
                            DetailRow(
                                icon = Lucide.MapPin,
                                label = "Location",
                                value = "Recorded",
                                iconColor = MaterialTheme.colorScheme.primary,
                                isTablet = isTablet
                            )
                        }

                        // Notes (if provided)
                        if (!attendanceCheckinCreated.data?.data?.notes.isNullOrBlank()) {
                            DetailRow(
                                icon = Lucide.FileText,
                                label = "Notes",
                                value = attendanceCheckinCreated.data?.data?.notes ?: "",
                                iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                isTablet = isTablet
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 24.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Secondary Button - Done
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isTablet) 56.dp else 48.dp),
                    border = BorderStroke(1.dp, AccentColor),
                    shape = RoundedCornerShape(if (isTablet) 16.dp else 12.dp)
                ) {
                    Text(
                        text = "Done",
                        fontSize = if (isTablet) 18.sp else 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp))
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    isTablet: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(if (isTablet) 16.dp else 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(if (isTablet) 24.dp else 20.dp),
            tint = iconColor
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(if (isTablet) 4.dp else 2.dp)
        ) {
            Text(
                text = label,
                fontSize = if (isTablet) 16.sp else 14.sp,
                color = PrimaryColor,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = if (isTablet) 18.sp else 16.sp,
                color = PrimaryColor.copy(0.6f),
                lineHeight = if (isTablet) 24.sp else 20.sp
            )
        }
    }
}
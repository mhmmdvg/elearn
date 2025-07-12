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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.TriangleAlert
import com.composables.icons.lucide.X
import com.elearn.presentation.ui.components.LottieSuccess
import com.elearn.presentation.ui.theme.AccentColor
import com.elearn.presentation.ui.theme.MutedColor
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

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearCheckinState()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding(),
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lottie Animation
        LottieSuccess(
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Success Message
        Text(
            text = "Check-in Successful!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isLate) "You've checked in late, but your attendance has been recorded."
            else "Your attendance has been recorded successfully.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Session Details Card
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
                // Session Title
                Text(
                    text = currentSession?.title ?: "",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Check-in Details
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Check-in Time
                    DetailRow(
                        icon = Lucide.Clock,
                        label = "Check-in Time",
                        value = checkinTime?.format(DateTimeFormatter.ofPattern("HH:mm, MMM dd"))
                            ?: "N/A",
                        iconColor = Color(0xFF4CAF50)
                    )

                    // Status
                    DetailRow(
                        icon = if (isLate) Lucide.TriangleAlert else Lucide.CircleCheck,
                        label = "Status",
                        value = if (isLate) "Late" else "On Time",
                        iconColor = if (isLate) Color(0xFFFF9800) else Color(0xFF4CAF50)
                    )

                    // Location (if recorded)
                    if (attendanceCheckinCreated.data?.data?.latitude != 0.0 && attendanceCheckinCreated.data?.data?.longitude != 0.0) {
                        DetailRow(
                            icon = Lucide.MapPin,
                            label = "Location",
                            value = "Recorded",
                            iconColor = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Notes (if provided)
                    if (!attendanceCheckinCreated.data?.data?.notes.isNullOrBlank()) {
                        DetailRow(
                            icon = Lucide.FileText,
                            label = "Notes",
                            value = attendanceCheckinCreated.data?.data?.notes ?: "",
                            iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Primary Button - View Details
            Button(
                onClick = onViewDetails,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.Eye,
                        contentDescription = "View Details",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "View Attendance Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Secondary Button - Done
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                border = BorderStroke(1.dp, AccentColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Done",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = AccentColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = iconColor
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

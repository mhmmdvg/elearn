package com.elearn.presentation.ui.screens.attendancesession.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.Minus
import com.composables.icons.lucide.Target
import com.composables.icons.lucide.X
import com.elearn.domain.model.AttendanceStudent
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.utils.LocationUtils
import com.elearn.utils.TimeUtils
import java.time.format.DateTimeFormatter

@Composable
fun AttendanceStudentCard(
    modifier: Modifier = Modifier,
    student: AttendanceStudent
) {
    val context = LocalContext.current
    var locationAddress by remember { mutableStateOf<String?>(null) }
    var isLoadingLocation by remember { mutableStateOf(false) }
    var showLocationDetails by remember { mutableStateOf(false) }

    val statusColor = when (student.status) {
        "PRESENT" -> Color(0xFF4CAF50)
        "LATE" -> Color(0xFFFF9800)
        "ABSENT" -> Color(0xFFF44336)
        else -> Color(0xFF757575)
    }

    LaunchedEffect(student.latitude, student.longitude) {
        if (student.latitude != null && student.longitude != null) {
            isLoadingLocation = true
            locationAddress = LocationUtils.getAddressFromCoordinates(
                context = context,
                latitude = student.latitude,
                longitude = student.longitude
            )
            isLoadingLocation = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = PrimaryForegroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = MutedColor,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${student.student.firstName} ${student.student.lastName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = student.student.email,
                        fontSize = 14.sp,
                        color = MutedForegroundColor
                    )

                    if (student.checkedInAt != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Clock,
                                contentDescription = "Check-in time",
                                modifier = Modifier.size(14.dp),
                                tint = PrimaryColor.copy(alpha=0.7f)
                            )
                            TimeUtils.parseUtcToLocal(student.checkedInAt)?.let {
                                Text(
                                    text = it.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    fontSize = 12.sp,
                                    color = PrimaryColor.copy(alpha=0.7f)
                                )
                            }
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (student.status) {
                                "PRESENT" -> Lucide.Check
                                "LATE" -> Lucide.Clock
                                "ABSENT" -> Lucide.X
                                else -> Lucide.Minus
                            },
                            contentDescription = student.status,
                            modifier = Modifier.size(12.dp),
                            tint = statusColor
                        )
                        Text(
                            text = when (student.status) {
                                "PRESENT" -> "Present"
                                "LATE" -> "Late"
                                "ABSENT" -> "Absent"
                                else -> "Unknown"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = statusColor
                        )
                    }
                }
            }

            if (student.latitude != null && student.longitude != null && student.status != "ABSENT") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showLocationDetails = !showLocationDetails },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Lucide.MapPin,
                                contentDescription = "Location",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Check-in Location",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Icon(
                            imageVector = Lucide.ChevronDown,
                            contentDescription = if (showLocationDetails) "Hide details" else "Show details",
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(
                                    animateFloatAsState(
                                        targetValue = if (showLocationDetails) 180f else 0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        ),
                                        label = "chevron_rotation"
                                    ).value
                                ),
                            tint = MutedForegroundColor
                        )
                    }

                    AnimatedVisibility(
                        visible = showLocationDetails,
                        enter = slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            initialOffsetY = { -it / 3 }
                        ) + expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = LinearOutSlowInEasing
                            )
                        ),
                        exit = slideOutVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            targetOffsetY = { -it / 3 }
                        ) + shrinkVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeOut(
                            animationSpec = tween(
                                durationMillis = 200,
                                easing = FastOutLinearInEasing
                            )
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .background(
                                    color = MutedColor.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Address
                            if (isLoadingLocation) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Lucide.MapPin,
                                        contentDescription = "Loading",
                                        modifier = Modifier.size(12.dp),
                                        tint = PrimaryColor.copy(0.7f)
                                    )
                                    Text(
                                        text = "Loading address...",
                                        fontSize = 11.sp,
                                        color = PrimaryColor.copy(0.7f),
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            } else {
                                Text(
                                    text = locationAddress ?: LocationUtils.formatCoordinates(
                                        student.latitude,
                                        student.longitude
                                    ),
                                    fontSize = 11.sp,
                                    color = PrimaryColor.copy(0.7f)
                                )
                            }

                            if (student.accuracy != null) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Lucide.Target,
                                        contentDescription = "Accuracy",
                                        modifier = Modifier.size(12.dp),
                                        tint = PrimaryColor.copy(0.7f)
                                    )
                                    Text(
                                        text = "Accuracy: ${
                                            LocationUtils.getAccuracyDescription(
                                                student.accuracy
                                            )
                                        }",
                                        fontSize = 11.sp,
                                        color = PrimaryColor.copy(0.7f)
                                    )
                                }
                            }

                            Text(
                                text = LocationUtils.formatCoordinates(
                                    student.latitude,
                                    student.longitude
                                ),
                                fontSize = 10.sp,
                                color = PrimaryColor.copy(0.5f),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Animated summary text when collapsed
                    AnimatedVisibility(
                        visible = !showLocationDetails,
                        enter = fadeIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ),
                        exit = fadeOut(
                            animationSpec = tween(
                                durationMillis = 150,
                                easing = FastOutLinearInEasing
                            )
                        )
                    ) {
                        Text(
                            text = if (isLoadingLocation) {
                                "Loading location..."
                            } else {
                                locationAddress?.let { address ->
                                    // Show first part of address (street name or city)
                                    address.split(",").firstOrNull()?.trim() ?: "Location recorded"
                                } ?: "Location recorded"
                            },
                            fontSize = 11.sp,
                            color = MutedForegroundColor,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            if (!student.notes.isNullOrBlank()) {
                Text(
                    text = "Note: ${student.notes}",
                    fontSize = 11.sp,
                    color = MutedForegroundColor,
                    modifier = Modifier.padding(top = 8.dp),
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}
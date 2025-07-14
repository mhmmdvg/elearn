package com.elearn.presentation.ui.screens.details.course.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.UserCheck
import com.composables.icons.lucide.Users
import com.elearn.domain.model.AttendanceSessionsData
import com.elearn.presentation.ui.components.shimmerEffect
import com.elearn.presentation.ui.theme.AccentColor
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.utils.TimeUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AttendanceSessionCard(
    modifier: Modifier = Modifier,
    session: AttendanceSessionsData,
    userRole: String,
    onCardClick: () -> Unit,
    onCheckIn: (() -> Unit)? = null,
    isCheckedIn: Boolean = false
) {
    val startTime = TimeUtils.parseUtcToLocal(session.startTime) ?: LocalDateTime.now()
    val endTime = TimeUtils.parseUtcToLocal(session.endTime) ?: LocalDateTime.now()

    val currentTime = LocalDateTime.now()
    val isOngoing = currentTime.isAfter(startTime) && currentTime.isBefore(endTime)
    val isPast = currentTime.isAfter(endTime)

    val statusColor = when {
        isOngoing -> Color(0xFF4CAF50) // Green
        isPast -> Color(0xFF757575) // Gray
        else -> Color(0xFF2196F3) // Blue
    }

    val statusText = when {
        isOngoing -> "Ongoing"
        isPast -> "Ended"
        else -> "Upcoming"
    }

    // Responsive values based on screen configuration
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val cardPadding = when {
        screenWidth < 360.dp -> 12.dp
        screenWidth < 480.dp -> 16.dp
        else -> 20.dp
    }

    val cornerRadius = when {
        screenWidth < 360.dp -> 12.dp
        else -> 16.dp
    }

    val verticalSpacing = when {
        screenWidth < 360.dp -> 8.dp
        screenWidth < 480.dp -> 12.dp
        else -> 16.dp
    }

    val horizontalSpacing = when {
        screenWidth < 360.dp -> 8.dp
        screenWidth < 480.dp -> 12.dp
        else -> 16.dp
    }

    val iconSize = when {
        screenWidth < 360.dp -> 14.dp
        else -> 16.dp
    }

    val titleFontSize = when {
        screenWidth < 360.dp -> 14.sp
        screenWidth < 480.dp -> 16.sp
        else -> 18.sp
    }

    val bodyFontSize = when {
        screenWidth < 360.dp -> 12.sp
        screenWidth < 480.dp -> 14.sp
        else -> 16.sp
    }

    val smallFontSize = when {
        screenWidth < 360.dp -> 10.sp
        screenWidth < 480.dp -> 12.sp
        else -> 14.sp
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = PrimaryForegroundColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .border(
                width = 1.dp,
                color = MutedColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .clip(
                shape = RoundedCornerShape(cornerRadius)
            )
            .then(
                if (userRole == "teacher") Modifier.clickable { onCardClick() } else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(cardPadding),
            verticalArrangement = Arrangement.spacedBy(verticalSpacing)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = session.title,
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = if (screenWidth < 360.dp) 1 else 2,
                        overflow = TextOverflow.Ellipsis,
                        color = PrimaryColor,
                        lineHeight = titleFontSize * 1.2
                    )

                    if (session.description.isNotBlank()) {
                        Text(
                            text = session.description,
                            fontSize = bodyFontSize,
                            color = MutedForegroundColor,
                            maxLines = if (screenWidth < 360.dp) 1 else 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp),
                            lineHeight = bodyFontSize * 1.3,
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(cornerRadius * 0.75f),
                    color = statusColor.copy(alpha = 0.1f),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = smallFontSize,
                        fontWeight = FontWeight.Medium,
                        color = statusColor,
                        modifier = Modifier.padding(
                            horizontal = if (screenWidth < 360.dp) 6.dp else 8.dp,
                            vertical = 4.dp
                        )
                    )
                }
            }

            // Time Information
            if (screenWidth < 360.dp) {
                // Stack vertically on very small screens
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TimeInfoRow(
                        icon = Lucide.Clock,
                        text = "${startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${
                            endTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                        }",
                        iconSize = iconSize,
                        fontSize = smallFontSize
                    )
                    TimeInfoRow(
                        icon = Lucide.Calendar,
                        text = startTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        iconSize = iconSize,
                        fontSize = smallFontSize
                    )
                }
            } else {
                // Side by side on larger screens
                Row(
                    horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TimeInfoRow(
                        icon = Lucide.Clock,
                        text = "${startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${
                            endTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                        }",
                        iconSize = iconSize,
                        fontSize = smallFontSize,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    TimeInfoRow(
                        icon = Lucide.Calendar,
                        text = startTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        iconSize = iconSize,
                        fontSize = smallFontSize,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
            }

            // Features Row
            if (screenWidth < 360.dp) {
                // Stack vertically on very small screens
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FeaturesContent(
                        session = session,
                        userRole = userRole,
                        iconSize = iconSize,
                        fontSize = smallFontSize,
                        isVertical = true
                    )
                }
            } else {
                // Side by side on larger screens
                Row(
                    horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FeaturesContent(
                        session = session,
                        userRole = userRole,
                        iconSize = iconSize,
                        fontSize = smallFontSize,
                        isVertical = false
                    )
                }
            }

            // Action Button for Students
            if (userRole == "student" && isOngoing && onCheckIn != null) {
                Button(
                    onClick = onCheckIn,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCheckedIn) Color(0xFF4CAF50) else AccentColor
                    ),
                    enabled = !isCheckedIn,
                    contentPadding = PaddingValues(
                        horizontal = cardPadding,
                        vertical = if (screenWidth < 360.dp) 8.dp else 12.dp
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isCheckedIn) Lucide.Check else Lucide.UserCheck,
                            contentDescription = if (isCheckedIn) "Checked In" else "Check In",
                            modifier = Modifier.size(iconSize),
                            tint = if (isCheckedIn) MutedForegroundColor else PrimaryColor
                        )
                        Text(
                            text = if (isCheckedIn) "Checked In" else "Check In Now",
                            fontSize = bodyFontSize,
                            fontWeight = FontWeight.Medium,
                            color = if (isCheckedIn) MutedForegroundColor else PrimaryColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeInfoRow(
    icon: ImageVector,
    text: String,
    iconSize: Dp,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = PrimaryColor.copy(0.8f)
        )
        Text(
            text = text,
            fontSize = fontSize,
            color = PrimaryColor.copy(0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FeaturesContent(
    session: AttendanceSessionsData,
    userRole: String,
    iconSize: Dp,
    fontSize: TextUnit,
    isVertical: Boolean
) {
    val arrangement = if (isVertical) {
        Arrangement.spacedBy(4.dp)
    } else {
        Arrangement.spacedBy(12.dp)
    }

    val content: @Composable () -> Unit = {
        if (session.requireLocation) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Lucide.MapPin,
                    contentDescription = "Location Required",
                    modifier = Modifier.size(iconSize),
                    tint = MutedForegroundColor
                )
                Text(
                    text = "Location Required",
                    fontSize = fontSize,
                    color = MutedForegroundColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (userRole == "teacher") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Lucide.Users,
                    contentDescription = "Attendance",
                    modifier = Modifier.size(iconSize),
                    tint = PrimaryColor.copy(0.8f)
                )
                Text(
                    text = "${session.attendanceCount}/${session.studentCount} attended",
                    fontSize = fontSize,
                    color = PrimaryColor.copy(0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    if (isVertical) {
        Column(verticalArrangement = arrangement) {
            content()
        }
    } else {
        Row(
            horizontalArrangement = arrangement,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Composable
fun AttendanceSessionSkeleton(
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val cardPadding = when {
        screenWidth < 360.dp -> 12.dp
        screenWidth < 480.dp -> 16.dp
        else -> 20.dp
    }

    val cornerRadius = when {
        screenWidth < 360.dp -> 12.dp
        else -> 16.dp
    }

    val verticalSpacing = when {
        screenWidth < 360.dp -> 8.dp
        screenWidth < 480.dp -> 12.dp
        else -> 16.dp
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = PrimaryForegroundColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .border(
                width = 1.dp,
                color = MutedColor,
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        Column(
            modifier = Modifier.padding(cardPadding),
            verticalArrangement = Arrangement.spacedBy(verticalSpacing)
        ) {
            // Title skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (screenWidth < 360.dp) 0.8f else 0.7f)
                    .height(if (screenWidth < 360.dp) 18.dp else 20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )

            // Description skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (screenWidth < 360.dp) 0.95f else 0.9f)
                    .height(if (screenWidth < 360.dp) 14.dp else 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )

            // Time info skeleton
            if (screenWidth < 360.dp) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .shimmerEffect()
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .shimmerEffect()
                    )
                }
            }

            // Features skeleton
            Box(
                modifier = Modifier
                    .width(if (screenWidth < 360.dp) 100.dp else 120.dp)
                    .height(if (screenWidth < 360.dp) 14.dp else 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
fun EmptyAttendanceSessionsState(
    modifier: Modifier = Modifier,
    title: String = "No Attendance Sessions",
    description: String = "No attendance sessions have been created yet.",
    isError: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(50),
            color = MutedColor,
            modifier = Modifier.size(80.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isError) Lucide.CircleAlert else Lucide.UserCheck,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MutedForegroundColor
                )
            }
        }

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )

        Text(
            text = description,
            fontSize = 14.sp,
            color = MutedColor
        )
    }
}
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
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
    val configuration = LocalConfiguration.current
    var locationAddress by remember { mutableStateOf<String?>(null) }
    var isLoadingLocation by remember { mutableStateOf(false) }
    var showLocationDetails by remember { mutableStateOf(false) }

    // Responsive values based on screen size
    val screenWidth = configuration.screenWidthDp.dp
    val isCompact = screenWidth < 600.dp
    val isTablet = screenWidth >= 600.dp

    // Dynamic sizing
    val cardPadding = if (isCompact) 12.dp else 16.dp
    val cornerRadius = if (isCompact) 16.dp else 16.dp
    val nameTextSize = if (isCompact) 14.sp else 16.sp
    val emailTextSize = if (isCompact) 12.sp else 14.sp
    val statusTextSize = if (isCompact) 10.sp else 12.sp
    val timeTextSize = if (isCompact) 10.sp else 12.sp
    val locationTextSize = if (isCompact) 10.sp else 11.sp
    val locationDetailTextSize = if (isCompact) 9.sp else 10.sp
    val locationSummaryTextSize = if (isCompact) 9.sp else 10.sp
    val coordinatesTextSize = if (isCompact) 8.sp else 9.sp
    val noteTextSize = if (isCompact) 10.sp else 11.sp
    val iconSize = if (isCompact) 12.dp else 14.dp
    val statusIconSize = if (isCompact) 10.dp else 12.dp

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
                shape = RoundedCornerShape(cornerRadius)
            )
            .border(
                width = 1.dp,
                color = MutedColor,
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(cardPadding)
        ) {
            // Main student info - responsive layout
            if (isTablet) {
                // Tablet layout - more spacious
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StudentInfoSection(
                        student = student,
                        nameTextSize = nameTextSize,
                        emailTextSize = emailTextSize,
                        timeTextSize = timeTextSize,
                        iconSize = iconSize,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    StatusBadge(
                        status = student.status,
                        statusColor = statusColor,
                        statusTextSize = statusTextSize,
                        statusIconSize = statusIconSize
                    )
                }
            } else {
                // Phone layout - compact
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    StudentInfoSection(
                        student = student,
                        nameTextSize = nameTextSize,
                        emailTextSize = emailTextSize,
                        timeTextSize = timeTextSize,
                        iconSize = iconSize,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    StatusBadge(
                        status = student.status,
                        statusColor = statusColor,
                        statusTextSize = statusTextSize,
                        statusIconSize = statusIconSize
                    )
                }
            }

            // Location section - responsive
            if (student.latitude != null && student.longitude != null && student.status != "ABSENT") {
                LocationSection(
                    student = student,
                    locationAddress = locationAddress,
                    isLoadingLocation = isLoadingLocation,
                    showLocationDetails = showLocationDetails,
                    onToggleDetails = { showLocationDetails = !showLocationDetails },
                    locationTextSize = locationTextSize,
                    locationDetailTextSize = locationDetailTextSize,
                    locationSummaryTextSize = locationSummaryTextSize,
                    coordinatesTextSize = coordinatesTextSize,
                    iconSize = iconSize,
                    cornerRadius = cornerRadius - 4.dp,
                    isCompact = isCompact
                )
            }

            // Notes section - responsive
            if (!student.notes.isNullOrBlank()) {
                Text(
                    text = "Note: ${student.notes}",
                    fontSize = noteTextSize,
                    color = MutedForegroundColor,
                    modifier = Modifier.padding(top = if (isCompact) 6.dp else 8.dp),
                    fontStyle = FontStyle.Italic,
                    lineHeight = if (isCompact) 14.sp else 16.sp
                )
            }
        }
    }
}

@Composable
private fun StudentInfoSection(
    student: AttendanceStudent,
    nameTextSize: TextUnit,
    emailTextSize: TextUnit,
    timeTextSize: TextUnit,
    iconSize: Dp,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "${student.student.firstName} ${student.student.lastName}",
            fontSize = nameTextSize,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = student.student.email,
            fontSize = emailTextSize,
            color = MutedForegroundColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
                    modifier = Modifier.size(iconSize),
                    tint = PrimaryColor.copy(alpha = 0.7f)
                )
                TimeUtils.parseUtcToLocal(student.checkedInAt)?.let {
                    Text(
                        text = it.format(DateTimeFormatter.ofPattern("HH:mm")),
                        fontSize = timeTextSize,
                        color = PrimaryColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(
    status: String,
    statusColor: Color,
    statusTextSize: TextUnit,
    statusIconSize: Dp
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = statusColor.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (status) {
                    "PRESENT" -> Lucide.Check
                    "LATE" -> Lucide.Clock
                    "ABSENT" -> Lucide.X
                    else -> Lucide.Minus
                },
                contentDescription = status,
                modifier = Modifier.size(statusIconSize),
                tint = statusColor
            )
            Text(
                text = when (status) {
                    "PRESENT" -> "Present"
                    "LATE" -> "Late"
                    "ABSENT" -> "Absent"
                    else -> "Unknown"
                },
                fontSize = statusTextSize,
                fontWeight = FontWeight.Medium,
                color = statusColor
            )
        }
    }
}

@Composable
private fun LocationSection(
    student: AttendanceStudent,
    locationAddress: String?,
    isLoadingLocation: Boolean,
    showLocationDetails: Boolean,
    onToggleDetails: () -> Unit,
    locationTextSize: TextUnit,
    locationDetailTextSize: TextUnit,
    locationSummaryTextSize: TextUnit,
    coordinatesTextSize: TextUnit,
    iconSize: Dp,
    cornerRadius: Dp,
    isCompact: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isCompact) 8.dp else 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(cornerRadius))
                .clickable { onToggleDetails() }
                .padding(vertical = 4.dp),
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
                    modifier = Modifier.size(iconSize),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Check-in Location",
                    fontSize = locationTextSize,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Icon(
                imageVector = Lucide.ChevronDown,
                contentDescription = if (showLocationDetails) "Hide details" else "Show details",
                modifier = Modifier
                    .size(if (isCompact) 14.dp else 16.dp)
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
            LocationDetailsCard(
                student = student,
                locationAddress = locationAddress,
                isLoadingLocation = isLoadingLocation,
                locationDetailTextSize = locationDetailTextSize,
                coordinatesTextSize = coordinatesTextSize,
                iconSize = iconSize,
                cornerRadius = cornerRadius,
                isCompact = isCompact
            )
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
                fontSize = locationSummaryTextSize,
                color = MutedForegroundColor,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LocationDetailsCard(
    student: AttendanceStudent,
    locationAddress: String?,
    isLoadingLocation: Boolean,
    locationDetailTextSize: TextUnit,
    coordinatesTextSize: TextUnit,
    iconSize: Dp,
    cornerRadius: Dp,
    isCompact: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isCompact) 6.dp else 8.dp)
            .background(
                color = MutedColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(if (isCompact) 8.dp else 12.dp),
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 4.dp else 6.dp)
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
                    modifier = Modifier.size(iconSize),
                    tint = PrimaryColor.copy(0.7f)
                )
                Text(
                    text = "Loading address...",
                    fontSize = locationDetailTextSize,
                    color = PrimaryColor.copy(0.7f),
                    fontStyle = FontStyle.Italic
                )
            }
        } else {
            Text(
                text = locationAddress ?: LocationUtils.formatCoordinates(
                    student.latitude!!,
                    student.longitude!!
                ),
                fontSize = locationDetailTextSize,
                color = PrimaryColor.copy(0.7f),
                lineHeight = if (isCompact) 14.sp else 16.sp
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
                    modifier = Modifier.size(iconSize),
                    tint = PrimaryColor.copy(0.7f)
                )
                Text(
                    text = "Accuracy: ${
                        LocationUtils.getAccuracyDescription(student.accuracy)
                    }",
                    fontSize = locationDetailTextSize,
                    color = PrimaryColor.copy(0.7f)
                )
            }
        }

        Text(
            text = LocationUtils.formatCoordinates(
                student.latitude!!,
                student.longitude!!
            ),
            fontSize = coordinatesTextSize,
            color = PrimaryColor.copy(0.5f),
            fontFamily = FontFamily.Monospace
        )
    }
}
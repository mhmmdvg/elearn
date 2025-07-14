package com.elearn.presentation.ui.screens.attendancesession.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.elearn.domain.model.AttendanceSessionDetailData
import com.elearn.domain.model.AttendanceSessionSummary
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.utils.TimeUtils
import java.time.format.DateTimeFormatter

@Composable
fun AttendanceSessionInfoCard(
    session: AttendanceSessionDetailData?,
    summary: AttendanceSessionSummary?
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isCompact = screenWidth < 600.dp

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
            modifier = Modifier.padding(
                horizontal = if (isCompact) 12.dp else 16.dp,
                vertical = if (isCompact) 12.dp else 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 16.dp)
        ) {
            // Session Title and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = session?.title ?: "",
                        fontSize = if (isCompact) 16.sp else 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = session?.className ?: "",
                        fontSize = if (isCompact) 12.sp else 14.sp,
                        color = MutedForegroundColor,
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (session?.isActive == true) Color(0xFF4CAF50).copy(alpha = 0.1f) else Color(
                        0xFF757575
                    ).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (session?.isActive == true) "Active" else "Ended",
                        fontSize = if (isCompact) 10.sp else 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (session?.isActive == true) Color(0xFF4CAF50) else Color(
                            0xFF757575
                        ),
                        modifier = Modifier.padding(
                            horizontal = if (isCompact) 6.dp else 8.dp,
                            vertical = if (isCompact) 3.dp else 4.dp
                        )
                    )
                }
            }

            // Time Info - Make it responsive with wrapping
            if (isCompact) {
                // Stack vertically on small screens
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.Clock,
                            contentDescription = "Time",
                            modifier = Modifier.size(14.dp),
                            tint = PrimaryColor.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "${
                                TimeUtils.parseUtcToLocal(session?.startTime ?: "")
                                    ?.format(DateTimeFormatter.ofPattern("HH:mm"))
                            } - ${
                                TimeUtils.parseUtcToLocal(session?.endTime ?: "")
                                    ?.format(DateTimeFormatter.ofPattern("HH:mm"))
                            }",
                            fontSize = 12.sp,
                            color = PrimaryColor.copy(alpha = 0.8f)
                        )
                    }

                    if (session?.requireLocation == true) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Lucide.MapPin,
                                contentDescription = "Location Required",
                                modifier = Modifier.size(14.dp),
                                tint = MutedForegroundColor
                            )
                            Text(
                                text = "Location Required",
                                fontSize = 12.sp,
                                color = MutedForegroundColor
                            )
                        }
                    }
                }
            } else {
                // Side by side on larger screens
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
                            tint = PrimaryColor.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "${
                                TimeUtils.parseUtcToLocal(session?.startTime ?: "")
                                    ?.format(DateTimeFormatter.ofPattern("HH:mm"))
                            } - ${
                                TimeUtils.parseUtcToLocal(session?.endTime ?: "")
                                    ?.format(DateTimeFormatter.ofPattern("HH:mm"))
                            }",
                            fontSize = 14.sp,
                            color = PrimaryColor.copy(alpha = 0.8f)
                        )
                    }

                    if (session?.requireLocation == true) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Lucide.MapPin,
                                contentDescription = "Location Required",
                                modifier = Modifier.size(16.dp),
                                tint = MutedForegroundColor
                            )
                            Text(
                                text = "Location Required",
                                fontSize = 14.sp,
                                color = MutedForegroundColor
                            )
                        }
                    }
                }
            }

            // Attendance Summary - Responsive grid
            if (isCompact && screenWidth < 360.dp) {
                // Very small screens: 2x2 grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AttendanceSummaryItem(
                            title = "Total",
                            count = summary?.totalStudents ?: 0,
                            color = MaterialTheme.colorScheme.primary,
                            isCompact = true
                        )

                        AttendanceSummaryItem(
                            title = "Present",
                            count = summary?.presentCount ?: 0,
                            color = Color(0xFF4CAF50),
                            isCompact = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AttendanceSummaryItem(
                            title = "Late",
                            count = summary?.lateCount ?: 0,
                            color = Color(0xFFFF9800),
                            isCompact = true
                        )

                        AttendanceSummaryItem(
                            title = "Absent",
                            count = summary?.absentCount ?: 0,
                            color = Color(0xFFF44336),
                            isCompact = true
                        )
                    }
                }
            } else {
                // Regular screens: single row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AttendanceSummaryItem(
                        title = "Total",
                        count = summary?.totalStudents ?: 0,
                        color = MaterialTheme.colorScheme.primary,
                        isCompact = isCompact
                    )

                    AttendanceSummaryItem(
                        title = "Present",
                        count = summary?.presentCount ?: 0,
                        color = Color(0xFF4CAF50),
                        isCompact = isCompact
                    )

                    AttendanceSummaryItem(
                        title = "Late",
                        count = summary?.lateCount ?: 0,
                        color = Color(0xFFFF9800),
                        isCompact = isCompact
                    )

                    AttendanceSummaryItem(
                        title = "Absent",
                        count = summary?.absentCount ?: 0,
                        color = Color(0xFFF44336),
                        isCompact = isCompact
                    )
                }
            }

            // Attendance Rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Attendance Rate",
                    fontSize = if (isCompact) 12.sp else 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "${summary?.attendanceRate ?: 0}%",
                    fontSize = if (isCompact) 14.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        (summary?.attendanceRate ?: 0) >= 80 -> Color(0xFF4CAF50)
                        (summary?.attendanceRate ?: 0) >= 60 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                )
            }
        }
    }
}

@Composable
private fun AttendanceSummaryItem(
    title: String,
    count: Int,
    color: Color,
    isCompact: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            fontSize = if (isCompact) 18.sp else 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            fontSize = if (isCompact) 10.sp else 12.sp,
            color = MutedForegroundColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
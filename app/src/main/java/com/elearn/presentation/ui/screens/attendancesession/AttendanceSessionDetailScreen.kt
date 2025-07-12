package com.elearn.presentation.ui.screens.attendancesession

import ActionBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.ChevronUp
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.Minus
import com.composables.icons.lucide.Target
import com.composables.icons.lucide.Users
import com.composables.icons.lucide.X
import com.elearn.domain.model.AttendanceSessionDetailData
import com.elearn.domain.model.AttendanceSessionSummary
import com.elearn.domain.model.AttendanceStudent
import com.elearn.presentation.ui.components.shimmerEffect
import com.elearn.presentation.ui.model.TabList
import com.elearn.presentation.ui.screens.home.components.ChipTabs
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.utils.LocationUtils
import com.elearn.utils.Resource
import com.elearn.utils.TimeUtils
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceSessionDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    classId: String,
    sessionId: String,
    attendanceDetailViewModel: AttendanceSessionDetailViewModel = hiltViewModel()
) {
    val attendanceDetailState by attendanceDetailViewModel.attendanceDetails.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(0) } // 0: All, 1: Present, 2: Late, 3: Absent

    LaunchedEffect(classId, sessionId) {
        attendanceDetailViewModel.fetchAttendanceSessionDetail(classId, sessionId)
    }

    LaunchedEffect(attendanceDetailState) {
        if (attendanceDetailState !is Resource.Loading) {
            isRefreshing = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ActionBar(
            title = attendanceDetailState.data?.data?.session?.title ?: "Attendance Detail",
            onBackClick = { navController.popBackStack() }
        )

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                attendanceDetailViewModel.fetchAttendanceSessionDetail(
                    classId,
                    sessionId,
                    isRefreshing
                )
            }
        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (attendanceDetailState) {
                    is Resource.Loading -> {
                        item { AttendanceDetailSkeleton() }
                        items(5) { AttendanceStudentSkeleton() }
                    }

                    is Resource.Success -> {
                        val data = attendanceDetailState.data?.data

                        // Session Info Card
                        item {
                            AttendanceSessionInfoCard(
                                session = data?.session,
                                summary = data?.summary
                            )
                        }

                        // Filter Tabs
                        item {
                            AttendanceFilterTabs(
                                selectedFilter = selectedFilter,
                                summary = data?.summary,
                                onFilterChange = { selectedFilter = it }
                            )
                        }

                        // Students List
                        val filteredStudents = when (selectedFilter) {
                            1 -> data?.checkedInStudents?.filter { it.status == "PRESENT" }
                                ?: emptyList()

                            2 -> data?.checkedInStudents?.filter { it.status == "LATE" }
                                ?: emptyList()

                            3 -> data?.notChekedInStudents ?: emptyList()
                            else -> (data?.checkedInStudents
                                ?: emptyList()) + (data?.notChekedInStudents ?: emptyList())
                        }

                        if (filteredStudents.isEmpty()) {
                            item {
                                EmptyAttendanceState(
                                    filter = selectedFilter
                                )
                            }
                        } else {
                            items(
                                items = filteredStudents,
                                key = { it.studentId }
                            ) { student ->
                                AttendanceStudentCard(
                                    student = student
                                )
                            }
                        }
                    }

                    is Resource.Error -> {
                        item {
                            ErrorState(
                                message = attendanceDetailState.message
                                    ?: "Failed to load attendance details"
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun AttendanceSessionInfoCard(
    session: AttendanceSessionDetailData?,
    summary: AttendanceSessionSummary?
) {
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = session?.className ?: "",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (session?.isActive == true) Color(0xFF4CAF50).copy(alpha = 0.1f) else Color(
                        0xFF757575
                    ).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (session?.isActive == true) "Active" else "Ended",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (session?.isActive == true) Color(0xFF4CAF50) else Color(
                            0xFF757575
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Time Info
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
                        text = "${
                            TimeUtils.parseUtcToLocal(session?.startTime ?: "")
                                ?.format(DateTimeFormatter.ofPattern("HH:mm"))
                        } - ${
                            TimeUtils.parseUtcToLocal(session?.endTime ?: "")
                                ?.format(DateTimeFormatter.ofPattern("HH:mm"))
                        }",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Location Required",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Attendance Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AttendanceSummaryItem(
                    title = "Total",
                    count = summary?.totalStudents ?: 0,
                    color = MaterialTheme.colorScheme.primary
                )

                AttendanceSummaryItem(
                    title = "Present",
                    count = summary?.presentCount ?: 0,
                    color = Color(0xFF4CAF50)
                )

                AttendanceSummaryItem(
                    title = "Late",
                    count = summary?.lateCount ?: 0,
                    color = Color(0xFFFF9800)
                )

                AttendanceSummaryItem(
                    title = "Absent",
                    count = summary?.absentCount ?: 0,
                    color = Color(0xFFF44336)
                )
            }

            // Attendance Rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Attendance Rate",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "${summary?.attendanceRate ?: 0}%",
                    fontSize = 16.sp,
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
fun AttendanceSummaryItem(
    title: String,
    count: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AttendanceFilterTabs(
    selectedFilter: Int,
    summary: AttendanceSessionSummary?,
    onFilterChange: (Int) -> Unit
) {
    val tabs = listOf(
        TabList(title = "All (${summary?.totalStudents ?: 0})"),
        TabList(title = "Present (${summary?.presentCount ?: 0})"),
        TabList(title = "Late (${summary?.lateCount ?: 0})"),
        TabList(title = "Absent (${summary?.absentCount ?: 0})")
    )

    ChipTabs(
        tabs = tabs,
        selectedTabIndex = selectedFilter,
        onTabSelected = onFilterChange
    )
}

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
                    .fillMaxWidth()
                    .padding(16.dp),
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TimeUtils.parseUtcToLocal(student.checkedInAt)?.let {
                                Text(
                                    text = it.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            imageVector = if (showLocationDetails) Lucide.ChevronUp else Lucide.ChevronDown,
                            contentDescription = if (showLocationDetails) "Hide details" else "Show details",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (showLocationDetails) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
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
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Loading address...",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Accuracy: ${
                                            LocationUtils.getAccuracyDescription(
                                                student.accuracy
                                            )
                                        }",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = LocationUtils.formatCoordinates(
                                    student.latitude,
                                    student.longitude
                                ),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    } else {
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            if (!student.notes.isNullOrBlank()) {
                Text(
                    text = "Note: ${student.notes}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun EmptyAttendanceState(
    filter: Int
) {
    val message = when (filter) {
        1 -> "No students marked as present"
        2 -> "No students marked as late"
        3 -> "No absent students"
        else -> "No students found"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Lucide.Users,
            contentDescription = "No students",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )

        Text(
            text = message,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun AttendanceDetailSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(
                shape = RoundedCornerShape(16.dp)
            )
            .shimmerEffect()
    )
}

@Composable
fun AttendanceStudentSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(
                shape = RoundedCornerShape(12.dp)
            )
            .shimmerEffect()
    )
}

@Composable
fun ErrorState(
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Lucide.CircleAlert,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = Color(0xFFF44336)
        )

        Text(
            text = message,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
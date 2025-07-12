package com.elearn.presentation.ui.screens.attendancesession

import ActionBar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.draw.rotate
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
import com.elearn.presentation.ui.screens.attendancesession.components.AttendanceDetailSkeleton
import com.elearn.presentation.ui.screens.attendancesession.components.AttendanceFilterTabs
import com.elearn.presentation.ui.screens.attendancesession.components.AttendanceSessionInfoCard
import com.elearn.presentation.ui.screens.attendancesession.components.AttendanceStudentCard
import com.elearn.presentation.ui.screens.attendancesession.components.AttendanceStudentSkeleton
import com.elearn.presentation.ui.screens.attendancesession.components.EmptyAttendanceState
import com.elearn.presentation.ui.screens.attendancesession.components.ErrorState
import com.elearn.presentation.ui.screens.home.components.ChipTabs
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.utils.LocationPermissionHandler
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
    var showLocationPermission by remember { mutableStateOf(false) }
    var locationPermissionGranted by remember { mutableStateOf(false) }
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

    LaunchedEffect(attendanceDetailState.data?.data?.session?.requireLocation) {
        if (attendanceDetailState.data?.data?.session?.requireLocation == true) {
            showLocationPermission = true
        }
    }

    if (showLocationPermission) {
        LocationPermissionHandler(
            onPermissionGranted = {
                locationPermissionGranted = true
                showLocationPermission = false
            },
            onPermissionDenied = {
                showLocationPermission = false
            }
        )
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

                            3 -> data?.notCheckedInStudents ?: emptyList()
                            else -> (data?.checkedInStudents
                                ?: emptyList()) + (data?.notCheckedInStudents ?: emptyList())
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
package com.elearn.presentation.ui.screens.attendancesession.components

import androidx.compose.runtime.Composable
import com.elearn.domain.model.AttendanceSessionSummary
import com.elearn.presentation.ui.model.TabList
import com.elearn.presentation.ui.screens.home.components.ChipTabs

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
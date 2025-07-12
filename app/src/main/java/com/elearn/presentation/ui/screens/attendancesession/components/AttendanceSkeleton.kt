package com.elearn.presentation.ui.screens.attendancesession.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.elearn.presentation.ui.components.shimmerEffect

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
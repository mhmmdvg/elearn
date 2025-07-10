package com.elearn.presentation.ui.screens.details.course.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.UserCheck
import com.elearn.presentation.ui.theme.AccentColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor

@Composable
fun StudentFAB(
    modifier: Modifier = Modifier,
    onCheckIn: () -> Unit,
    isCheckedIn: Boolean = false
) {
    FloatingActionButton(
        onClick = onCheckIn,
        modifier = modifier,
        containerColor = if (isCheckedIn) Color(0xFF4CAF50) else AccentColor,
        shape = CircleShape
    ) {
        Icon(
            imageVector = Lucide.UserCheck,
            contentDescription = if (isCheckedIn) "Checked In" else "Check In",
            tint = PrimaryForegroundColor
        )
    }
}
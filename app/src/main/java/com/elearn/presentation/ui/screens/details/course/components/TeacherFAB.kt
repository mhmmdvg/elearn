package com.elearn.presentation.ui.screens.details.course.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.UserCheck
import com.elearn.presentation.ui.theme.AccentColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor

data class FabOption(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)

@Composable
private fun MultiOptionFAB(
    modifier: Modifier = Modifier,
    options: List<FabOption>,
    containerColor: Color = PrimaryColor,
    contentColor: Color = PrimaryForegroundColor
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = tween(300),
        label = "fab_rotation"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Options (shown when expanded)
        options.reversed().forEach { option ->
            androidx.compose.animation.AnimatedVisibility(
                visible = isExpanded,
                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(),
                exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.scaleOut()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Label
                    Surface(
                        modifier = Modifier
                            .clickable {
                                option.onClick()
                                isExpanded = false
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryForegroundColor,
                        shadowElevation = 3.dp
                    ) {
                        Text(
                            text = option.label,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Mini FAB
                    FloatingActionButton(
                        onClick = {
                            option.onClick()
                            isExpanded = false
                        },
                        modifier = Modifier.size(48.dp),
                        containerColor = containerColor,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = option.label,
                            tint = contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Main FAB
        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            containerColor = containerColor,
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.Close else Lucide.Plus,
                contentDescription = if (isExpanded) "Close" else "Add",
                tint = contentColor,
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
fun TeacherFAB(
    modifier: Modifier = Modifier,
    onCreateMaterial: () -> Unit,
    onCreateAttendance: () -> Unit
) {
    val options = listOf(
        FabOption(
            icon = Lucide.FileText,
            label = "Create Material",
            onClick = onCreateMaterial
        ),
        FabOption(
            icon = Lucide.UserCheck,
            label = "Create Attendance",
            onClick = onCreateAttendance
        )
    )

    MultiOptionFAB(
        modifier = modifier,
        options = options,
        containerColor = AccentColor,
    )
}
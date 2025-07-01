package com.elearn.presentation.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.School
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.TriangleAlert
import com.elearn.presentation.ui.screens.auth.AuthViewModel
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassCard(
    className: String,
    classDescription: String? = null,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {

    val userInfoState by authViewModel.userInfoState.collectAsState()
    val density = LocalDensity.current
    var offsetX by remember { mutableFloatStateOf(0f) }
    val deleteThreshold = with(density) { -120.dp.toPx() }

    // Bottom sheet state
    var showDeleteBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    val isTeacher = userInfoState.data?.data?.role?.name == "teacher"

    val draggableState = rememberDraggableState { delta ->
        if(!isTeacher) return@rememberDraggableState
        val newOffset = offsetX + delta
        offsetX = when {
            newOffset > 0f -> 0f
            newOffset < deleteThreshold -> deleteThreshold
            else -> newOffset
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Background delete area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Red,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.size(40.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color.Transparent
                        )
                        if (!classDescription.isNullOrBlank()) {
                            Text(
                                text = "",
                                fontSize = 14.sp,
                                color = Color.Transparent
                            )
                        }
                    }

                    // Delete button - now shows bottom sheet
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            showDeleteBottomSheet = true
                        }
                    ) {
                        Icon(
                            imageVector = Lucide.Trash2,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Delete",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        // Main card content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .background(
                    color = PrimaryForegroundColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(16.dp),
                    color = MutedColor
                )
                .clip(
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(
                    onClick = {
                        if (offsetX == 0f) {
                            onClick()
                        } else {
                            offsetX = 0f // Reset position if swiped
                        }
                    },
                    indication = ripple(
                        color = PrimaryColor
                    ),
                    interactionSource = remember { MutableInteractionSource() }
                )
                .then(
                    if (isTeacher) Modifier.draggable(
                        state = draggableState,
                        orientation = Orientation.Horizontal,
                        onDragStopped = { _ ->
                            // Snap to delete threshold or reset to 0
                            offsetX = if (offsetX <= deleteThreshold * 0.5f) {
                                deleteThreshold
                            } else {
                                0f
                            }
                        }
                    ) else Modifier
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Class icon
                Icon(
                    imageVector = Lucide.School,
                    contentDescription = "Class",
                    tint = PrimaryColor,
                    modifier = Modifier.size(40.dp)
                )

                // Class info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = className,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!classDescription.isNullOrBlank()) {
                        Text(
                            text = classDescription,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation bottom sheet
    if (showDeleteBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showDeleteBottomSheet = false },
            sheetState = bottomSheetState
        ) {
            DeleteConfirmationContent(
                className = className,
                onConfirmDelete = {
                    onDelete()
                    showDeleteBottomSheet = false
                },
                onCancel = {
                    showDeleteBottomSheet = false
                }
            )
        }
    }
}

@Composable
private fun DeleteConfirmationContent(
    className: String,
    onConfirmDelete: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .background(
                color = PrimaryForegroundColor
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Warning Icon
        Surface(
            shape = CircleShape,
            color = Color.Red.copy(alpha = 0.1f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Lucide.TriangleAlert,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Red
                )
            }
        }

        // Title and Description
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Delete Class",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Are you sure you want to delete \"$className\"?",
                fontSize = 16.sp,
                color = PrimaryColor,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Text(
                text = "This action cannot be undone.",
                fontSize = 14.sp,
                color = Color.Red,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Delete Button
            Button(
                onClick = onConfirmDelete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Trash2,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Yes, Delete",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Cancel Button
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
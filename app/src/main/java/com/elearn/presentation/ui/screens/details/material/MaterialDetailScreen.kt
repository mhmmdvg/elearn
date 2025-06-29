package com.elearn.presentation.ui.screens.details.material

import ActionBar
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.composables.icons.lucide.Download
import com.composables.icons.lucide.File
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Image
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pencil
import com.composables.icons.lucide.Trash
import com.elearn.domain.model.HTTPResponse
import com.elearn.domain.model.MaterialData
import com.elearn.presentation.ui.components.CacheImage
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.utils.Resource
import com.elearn.utils.formatDate

@Composable
fun MaterialDetailScreen(
    modifier: Modifier = Modifier,
    materialId: String,
    navController: NavController,
    viewModel: MaterialDetailViewModel = hiltViewModel()
) {
    /* State */
    val scrollState = rememberScrollState()
    val materialDetailState by viewModel.materialDetailState.collectAsState()

    LaunchedEffect(materialId) {
        viewModel.fetchMaterialDetail(materialId)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionBar(
            title = "Material Detail",
            onBackClick = {
                navController.popBackStack()
            }
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .verticalScroll(scrollState)
        ) {
            when (materialDetailState) {
                is Resource.Loading -> {
                    MaterialDetailSkeleton()
                }
                is Resource.Success -> {
                    MaterialDetailContent(materialDetailState = materialDetailState)
                }
                is Resource.Error -> {
                    // Error state - you can customize this
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = materialDetailState.message ?: "Unknown error occurred",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MaterialDetailContent(
    materialDetailState: Resource<HTTPResponse<MaterialData>>
) {
    val isImage = materialDetailState.data?.data?.fileType == "IMAGE"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PrimaryForegroundColor,
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(18.dp),
                color = MutedColor
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with teacher info and file type badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MutedColor,
                        modifier = Modifier.size(48.dp)
                    ) {
                        CacheImage(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            imageUrl = materialDetailState.data?.data?.teacher?.imageUrl ?: "https://github.com/shadcn.png",
                            description = "Teacher Avatar",
                        )
                    }

                    Column {
                        Text(
                            text = "${materialDetailState.data?.data?.teacher?.firstName} ${materialDetailState.data?.data?.teacher?.lastName}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = materialDetailState.data?.data?.createdAt?.formatDate() ?: "",
                            fontSize = 12.sp,
                            color = MutedForegroundColor
                        )
                    }
                }

                FileTypeBadge(isImage = isImage)
            }

            // Material title
            Text(
                text = materialDetailState.data?.data?.name ?: "Unknown Material",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // File preview section
            MaterialFilePreview(
                isImage = isImage,
                fileUrl = materialDetailState.data?.data?.fileUrl ?: "",
                fileName = materialDetailState.data?.data?.fileName ?: "",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Description section
            materialDetailState.data?.data?.description?.let {
                if (it.isNotEmpty()) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Description",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = it ?: "",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = MutedForegroundColor,
                            style = TextStyle(
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both
                                )
                            )
                        )
                    }
                }
            }

            // File info section
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = MutedColor.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isImage) Lucide.Image else Lucide.FileText,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = if (isImage) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                        Column {
                            Text(
                                text = if (isImage) "IMAGE" else "PDF",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Tap to download",
                                fontSize = 12.sp,
                                color = MutedForegroundColor
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Blue.copy(alpha = 0.1f),
                        modifier = Modifier.clickable { /* TODO: Handle download */ }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Download,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.Blue
                            )
                            Text(
                                text = "Download",
                                fontSize = 12.sp,
                                color = Color.Blue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(bottomStart = 18.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true),
                            onClick = { /* TODO: Handle edit */ }
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            6.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                    ) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Lucide.Pencil,
                            contentDescription = "edit"
                        )
                        Text(
                            text = "Edit",
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(bottomEnd = 18.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true),
                            onClick = { /* TODO: Handle delete */ }
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            6.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                    ) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Lucide.Trash,
                            contentDescription = "delete",
                            tint = Color.Red
                        )
                        Text(
                            text = "Delete",
                            color = Color.Red,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MaterialDetailSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PrimaryForegroundColor,
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(18.dp),
                color = MutedColor
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header skeleton
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar skeleton
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                            .shimmerEffect()
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Teacher name skeleton
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                                .shimmerEffect()
                        )
                        // Date skeleton
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                                .shimmerEffect()
                        )
                    }
                }

                // File type badge skeleton
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                        .shimmerEffect()
                )
            }

            // Title skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(24.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                    .shimmerEffect()
            )

            // File preview skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                    .shimmerEffect()
            )

            // Description section skeleton
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Description title skeleton
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                        .shimmerEffect()
                )
                // Description text skeleton
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                            .shimmerEffect()
                    )
                }
            }

            // File info section skeleton
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = MutedColor.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // File icon skeleton
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                                .shimmerEffect()
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            // File type skeleton
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                                    .shimmerEffect()
                            )
                            // Tap to download skeleton
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                                    .shimmerEffect()
                            )
                        }
                    }

                    // Download button skeleton
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                            .shimmerEffect()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons skeleton
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(bottomStart = 18.dp))
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                            .shimmerEffect()
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(bottomEnd = 18.dp))
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

// Extension function for shimmer effect
@Composable
fun Modifier.shimmerEffect(): Modifier {
    return this // You can implement actual shimmer animation here if needed
}

@Composable
private fun FileTypeBadge(isImage: Boolean) {
    val (backgroundColor, textColor, text) = if (isImage) {
        Triple(
            Color(0xFF10B981).copy(alpha = 0.1f),
            Color(0xFF10B981),
            "IMAGE"
        )
    } else {
        Triple(
            Color(0xFFEF4444).copy(alpha = 0.1f),
            Color(0xFFEF4444),
            "PDF"
        )
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun MaterialFilePreview(
    isImage: Boolean,
    fileUrl: String,
    fileName: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isImage) 240.dp else 120.dp),
        shape = RoundedCornerShape(12.dp),
        color = MutedColor.copy(alpha = 0.1f)
    ) {
        if (isImage) {
            CacheImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                imageUrl = fileUrl,
                description = fileName,
                contentScale = ContentScale.Crop
            )
        } else {
            // Non-image file preview
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.1f),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Lucide.FileText,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFFEF4444)
                            )
                        }
                    }
                    Text(
                        text = "PDF Document",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MutedForegroundColor
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MaterialDetailSkeletonPreview() {
    MaterialDetailSkeleton()
}
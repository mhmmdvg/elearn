package com.elearn.presentation.ui.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.BookOpen
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.elearn.domain.model.MaterialData
import com.elearn.presentation.ui.components.CacheImage
import com.elearn.presentation.ui.screens.details.course.components.FileType
import com.elearn.presentation.ui.screens.details.course.components.getFileIcon
import com.elearn.presentation.ui.screens.details.course.components.getFileIconColor
import com.elearn.presentation.ui.screens.details.course.components.getFileType
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.utils.DateFormatter.formatDate
import com.elearn.utils.formatDate

@Composable
fun NewsCard(
    material: MaterialData,
    className: String,
    onClick: () -> Unit
) {
    val fileType = getFileType(material.fileUrl)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PrimaryForegroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(16.dp),
                color = MutedColor
            )
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with teacher info and update indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MutedColor,
                        modifier = Modifier.size(44.dp)
                    ) {
                        CacheImage(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            imageUrl = material.teacher.imageUrl ?: "https://github.com/shadcn.png",
                            description = "Teacher Avatar",
                        )
                    }

                    Column {
                        Text(
                            text = "${material.teacher.firstName} ${material.teacher.lastName}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = className,
                            fontSize = 12.sp,
                            color = MutedForegroundColor
                        )
                    }
                }
            }

            // Material preview section
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Material preview
                MaterialPreviewThumbnail(
                    fileUrl = material.fileUrl,
                    fileName = material.name,
                    fileType = fileType
                )

                // Material content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = material.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (material.description.isNotEmpty()) {
                        Text(
                            text = material.description,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = MutedForegroundColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both
                                )
                            )
                        )
                    }

                    // File info and timestamp
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = getFileIcon(material.fileUrl),
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = getFileIconColor(material.fileUrl)
                            )
                            Text(
                                text = when (fileType) {
                                    FileType.PDF -> "PDF"
                                    FileType.IMAGE -> "Image"
                                    FileType.DOCUMENT -> "Doc"
                                    FileType.UNKNOWN -> "File"
                                },
                                fontSize = 10.sp,
                                color = MutedForegroundColor,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Text(
                            text = "•",
                            fontSize = 10.sp,
                            color = MutedForegroundColor
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Clock,
                                contentDescription = null,
                                modifier = Modifier.size(10.dp),
                                tint = MutedForegroundColor
                            )
                            Text(
                                text = material.createdAt.formatDate(),
                                fontSize = 10.sp,
                                color = MutedForegroundColor
                            )
                        }
                    }
                }
            }

            // Action button
            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MutedColor)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Lucide.BookOpen,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = PrimaryColor
                    )
                    Text(
                        text = "View Material",
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = PrimaryColor
                    )
                }
            }
        }
    }
}

@Composable
private fun MaterialPreviewThumbnail(
    fileUrl: String,
    fileName: String,
    fileType: FileType
) {
    Surface(
        modifier = Modifier
            .width(80.dp)
            .height(80.dp),
        shape = RoundedCornerShape(8.dp),
        color = when (fileType) {
            FileType.IMAGE -> Color.Transparent
            else -> MutedColor.copy(alpha = 0.1f)
        }
    ) {
        when (fileType) {
            FileType.IMAGE -> {
                CacheImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    imageUrl = fileUrl,
                    description = fileName,
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = getFileIcon(fileUrl),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = getFileIconColor(fileUrl)
                        )
                        Text(
                            text = when (fileType) {
                                FileType.PDF -> "PDF"
                                FileType.DOCUMENT -> "DOC"
                                FileType.UNKNOWN -> "FILE"
                                else -> ""
                            },
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium,
                            color = getFileIconColor(fileUrl)
                        )
                    }
                }
            }
        }
    }
}
package com.elearn.presentation.ui.screens.details.course.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Download
import com.composables.icons.lucide.File
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Image
import com.composables.icons.lucide.Lucide
import com.elearn.domain.model.MaterialData
import com.elearn.presentation.ui.components.CacheImage
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.utils.formatDate

@Composable
fun EnhancedMaterialCard(
    material: MaterialData,
    onClick: () -> Unit
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
            .clip(
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        imageUrl = material.teacher.imageUrl
                            ?: "https://github.com/shadcn.png",
                        description = "Teacher Avatar",
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${material.teacher.firstName} ${material.teacher.lastName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = material.createdAt.formatDate(),
                        fontSize = 12.sp,
                        color = MutedForegroundColor
                    )
                }

                // File type badge
                FileTypeBadge(fileUrl = material.fileUrl)
            }

            // File preview section
            MaterialFilePreview(
                fileUrl = material.fileUrl,
                fileName = material.name
            )

            // Content section
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = material.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (material.description.isNotEmpty()) {
                    Text(
                        text = material.description,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MutedForegroundColor,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both
                            )
                        )
                    )
                }
            }

            // Footer with file info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getFileIcon(material.fileUrl),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = getFileIconColor(material.fileUrl)
                    )
                    Text(
                        text = getFileTypeText(material.fileUrl),
                        fontSize = 12.sp,
                        color = MutedForegroundColor,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Blue.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Download,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.Blue
                        )
                        Text(
                            text = "Open",
                            fontSize = 12.sp,
                            color = Color.Blue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialFilePreview(
    fileUrl: String,
    fileName: String
) {
    val fileType = getFileType(fileUrl)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(12.dp),
        color = MutedColor.copy(alpha = 0.2f)
    ) {
        when (fileType) {
            FileType.IMAGE -> {
                CacheImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    imageUrl = fileUrl,
                    description = fileName,
                    contentScale = ContentScale.Crop
                )
            }
            FileType.PDF -> {
                PdfPreviewCard()
            }
            FileType.DOCUMENT -> {
                DocumentPreviewCard(fileType = fileType)
            }
            FileType.UNKNOWN -> {
                UnknownFilePreviewCard()
            }
        }
    }
}

@Composable
fun FileTypeBadge(fileUrl: String) {
    val fileType = getFileType(fileUrl)
    val (backgroundColor, textColor, text) = when (fileType) {
        FileType.PDF -> Triple(
            Color(0xFFEF4444).copy(alpha = 0.1f),
            Color(0xFFEF4444),
            "PDF"
        )
        FileType.IMAGE -> Triple(
            Color(0xFF10B981).copy(alpha = 0.1f),
            Color(0xFF10B981),
            "IMG"
        )
        FileType.DOCUMENT -> Triple(
            Color(0xFF3B82F6).copy(alpha = 0.1f),
            Color(0xFF3B82F6),
            "DOC"
        )
        FileType.UNKNOWN -> Triple(
            Color(0xFF6B7280).copy(alpha = 0.1f),
            Color(0xFF6B7280),
            "FILE"
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
fun PdfPreviewCard() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
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

@Composable
fun DocumentPreviewCard(fileType: FileType) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF3B82F6).copy(alpha = 0.1f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Lucide.FileText,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color(0xFF3B82F6)
                    )
                }
            }
            Text(
                text = when (fileType) {
                    FileType.DOCUMENT -> "Document"
                    else -> "File"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MutedForegroundColor
            )
        }
    }
}

@Composable
fun UnknownFilePreviewCard() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF6B7280).copy(alpha = 0.1f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Lucide.File,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color(0xFF6B7280)
                    )
                }
            }
            Text(
                text = "File",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MutedForegroundColor
            )
        }
    }
}

enum class FileType {
    PDF, IMAGE, DOCUMENT, UNKNOWN
}

fun getFileType(fileUrl: String): FileType {
    val extension = fileUrl.substringAfterLast('.', "").lowercase()
    return when (extension) {
        "pdf" -> FileType.PDF
        "jpg", "jpeg", "png", "gif", "bmp", "webp" -> FileType.IMAGE
        "doc", "docx", "txt", "rtf" -> FileType.DOCUMENT
        else -> FileType.UNKNOWN
    }
}

fun getFileIcon(fileUrl: String): ImageVector {
    return when (getFileType(fileUrl)) {
        FileType.PDF -> Lucide.FileText
        FileType.IMAGE -> Lucide.Image
        FileType.DOCUMENT -> Lucide.FileText
        FileType.UNKNOWN -> Lucide.File
    }
}

fun getFileIconColor(fileUrl: String): Color {
    return when (getFileType(fileUrl)) {
        FileType.PDF -> Color(0xFFEF4444)
        FileType.IMAGE -> Color(0xFF10B981)
        FileType.DOCUMENT -> Color(0xFF3B82F6)
        FileType.UNKNOWN -> Color(0xFF6B7280)
    }
}

fun getFileTypeText(fileUrl: String): String {
    val extension = fileUrl.substringAfterLast('.', "").uppercase()
    return if (extension.isNotEmpty()) extension else "FILE"
}
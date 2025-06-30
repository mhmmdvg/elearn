package com.elearn.presentation.ui.screens.details.course.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.composables.icons.lucide.Download
import com.composables.icons.lucide.ExternalLink
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

@Composable
fun EnhancedMaterialCard(
    material: MaterialData,
    onClick: () -> Unit
) {
    val context = LocalContext.current

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
                    color = Color.Blue.copy(alpha = 0.1f),
                    onClick = {
                        openFile(context, material.fileUrl, material.name)
                    },
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isWebUrl(material.fileUrl)) Lucide.ExternalLink else Lucide.Download,
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

// File opening functionality
fun isWebUrl(url: String): Boolean {
    return url.startsWith("http://") || url.startsWith("https://")
}

fun openFile(context: Context, fileUrl: String, fileName: String) {
    try {
        if (isWebUrl(fileUrl)) {
            // For web URLs, open directly in browser or appropriate app
            openWebFile(context, fileUrl)
        } else {
            // For local files, open with appropriate app
            openLocalFile(context, fileUrl, fileName)
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun openWebFile(context: Context, fileUrl: String) {
    val fileType = getFileType(fileUrl)

    when (fileType) {
        FileType.IMAGE -> {
            // Open image in a custom image viewer or gallery app
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(fileUrl)
                type = "image/*"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Fallback to browser
                openInBrowser(context, fileUrl)
            }
        }

        FileType.PDF -> {
            // Try to open PDF with a PDF viewer
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(fileUrl)
                type = "application/pdf"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Fallback: download and open or open in browser
                downloadAndOpenFile(context, fileUrl, "document.pdf")
            }
        }

        FileType.DOCUMENT -> {
            // For documents, try to open with appropriate app or download first
            downloadAndOpenFile(context, fileUrl, "document.${getFileExtension(fileUrl)}")
        }

        FileType.UNKNOWN -> {
            // Open in browser as fallback
            openInBrowser(context, fileUrl)
        }
    }
}

private fun openLocalFile(context: Context, filePath: String, fileName: String) {
    val file = File(filePath)
    if (!file.exists()) {
        Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
        return
    }

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider", // Make sure you have FileProvider configured
        file
    )

    val mimeType = getMimeType(filePath)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No app available to open this file type", Toast.LENGTH_SHORT).show()
    }
}

private fun openInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No browser available", Toast.LENGTH_SHORT).show()
    }
}

private fun downloadAndOpenFile(context: Context, fileUrl: String, fileName: String) {
    Toast.makeText(context, "Downloading file...", Toast.LENGTH_SHORT).show()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val file = downloadFile(context, fileUrl, fileName)

            withContext(Dispatchers.Main) {
                if (file != null) {
                    openLocalFile(context, file.absolutePath, fileName)
                } else {
                    Toast.makeText(context, "Failed to download file", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private suspend fun downloadFile(context: Context, fileUrl: String, fileName: String): File? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(fileUrl)
            val connection = url.openConnection()
            connection.connect()

            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            val inputStream = connection.getInputStream()

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            file
        } catch (e: Exception) {
            null
        }
    }
}

private fun getMimeType(filePath: String): String {
    val extension = filePath.substringAfterLast('.', "").lowercase()
    return when (extension) {
        "pdf" -> "application/pdf"
        "doc" -> "application/msword"
        "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        "txt" -> "text/plain"
        "rtf" -> "application/rtf"
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "gif" -> "image/gif"
        "bmp" -> "image/bmp"
        "webp" -> "image/webp"
        else -> "*/*"
    }
}

private fun getFileExtension(fileUrl: String): String {
    return fileUrl.substringAfterLast('.', "")
}
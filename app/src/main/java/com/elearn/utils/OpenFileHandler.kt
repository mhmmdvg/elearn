package com.elearn.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.elearn.presentation.ui.screens.details.course.components.FileType
import com.elearn.presentation.ui.screens.details.course.components.getFileType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

object OpenFileHandler {
    private val downloadedFiles = mutableMapOf<String, String>()

    fun isWebUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://")
    }

    fun openFile(context: Context, fileUrl: String, fileName: String) {
        try {
            if (isWebUrl(fileUrl)) {
                val fileType = getFileType(fileUrl)

                when (fileType) {
                    FileType.IMAGE -> {
                        openImageUrl(context, fileUrl)
                    }

                    FileType.PDF -> {
                        // Always download PDFs for reliable opening
                        downloadAndOpenFile(context, fileUrl, fileName.ifEmpty { "document.pdf" })
                    }

                    FileType.DOCUMENT -> {
                        downloadAndOpenFile(context, fileUrl, fileName)
                    }

                    FileType.UNKNOWN -> {
                        if (looksLikeImageUrl(fileUrl)) {
                            openImageUrl(context, fileUrl)
                        } else {
                            openInBrowser(context, fileUrl)
                        }
                    }
                }
            } else {
                openLocalFile(context, fileUrl, fileName)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun looksLikeImageUrl(url: String): Boolean {
        val lowerUrl = url.lowercase()
        return lowerUrl.contains("image") ||
                lowerUrl.contains("photo") ||
                lowerUrl.contains("picture") ||
                lowerUrl.contains("img") ||
                lowerUrl.contains("imgur.com") ||
                lowerUrl.contains("pinterest.com") ||
                lowerUrl.contains("instagram.com") ||
                lowerUrl.contains("unsplash.com") ||
                lowerUrl.contains("format=jpg") ||
                lowerUrl.contains("format=png") ||
                lowerUrl.contains("format=webp")
    }

    private fun openImageUrl(context: Context, fileUrl: String) {
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(fileUrl)
                type = "image/*"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val imageViewerApps = context.packageManager.queryIntentActivities(
                viewIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )

            if (imageViewerApps.isNotEmpty()) {
                val preferredApp = imageViewerApps.find { resolveInfo ->
                    val packageName = resolveInfo.activityInfo.packageName
                    packageName.contains("gallery", ignoreCase = true) ||
                            packageName.contains("photos", ignoreCase = true) ||
                            packageName.contains("image", ignoreCase = true) ||
                            packageName.contains("media", ignoreCase = true)
                }

                if (preferredApp != null) {
                    viewIntent.setClassName(
                        preferredApp.activityInfo.packageName,
                        preferredApp.activityInfo.name
                    )
                }

                context.startActivity(viewIntent)
            } else {
                openImageInBrowser(context, fileUrl)
            }
        } catch (e: Exception) {
            openImageInBrowser(context, fileUrl)
        }
    }

    private fun openImageInBrowser(context: Context, fileUrl: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (browserIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(browserIntent)
            } else {
                Toast.makeText(context, "No app available to view images", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to open image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openLocalFile(context: Context, filePath: String, fileName: String) {
        try {
            val file = File(filePath)
            if (!file.exists() || file.length() == 0L) {
                Toast.makeText(context, "File not found or empty", Toast.LENGTH_SHORT).show()
                return
            }

            // Create new file URI each time to avoid permission issues
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val mimeType = getMimeType(filePath)

            // Create intent with proper flags
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            // Try to start the activity
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                // If that fails, try with chooser
                val chooserIntent = Intent.createChooser(intent, "Open with")
                chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                try {
                    context.startActivity(chooserIntent)
                } catch (e2: Exception) {
                    Toast.makeText(
                        context,
                        "No app available to open this file type",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } catch (e: Exception) {
            Toast.makeText(context, "Failed to open file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openInBrowser(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "No browser available", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to open in browser: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun downloadAndOpenFile(context: Context, fileUrl: String, fileName: String) {
        // Generate a unique file key based on URL
        val fileKey = generateFileKey(fileUrl)
        val cachedFilePath = downloadedFiles[fileKey]

        // Check if we have a cached file and it still exists
        if (cachedFilePath != null) {
            val cachedFile = File(cachedFilePath)
            if (cachedFile.exists() && cachedFile.length() > 0 && cachedFile.canRead()) {
                // File exists and is valid, open it
                openLocalFile(context, cachedFilePath, fileName)
                return
            } else {
                // File is corrupted or missing, remove from cache
                downloadedFiles.remove(fileKey)
                if (cachedFile.exists()) {
                    cachedFile.delete()
                }
            }
        }

        // Need to download the file
        Toast.makeText(context, "Downloading file...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val downloadedFile = downloadFile(context, fileUrl, fileName)

                withContext(Dispatchers.Main) {
                    if (downloadedFile != null && downloadedFile.exists() && downloadedFile.length() > 0) {
                        // Store the file path in cache
                        downloadedFiles[fileKey] = downloadedFile.absolutePath

                        Toast.makeText(context, "Download completed", Toast.LENGTH_SHORT).show()
                        openLocalFile(context, downloadedFile.absolutePath, fileName)
                    } else {
                        Toast.makeText(context, "Failed to download file", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private suspend fun downloadFile(context: Context, fileUrl: String, fileName: String): File? {
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(fileUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 30000
                connection.setRequestProperty("User-Agent", "ELearnApp/1.0")
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("Server returned HTTP ${connection.responseCode}")
                }

                // Use app's cache directory instead of external files
                val cacheDir = File(context.cacheDir, "downloaded_files")
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs()
                }

                // Generate unique filename
                val fileKey = generateFileKey(fileUrl)
                val extension = getFileExtension(fileName)
                val finalFileName = "${fileKey}${if (extension.isNotEmpty()) ".$extension" else ""}"

                val outputFile = File(cacheDir, finalFileName)
                val tempFile = File(cacheDir, "${finalFileName}.tmp")

                // Download to temp file first
                connection.inputStream.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                        }
                    }
                }

                // Verify and move to final location
                if (tempFile.exists() && tempFile.length() > 0) {
                    if (outputFile.exists()) {
                        outputFile.delete()
                    }

                    if (tempFile.renameTo(outputFile)) {
                        outputFile
                    } else {
                        // If rename fails, try copy
                        tempFile.inputStream().use { input ->
                            FileOutputStream(outputFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        tempFile.delete()
                        if (outputFile.exists() && outputFile.length() > 0) outputFile else null
                    }
                } else {
                    tempFile.delete()
                    null
                }

            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun generateFileKey(url: String): String {
        return try {
            val digest = MessageDigest.getInstance("MD5")
            val hashBytes = digest.digest(url.toByteArray())
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            url.hashCode().toString()
        }
    }

    private fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "")
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
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "ppt" -> "application/vnd.ms-powerpoint"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            else -> "*/*"
        }
    }

    // Clear cache
    fun clearDownloadCache(context: Context) {
        try {
            downloadedFiles.clear()
            val cacheDir = File(context.cacheDir, "downloaded_files")
            if (cacheDir.exists()) {
                cacheDir.listFiles()?.forEach { it.delete() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Clean old files (older than 7 days)
    fun cleanupOldFiles(context: Context) {
        try {
            val cacheDir = File(context.cacheDir, "downloaded_files")
            if (cacheDir.exists()) {
                val currentTime = System.currentTimeMillis()
                val maxAge = 7 * 24 * 60 * 60 * 1000L // 7 days

                cacheDir.listFiles()?.forEach { file ->
                    if (currentTime - file.lastModified() > maxAge) {
                        file.delete()
                        // Remove from our cache map
                        downloadedFiles.values.removeAll { it == file.absolutePath }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
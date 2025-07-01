package com.elearn.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

object ImageDownloadHandler {

    fun downloadImage(
        context: Context,
        imageUrl: String,
        fileName: String = "",
        onDownloadStart: (() -> Unit)? = null,
        onDownloadComplete: ((File?) -> Unit)? = null,
        onDownloadError: ((String) -> Unit)? = null
    ) {
        onDownloadStart?.invoke()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val downloadedFile = downloadImageFile(context, imageUrl, fileName)

                withContext(Dispatchers.Main) {
                    if (downloadedFile != null && downloadedFile.exists()) {
                        Toast.makeText(context, "Image downloaded successfully", Toast.LENGTH_SHORT).show()
                        onDownloadComplete?.invoke(downloadedFile)

                        // Optionally notify gallery about the new image
                        notifyGallery(context, downloadedFile)
                    } else {
                        val errorMsg = "Failed to download image"
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        onDownloadError?.invoke(errorMsg)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val errorMsg = "Download failed: ${e.message}"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    onDownloadError?.invoke(errorMsg)
                }
            }
        }
    }

    private suspend fun downloadImageFile(
        context: Context,
        imageUrl: String,
        fileName: String
    ): File? {
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(imageUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 30000
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("HTTP error code: $responseCode")
                }

                // Generate file name if not provided
                val finalFileName = if (fileName.isNotEmpty()) {
                    ensureImageExtension(fileName)
                } else {
                    generateImageFileName(imageUrl)
                }

                // Create Pictures directory for images
                val picturesDir = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "ELearn_Images"
                )
                if (!picturesDir.exists()) {
                    picturesDir.mkdirs()
                }

                val file = File(picturesDir, finalFileName)
                val outputStream = FileOutputStream(file)
                val inputStream: InputStream = connection.inputStream

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                if (file.length() > 0) {
                    file
                } else {
                    file.delete()
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

    private fun ensureImageExtension(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return if (extension in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")) {
            fileName
        } else {
            "$fileName.jpg" // Default to jpg if no valid extension
        }
    }

    private fun generateImageFileName(imageUrl: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val extension = getImageExtensionFromUrl(imageUrl)
        return "ELearn_Image_$timestamp.$extension"
    }

    private fun getImageExtensionFromUrl(url: String): String {
        val extension = url.substringAfterLast('.', "").lowercase()
        return if (extension in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")) {
            extension
        } else {
            "jpg" // Default extension
        }
    }

    private fun notifyGallery(context: Context, file: File) {
        try {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val uri = Uri.fromFile(file)
            mediaScanIntent.data = uri
            context.sendBroadcast(mediaScanIntent)
        } catch (e: Exception) {
            // Ignore gallery notification errors
        }
    }

    fun shareImage(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Share Image")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            if (shareIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(chooser)
            } else {
                Toast.makeText(context, "No app available to share image", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to share image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveImageToGallery(context: Context, bitmap: Bitmap, fileName: String = ""): Uri? {
        return try {
            val finalFileName = if (fileName.isNotEmpty()) {
                ensureImageExtension(fileName)
            } else {
                generateImageFileName("")
            }

            val contentValues = android.content.ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, finalFileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ELearn")
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                }
            }

            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
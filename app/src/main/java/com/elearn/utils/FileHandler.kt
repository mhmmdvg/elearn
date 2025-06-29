package com.elearn.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream

object FileHandler {

    fun getMimeType(context: Context, uri: Uri): String {
        context.contentResolver.getType(uri)?.let { mimeType ->
            if (mimeType != "*/*" && mimeType.isNotBlank()) {
                return mimeType
            }
        }

        val fileName = getFileName(context, uri)
        val extension = fileName.substringAfterLast('.', "").lowercase()

        val mimeTypeFromExtension = when (extension) {
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            else -> {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }
        }
        return mimeTypeFromExtension ?: "application/octet-stream"
    }


    fun uriToFile(context: Context, uri: Uri, defaultFileName: String = "temp_file"): File {
        val contentResolver = context.contentResolver
        val fileName = getFileName(context, uri, defaultFileName)
        val tempFile = File(context.cacheDir, fileName)

        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return tempFile
    }

    fun getFileName(context: Context, uri: Uri, fileName: String = "temp_file"): String {
        var fileName = fileName
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex =
                    it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    fileName = it.getString(displayNameIndex) ?: fileName
                }
            }
        }

        return fileName
    }
}
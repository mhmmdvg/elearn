package com.elearn.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (error: Exception) {
            dateString
        }
    }
}

fun String.formatDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        val date = inputFormat.parse(this)
        outputFormat.format(date ?: Date())
    } catch (error: Exception) {
        this
    }
}
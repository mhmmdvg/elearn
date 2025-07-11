package com.elearn.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object TimeUtils {

    // Parse UTC time from backend and convert to local DateTime
    fun parseUtcToLocal(utcTimeString: String): LocalDateTime? {
        return try {
            if (utcTimeString.endsWith("Z")) {
                // Parse as UTC and convert to local
                Instant.parse(utcTimeString)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            } else {
                // Already local time
                LocalDateTime.parse(utcTimeString)
            }
        } catch (e: Exception) {
            null
        }
    }


    fun localDateTimeToUtcIso(localDateTime: LocalDateTime): String {
        return localDateTime
            .atZone(ZoneId.systemDefault()) // Convert to system timezone
            .withZoneSameInstant(ZoneId.of("UTC")) // Convert to UTC
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
    }

    // Convert UTC ISO string from backend to LocalDateTime for display
    fun utcIsoToLocalDateTime(utcIsoString: String): LocalDateTime? {
        return try {
            // Handle both formats: with and without milliseconds
            val formatter = if (utcIsoString.contains('.')) {
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            } else {
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            }

            ZonedDateTime.parse(utcIsoString, formatter)
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
        } catch (e: Exception) {
            null
        }
    }

    // Format LocalDateTime for display
    fun formatForDisplay(localDateTime: LocalDateTime, pattern: String): String {
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern))
    }
}
package com.elearn.utils

import android.content.Context
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object LocationUtils {

    suspend fun getAddressFromCoordinates(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String? = withContext(Dispatchers.IO) {
        try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(context)
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    return@withContext buildString {
                        address.thoroughfare?.let { append(it) }
                        address.subThoroughfare?.let {
                            if (isNotEmpty()) append(" ")
                            append(it)
                        }

                        address.locality?.let {
                            if (isNotEmpty()) append(", ")
                            append(it)
                        }

                        address.adminArea?.let {
                            if (isNotEmpty()) append(", ")
                            append(it)
                        }

                        address.countryName?.let {
                            if (isNotEmpty()) append(", ")
                            append(it)
                        }
                    }
                }
            }
            null
        } catch (error: Exception) {
            error.printStackTrace()
            null
        }
    }

    fun formatCoordinates(latitude: Double, longitude: Double): String {
        val latDirection = if (latitude >= 0) "N" else "S"
        val lngDirection = if (longitude >= 0) "E" else "W"

        return "${String.format("%.6f", kotlin.math.abs(latitude))}°$latDirection, " +
                "${String.format("%.6f", kotlin.math.abs(longitude))}°$lngDirection"
    }

    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371000.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)

        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))

        return earthRadius * c
    }

    fun getAccuracyDescription(accuracy: Double?): String {
        return when {
            accuracy == null -> "Unknown"
            accuracy <= 5 -> "Excellent (±${accuracy.toInt()}m)"
            accuracy <= 10 -> "Good (±${accuracy.toInt()}m)"
            accuracy <= 20 -> "Fair (±${accuracy.toInt()}m)"
            else -> "Poor (±${accuracy.toInt()}m)"
        }
    }
}
package com.elearn.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val cancellationTokenSource = CancellationTokenSource()

    suspend fun getCurrentLocation(): Result<Location> =
        suspendCancellableCoroutine { continuation ->
            if (!hasLocationPermission()) {
                continuation.resume(Result.failure(SecurityException("Location permission not granted")))
                return@suspendCancellableCoroutine
            }

            if (!isLocationEnabled()) {
                continuation.resume(Result.failure(SecurityException("Location is not enabled")))
                return@suspendCancellableCoroutine
            }

            try {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        continuation.resume(Result.success(location))
                    } else {
                        continuation.resume(Result.failure(Exception("Unable to get location")))
                    }
                }.addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }
            } catch (e: SecurityException) {
                continuation.resume(Result.failure(e))
            }

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }


    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}
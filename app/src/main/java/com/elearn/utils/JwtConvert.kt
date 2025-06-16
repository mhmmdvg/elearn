package com.elearn.utils

import android.util.Base64
import android.util.Log
import org.json.JSONObject

object JwtConvert {

    private const val TAG = "JWTUtils"

    fun decodeToken(token: String): JSONObject? {
        try {
            val parts = token.split(".")

            if (parts.size != 3) {
                Log.e(TAG, "Invalid token format")
                return null
            }

            val payload = parts[1]
            val decodedPayload = String(Base64.decode(payload, Base64.URL_SAFE))

            return JSONObject(decodedPayload)
        } catch (e: Exception) {
            Log.e(TAG, "Error decoding token", e)
            return null
        }
    }

    fun getClaim(token: String, claimName: String): String? {
        try {
            val payload = decodeToken(token) ?: return null
            return if (payload.has(claimName)) payload.getString(claimName) else null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting claim: $claimName", e)
            return null
        }
    }

    fun getUserId(token: String): String? {
        return getClaim(token, "sub")
    }
}
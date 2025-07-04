package com.elearn.data.remote.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.elearn.data.remote.api.UserApi
import com.elearn.domain.model.CachedUserData
import com.elearn.domain.model.ErrorResponse
import com.elearn.domain.model.UpdateImageResponse
import com.elearn.domain.model.UserDescriptionReq
import com.elearn.domain.model.UserDescriptionRes
import com.elearn.domain.model.UserNameRequest
import com.elearn.domain.model.UserNameResponse
import com.elearn.domain.model.UserResponse
import com.elearn.utils.FileHandler.getMimeType
import com.elearn.utils.FileHandler.uriToFile
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi
) {
    private val userCache = mutableMapOf<String, CachedUserData>()
    private val cacheExpirationTime = 10 * 60 * 1000L

    suspend fun fetchUserInfo(id: String): Result<UserResponse> {

        val cachedData = userCache[id]
        if (cachedData != null && isCacheValid(cachedData.timestamp)) {
            return Result.success(cachedData.data)
        }

        return try {
            val res = userApi.userInfo(id)

            if (res.isSuccessful) {
                res.body()?.let { userResponse ->
                    userCache[id] = CachedUserData(
                        data = userResponse,
                        timestamp = System.currentTimeMillis()
                    )
                    Result.success(userResponse)
                } ?: Result.failure(Exception("Empty response Body"))
            } else {
                val errorBody = res.errorBody()?.string()
                val errorResponse = try {
                    Json.decodeFromString<ErrorResponse>(errorBody ?: "")
                } catch (e: Exception) {
                    ErrorResponse("Failed to parse error response")
                }
                Result.failure(Exception(errorResponse.error))
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    suspend fun putUserName(id: String, req: UserNameRequest): Result<UserNameResponse> {
        return try {
            val res = userApi.updateUserName(id, req)

            if (res.isSuccessful) {
                res.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = res.errorBody()?.string()
                val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody ?: "")
                Result.failure(Exception(errorResponse.error))
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    suspend fun putUserDescription(
        id: String,
        req: UserDescriptionReq
    ): Result<UserDescriptionRes> {
        return try {
            val res = userApi.updateDescription(id, req)

            if (res.isSuccessful) {
                res.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = res.errorBody()?.string()
                val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody ?: "")
                Result.failure(Exception(errorResponse.error))
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    suspend fun updateProfileImage(
        context: Context,
        id: String,
        imageUri: Uri
    ): Result<UpdateImageResponse> {
        return try {
            val file = uriToFile(context, imageUri, "profile_image.jpg")
            val mimeType = getMimeType(context, imageUri)
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())

            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val res = userApi.updateProfileImage(id, imagePart)

            if (res.isSuccessful) {
                res.body()?.let { response ->
                    invalidateUserCache(id)
                    Result.success(response)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = res.errorBody()?.string()
                val errorResponse = try {
                    Json.decodeFromString<ErrorResponse>(errorBody ?: "")
                } catch (e: Exception) {
                    ErrorResponse("Failed to parse error response")
                }
                Result.failure(Exception(errorResponse.error))
            }
        } catch (error: Exception) {
            Log.e("UserRepository", "Error updating profile image", error)
            Result.failure(error)
        }
    }

    fun invalidateUserCache(userId: String) {
        userCache.remove(userId)
    }

    fun invalidateCaches() {
        userCache.clear()
    }

    fun getCachedUserInfo(id: String): UserResponse? {
        val cachedData = userCache[id]
        return if (cachedData != null && isCacheValid(cachedData.timestamp)) {
            cachedData.data
        } else null

    }

    private fun isCacheValid(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp < cacheExpirationTime
    }
}

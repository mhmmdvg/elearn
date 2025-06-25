package com.elearn.data.remote.repository

import android.content.Context
import android.net.Uri
import com.elearn.data.remote.api.MaterialApi
import com.elearn.domain.model.CachedMaterialData
import com.elearn.domain.model.CreateMaterialResponse
import com.elearn.domain.model.ErrorResponse
import com.elearn.domain.model.MaterialResponse
import com.elearn.utils.FileHandler.getMimeType
import com.elearn.utils.FileHandler.uriToFile
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaterialRepository @Inject constructor(
    private val materialApi: MaterialApi
) {
    private var _materialListCache: CachedMaterialData? = null
    private val cacheExpirationTime = 10 * 60 * 1000L

    suspend fun fetchMaterial(): Result<MaterialResponse> {
        val cachedData = _materialListCache
        if (cachedData != null && isCacheValid(cachedData.timestamp)) {
            return Result.success(cachedData.data)
        }

        return try {
            val res = materialApi.getMaterials()

            if (res.isSuccessful) {
                res.body()?.let {
                    _materialListCache = CachedMaterialData(
                        data = it,
                        timestamp = System.currentTimeMillis()
                    )
                    Result.success(it)
                } ?: Result.failure(Exception("Empty Response Body"))
            } else {
                val errorBody = res.errorBody()?.string()
                val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody ?: "")
                Result.failure(Exception(errorResponse.error))
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    suspend fun createMaterial(
        context: Context,
        fileUri: Uri,
        name: String,
        description: String?,
        classId: String
    ): Result<CreateMaterialResponse> {
        return try {
            val file = uriToFile(context, fileUri)
            val mimeType = getMimeType(context, fileUri)

            val fileRequestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)
            val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = description?.toRequestBody("text/plain".toMediaTypeOrNull())
            val classIdPart = classId.toRequestBody("text/plain".toMediaTypeOrNull())

            val res = materialApi.createMaterial(
                file = filePart,
                name = namePart,
                description = descriptionPart,
                classId = classIdPart
            )

            if (res.isSuccessful) {
                res.body()?.let {
                    invalidateMaterialCache()
                    Result.success(it)
                } ?: Result.failure(Exception("Empty Response Body"))
            } else {
                val errorBody = res.errorBody()?.string()
                val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody ?: "")
                Result.failure(Exception(errorResponse.error))
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    fun getCacheMaterialList(): MaterialResponse? {
        val cachedData = _materialListCache
        return if (cachedData != null && isCacheValid(cachedData.timestamp)) {
            cachedData.data
        } else null
    }

    fun invalidateMaterialCache() {
        _materialListCache = null
    }

    private fun isCacheValid(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp < cacheExpirationTime
    }

//    private fun getMimeType(context: Context, uri: Uri): String {
//        context.contentResolver.getType(uri)?.let { mimeType ->
//            if (mimeType != "*/*" && mimeType.isNotBlank()) {
//                return mimeType
//            }
//        }
//
//        val fileName = getFileName(context, uri)
//        val extension = fileName.substringAfterLast('.', "").lowercase()
//
//        val mimeTypeFromExtension = when (extension) {
//            "pdf" -> "application/pdf"
//            "doc" -> "application/msword"
//            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
//            "jpg", "jpeg" -> "image/jpeg"
//            "png" -> "image/png"
//            "gif" -> "image/gif"
//            "webp" -> "image/webp"
//            else -> {
//                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
//            }
//        }
//        return mimeTypeFromExtension ?: "application/octet-stream"
//    }
//
//    private fun uriToFile(context: Context, uri: Uri): File {
//        val contentResolver = context.contentResolver
//        val fileName = getFileName(context, uri)
//        val tempFile = File(context.cacheDir, fileName)
//
//        contentResolver.openInputStream(uri)?.use { inputStream ->
//            FileOutputStream(tempFile).use { outputStream ->
//                inputStream.copyTo(outputStream)
//            }
//        }
//
//        return tempFile
//    }
//
//    private fun getFileName(context: Context, uri: Uri): String {
//        var fileName = "temp_file"
//        val cursor = context.contentResolver.query(uri, null, null, null, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val displayNameIndex =
//                    it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
//                if (displayNameIndex != -1) {
//                    fileName = it.getString(displayNameIndex) ?: "temp_file"
//                }
//            }
//        }
//
//        return fileName
//    }
}
package com.elearn.data.remote.repository

import android.content.Context
import android.net.Uri
import com.elearn.data.remote.api.MaterialApi
import com.elearn.domain.model.CacheMaterialDetail
import com.elearn.domain.model.CachedMaterialData
import com.elearn.domain.model.CreateMaterialResponse
import com.elearn.domain.model.ErrorResponse
import com.elearn.domain.model.HTTPResponse
import com.elearn.domain.model.MaterialData
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
    private var _allMaterialsCache: CachedMaterialData? = null
    private var _filteredMaterialsCache: MutableMap<String, CachedMaterialData> = mutableMapOf()
    private var _materialDetailCache: MutableMap<String, CacheMaterialDetail> = mutableMapOf()

    private val cacheExpirationTime = 10 * 60 * 1000L

    suspend fun fetchMaterial(courseId: String? = null): Result<MaterialResponse> {
        return if (courseId.isNullOrEmpty()) {
            fetchAllMaterials()
        } else {
            fetchMaterialsByClassId(courseId)
        }
    }

    suspend fun fetchMaterialDetail(id: String): Result<HTTPResponse<MaterialData>> {
        val cachedData = _materialDetailCache[id]
        if (cachedData != null && isCacheValid(cachedData.timestamp)) {
            return Result.success(cachedData.data)
        }

        return try {
            val res = materialApi.getMaterialDetail(id)

            if (res.isSuccessful) {
                res.body()?.let {
                    _materialDetailCache[id] = CacheMaterialDetail(
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
                    invalidateAllMaterialCaches()
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

    suspend fun putMaterial(
        materialId: String,
        context: Context,
        fileUri: Uri? = null,
        name: String,
        description: String?,
    ): Result<CreateMaterialResponse> {
        return try {
            val filePart: MultipartBody.Part? = fileUri?.let { uri ->
                val file = uriToFile(context, uri)
                val mimeType = getMimeType(context, uri)
                val fileRequestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", file.name, fileRequestBody)
            }
            val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = description?.toRequestBody("text/plain".toMediaTypeOrNull())

            val res = materialApi.putMaterial(
                id = materialId,
                file = filePart,
                name = namePart,
                description = descriptionPart,
            )

            if (res.isSuccessful) {
                res.body()?.let {
                    invalidateAllMaterialCaches()
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

    suspend fun deleteMaterial(materialId: String): Result<HTTPResponse<MaterialData>> {
        return try {
            val res = materialApi.deleteMaterial(materialId)

            if (res.isSuccessful) {
                res.body()?.let {
                    invalidateAllMaterialCaches()
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


    fun getCacheMaterialList(classId: String? = null): MaterialResponse? {
        return if (classId.isNullOrEmpty()) {
            val cachedData = _allMaterialsCache
            if (cachedData != null && isCacheValid(cachedData.timestamp)) {
                cachedData.data
            } else null
        } else {
            // Get filtered materials cache
            val cachedData = _filteredMaterialsCache[classId]
            if (cachedData != null && isCacheValid(cachedData.timestamp)) {
                cachedData.data
            } else null
        }
    }

    fun invalidateMaterialCache(classId: String? = null) {
        if (classId.isNullOrEmpty()) {
            _allMaterialsCache = null
        } else {
            _filteredMaterialsCache.remove(classId)
        }
    }

    fun invalidateMaterialDetailCache(id: String) {
        _materialDetailCache.remove(id)
    }

    fun invalidateAllMaterialCaches() {
        _allMaterialsCache = null
        _filteredMaterialsCache.clear()
        _materialDetailCache.clear()
    }

    private suspend fun fetchAllMaterials(): Result<MaterialResponse> {
        val cachedData = _allMaterialsCache
        if (cachedData != null && isCacheValid(cachedData.timestamp)) {
            return Result.success(cachedData.data)
        }

        return try {
            val res = materialApi.getMaterials(null)

            if (res.isSuccessful) {
                res.body()?.let { materialResponse ->
                    _allMaterialsCache = CachedMaterialData(
                        data = materialResponse,
                        timestamp = System.currentTimeMillis()
                    )
                    Result.success(materialResponse)
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

    private suspend fun fetchMaterialsByClassId(classId: String): Result<MaterialResponse> {
        val cachedData = _filteredMaterialsCache[classId]
        if (cachedData != null && isCacheValid(cachedData.timestamp)) {
            return Result.success(cachedData.data)
        }

        return try {
            val res = materialApi.getMaterials(classId)

            if (res.isSuccessful) {
                res.body()?.let { materialResponse ->
                    _filteredMaterialsCache[classId] = CachedMaterialData(
                        data = materialResponse,
                        timestamp = System.currentTimeMillis()
                    )
                    Result.success(materialResponse)
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

    private fun isCacheValid(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp < cacheExpirationTime
    }
}
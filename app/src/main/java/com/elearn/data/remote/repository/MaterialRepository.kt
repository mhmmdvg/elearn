package com.elearn.data.remote.repository

import android.util.Log
import com.elearn.data.remote.api.MaterialApi
import com.elearn.domain.model.CourseResponse
import com.elearn.domain.model.ErrorResponse
import com.elearn.domain.model.MaterialResponse
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaterialRepository @Inject constructor(
    private val materialApi: MaterialApi
) {
    private var _materialListCache: MaterialResponse? = null

    suspend fun fetchMaterial(): Result<MaterialResponse> {
        _materialListCache?.let {
            return Result.success(it)
        }

        return try {
            val res = materialApi.getMaterials()

            if (res.isSuccessful) {
                res.body()?.let {
                    _materialListCache = it
                    Log.d("check", it.toString())
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
        return if (cachedData != null) cachedData else null
    }

    fun invalidateMaterialCache() {
        _materialListCache = null
    }
}
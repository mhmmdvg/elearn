package com.elearn.data.remote.repository

import com.elearn.data.remote.api.CourseApi
import com.elearn.data.remote.local.TokenManager
import com.elearn.domain.model.CourseResponse
import com.elearn.domain.model.ErrorResponse
import kotlinx.serialization.json.Json
import javax.inject.Inject

class CourseRepository @Inject constructor(
    private val courseApi: CourseApi
) {
    suspend fun fetchCourse(): Result<CourseResponse> {
        return try {
            val res = courseApi.getCourses()

            if (res.isSuccessful) {
                res.body()?.let {
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
}
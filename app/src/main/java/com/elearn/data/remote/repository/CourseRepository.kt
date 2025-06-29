package com.elearn.data.remote.repository

import com.elearn.data.remote.api.CourseApi
import com.elearn.domain.model.CourseData
import com.elearn.domain.model.CourseJoinRequest
import com.elearn.domain.model.CourseJoinResponse
import com.elearn.domain.model.CourseResponse
import com.elearn.domain.model.CreateCourseRequest
import com.elearn.domain.model.CreateCourseResponse
import com.elearn.domain.model.ErrorResponse
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    private val courseApi: CourseApi
) {
    private var courseListCache: CourseResponse<List<CourseData>>? = null

    suspend fun fetchCourse(): Result<CourseResponse<List<CourseData>>> {

        courseListCache?.let {
            return Result.success(it)
        }

        return try {
            val res = courseApi.getCourses()

            if (res.isSuccessful) {
                res.body()?.let {
                    courseListCache = it
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

    suspend fun fetchCourseDetail(courseId: String): Result<CourseResponse<CourseData>> {
        return try {
            val res = courseApi.getCourseDetail(courseId)

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

    suspend fun createCourse(payload: CreateCourseRequest): Result<CreateCourseResponse> {
        return try {
            val res = courseApi.createCourse(payload)

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

    suspend fun postJoinCourse(code: String): Result<CourseJoinResponse> {
        return try {
            val res = courseApi.joinCourse(CourseJoinRequest(code))

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

    suspend fun updateCourse(courseId: String, payload: CreateCourseRequest): Result<CreateCourseResponse> {
        return try {
            val res = courseApi.updateCourse(courseId, payload)

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

    fun getCacheCourseList(): CourseResponse<List<CourseData>>? {
        val cachedData = courseListCache
        return if (cachedData != null) cachedData else null
    }

    fun invalidateCourseCache() {
        courseListCache = null
    }
}
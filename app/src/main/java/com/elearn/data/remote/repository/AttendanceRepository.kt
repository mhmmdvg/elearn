package com.elearn.data.remote.repository

import com.elearn.data.remote.api.AttendanceApi
import com.elearn.domain.model.AttendanceCheckinData
import com.elearn.domain.model.AttendanceCheckinReq
import com.elearn.domain.model.AttendanceSessionsData
import com.elearn.domain.model.AttendanceSessionsReq
import com.elearn.domain.model.CachedAttendanceSession
import com.elearn.domain.model.ErrorResponse
import com.elearn.domain.model.HTTPResponse
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    private val attendanceApi: AttendanceApi
) {
    private var _attendanceSessionsCache: MutableMap<String, CachedAttendanceSession> =
        mutableMapOf()

    private val _cacheExpirationTime = 10 * 60 * 1000L

    suspend fun fetchAttendanceSessions(classId: String): Result<HTTPResponse<List<AttendanceSessionsData>>> {
        val cachedData = _attendanceSessionsCache[classId]
        if (cachedData != null && isCacheValid(cachedData.timestamp)) {
            return Result.success(cachedData.data)
        }

        return try {
            val res = attendanceApi.getAttendanceSession(classId)

            if (res.isSuccessful) {
                res.body()?.let {
                    _attendanceSessionsCache[classId] = CachedAttendanceSession(
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

    suspend fun createAttendanceSession(
        payload: AttendanceSessionsReq
    ): Result<HTTPResponse<AttendanceSessionsData>> {
        return try {
            val res = attendanceApi.postAttendanceSession(payload)

            if (res.isSuccessful) {
                res.body()?.let {
                    invalidateAttendanceSessionCache(it.data.id)
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

    suspend fun createAttendanceCheckin(
        payload: AttendanceCheckinReq
    ): Result<HTTPResponse<AttendanceCheckinData>> {
        return try {
            val res = attendanceApi.postCheckin(payload)

            if (res.isSuccessful) {
                res.body()?.let { result ->
                    Result.success(result)
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

    fun invalidateAttendanceSessionCache(classId: String) {
        _attendanceSessionsCache.remove(classId)
    }

    private fun isCacheValid(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp < _cacheExpirationTime
    }

}
package com.elearn.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceSessionsData(
    val id: String,
    val classId: String,
    val teacherId: String,
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val requireLocation: Boolean,
    val createdAt: String,
    val studentCount: Int,
    val attendanceCount: Int
)

@Serializable
data class AttendanceCheckinData(
    val id: String,
    val sessionId: String,
    val studentId: String,
    val classId: String,
    val checkInTime: String,
    val checkoutTime: String?,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val notes: String,
    val createdAt: String
)

@Serializable
data class AttendanceSessionsReq(
    val classId: String,
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val requireLocation: Boolean
)

@Serializable
data class AttendanceCheckinReq(
    val sessionId: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val notes: String
)

data class CachedAttendanceSession(
    val data: HTTPResponse<List<AttendanceSessionsData>>,
    val timestamp: Long
)
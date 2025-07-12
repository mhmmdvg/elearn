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
    val hasCheckedIn: Boolean,
    val studentCount: Int,
    val attendanceCount: Int
)

@Serializable
data class AttendanceSessionDetailRes(
    val session: AttendanceSessionDetailData,
    val summary: AttendanceSessionSummary,
    val checkedInStudents: List<AttendanceStudent>,
    val notChekedInStudents: List<AttendanceStudent>
)

@Serializable
data class AttendanceSessionDetailData(
    val id: String,
    val title: String,
    val startTime: String,
    val endTime: String,
    val isActive: Boolean,
    val requireLocation: Boolean,
    val classId: String,
    val className: String,
)
@Serializable
data class AttendanceSessionSummary(
    val totalStudents: Int,
    val checkedIn: Int,
    val notCheckedIn: Int,
    val presentCount: Int,
    val lateCount: Int,
    val absentCount: Int,
    val attendanceRate: Int
)

@Serializable
data class AttendanceStudent(
    val studentId: String,
    val student: Student,
    val status: String,
    val isLate: Boolean,
    val checkedInAt: String?,
    val latitude: Double?,
    val longitude: Double?,
    val accuracy: Double?,
    val notes: String?
)

@Serializable
data class Student(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String
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
    val status: String,
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
    val notes: String?
)

data class CachedAttendanceSession<T>(
    val data: HTTPResponse<T>,
    val timestamp: Long
)

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float
)
package com.elearn.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CourseResponse<T>(
    val message: String,
    val data: T
)

@Serializable
data class CourseData(
    val id: String,
    val name: String,
    val code: String,
    val description: String,
    val teacherId: String,
    val isActive: Boolean,
    val _count: CountEnrollments?
)

@Serializable
data class CountEnrollments(
    val enrollments: Int,
)

@Serializable
data class CreateCourseRequest(
    val name: String? = null,
    val description: String? = null,
)

@Serializable
data class CourseJoinRequest(
    val code: String
)

@Serializable
data class CreateCourseResponse(
    val message: String,
    val data: CreateCourseData
)

@Serializable
data class CreateCourseData(
    val id: String,
    val code: String,
    val name: String,
    val description: String,
    val teacherId: String,
)

@Serializable
data class CourseJoinResponse(
    val message: String,
    val data: CourseJoinData
)

@Serializable
data class CourseJoinData(
    val enrollmentId: String,
    val course: CourseClassData,
    val enrollmentDate: String
)

@Serializable
data class CourseClassData(
    val id: String,
    val code: String,
    val name: String,
    val description: String,
    val teacher: String
)

//@Serializable
//data class CourseUpdateResponse(
//    val message: String,
//    val data: CourseUpdateNameData
//)
//
//@Serializable
//data class CourseUpdateNameData(
//    val name: String,
//    val updatedAt: String
//)
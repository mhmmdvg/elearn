package com.elearn.domain.model

data class CourseResponse(
    val message: String,
    val data: List<CourseData>
)

data class CourseData(
    val id: String,
    val name: String,
    val description: String,
    val teacherId: String,
    val isActive: Boolean
)

data class CreateCourseRequest(
    val name: String,
    val description: String?
)

data class CourseJoinRequest(
    val code: String
)

data class CreateCourseResponse(
    val message: String,
    val data: CreateCourseData
)

data class CreateCourseData(
    val id: String,
    val code: String,
    val name: String,
    val description: String,
    val teacherId: String,
)

data class CourseJoinResponse(
    val message: String,
    val data: CourseJoinData
)

data class CourseJoinData(
    val enrollmentId: String,
    val course: CourseClassData,
    val enrollmentDate: String
)

data class CourseClassData(
    val id: String,
    val code: String,
    val name: String,
    val description: String,
    val teacher: String
)
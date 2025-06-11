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

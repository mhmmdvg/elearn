package com.elearn.domain.model

import com.google.gson.annotations.SerializedName


data class MaterialResponse(
    val message: String,
    val data: MaterialList,
    val pagination: PaginationData
)

data class MaterialList(
    val materials: List<MaterialData>
)

data class MaterialData(
    val id: String,
    val name: String,
    val description: String,
    val classId: String,
    val teacherId: String,
    val fileUrl: String,
    val fileName: String,
    val fileType: String,
    val fileSize: Long,
    val mimeType: String,
    val isActive: Boolean,
    @SerializedName("class")
    val course: ClassData,
    val teacher: TeacherData
)

data class ClassData(
    val id: String,
    val name: String,
    val code: String,
)

data class TeacherData(
    val id: String,
    val imageUrl: String,
    val firstName: String,
    val lastName: String
)

data class PaginationData(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int
)
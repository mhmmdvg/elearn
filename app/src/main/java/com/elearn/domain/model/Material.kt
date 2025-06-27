package com.elearn.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class MaterialResponse(
    val message: String,
    val data: MaterialList,
    val pagination: PaginationData
)

@Serializable
data class MaterialList(
    val materials: List<MaterialData>
)

@Serializable
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
    val teacher: TeacherData,
    val createdAt: String
)

@Serializable
data class ClassData(
    val id: String,
    val name: String,
    val code: String,
)

@Serializable
data class TeacherData(
    val id: String,
    val imageUrl: String,
    val firstName: String,
    val lastName: String
)

@Serializable
data class PaginationData(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int
)

@Serializable
data class CreateMaterialResponse(
    val success: Boolean,
    val message: String,
    val data: Material?
)

@Serializable
data class Material(
    val id: String,
    val name: String,
    val description: String?,
    val fileUrl: String,
    val fileName: String,
    val fileSize: Long,
    val fileType: String,
    val mimeType: String,
    val teacherId: String,
    val classId: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val `class`: ClassInfo,
    val teacher: TeacherInfo
)

@Serializable
data class ClassInfo(
    val id: String,
    val name: String,
    val code: String
)

@Serializable
data class TeacherInfo(
    val id: String,
    val firstName: String,
    val lastName: String,
    val username: String
)

data class CachedMaterialData(
    val data: MaterialResponse,
    val timestamp: Long
)
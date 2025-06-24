package com.elearn.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val message: String,
    val data: UserData
)

@Serializable
data class UserNameRequest(
    val firstName: String,
    val lastName: String
)

@Serializable
data class UserDescriptionReq(
    val description: String
)

@Serializable
data class UserData(
    val id: String,
    val imageUrl: String?,
    val firstName: String,
    val lastName: String,
    val username: String,
    val description: String,
    val email: String,
    val role: UserRoleData
)

@Serializable
data class UserRoleData(
    val id: String,
    val name: String,
)

@Serializable
data class UserNameResponse(
    val message: String,
    val data: UserNameData
)

@Serializable
data class UserDescriptionRes(
    val message: String,
    val data: UserDescriptionData
)

@Serializable
data class UserNameData(
    val updatedAt: String,
    val firstName: String,
    val lastName: String,
)

@Serializable
data class UserDescriptionData(
    val updatedAt: String,
    val description: String,
)

data class CachedUserData(
    val data: UserResponse,
    val timestamp: Long
)

@Serializable
data class UpdateImageResponse(
    val message: String,
    val data: UpdateImageData
)

@Serializable
data class UpdateImageData(
    val imageUrl: String,
    val updatedAt: String
)
package com.elearn.domain.model

data class UserResponse(
    val message: String,
    val data: UserData
)

data class UserNameRequest(
    val firstName: String,
    val lastName: String
)

data class UserDescriptionReq(
    val description: String
)

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

data class UserRoleData(
    val id: String,
    val name: String,
)

data class UserNameResponse(
    val message: String,
    val data: UserNameData
)

data class UserDescriptionRes(
    val message: String,
    val data: UserDescriptionData
)

data class UserNameData(
    val updatedAt: String,
    val firstName: String,
    val lastName: String,
)

data class UserDescriptionData(
    val updatedAt: String,
    val description: String,
)
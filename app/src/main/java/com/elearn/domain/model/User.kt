package com.elearn.domain.model

data class UserResponse(
    val message: String,
    val data: UserData
)

data class UserData(
    val id: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val role: UserRoleData
)

data class UserRoleData(
    val id: String,
    val name: String,
)
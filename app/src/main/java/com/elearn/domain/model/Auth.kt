package com.elearn.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class EmailCheck(
    val email: String,
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val message: String,
    val data: LoginDataRes?
)

@Serializable
data class LoginDataRes(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val token: String
)

@Serializable
data class LogoutResponse(
    val message: String
)

@Serializable
data class ErrorResponse(
    val error: String
)

@Serializable
data class UserSharedPreferences(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val iat: Int,
    val exp: Int
)
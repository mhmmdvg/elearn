package com.elearn.domain.model

data class EmailCheck(
    val email: String,
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val data: LoginDataRes?
)

data class LoginDataRes(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val token: String
)

data class LogoutResponse(
    val message: String
)

data class ErrorResponse(
    val error: String
)

data class UserSharedPreferences(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val iat: Int,
    val exp: Int
)
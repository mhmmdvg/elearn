package com.elearn.data.remote.repository

import com.elearn.data.remote.api.AuthApi
import com.elearn.data.remote.local.TokenManager
import com.elearn.domain.model.EmailCheck
import com.elearn.domain.model.ErrorResponse
import com.elearn.domain.model.LoginRequest
import com.elearn.domain.model.LoginResponse
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {

    suspend fun emailCheck(email: String): Result<LoginResponse> {
        return try {
            val res = authApi.emailCheck(EmailCheck(email))

            if (res.isSuccessful) {
                res.body()?.let { response ->
                    Result.success(response)
                } ?: Result.failure(Exception("Empty Response Body"))
            } else {
                val errorBody = res.errorBody()?.string()
                val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody ?: "")
                Result.failure(Exception(errorResponse.error))
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val res = authApi.login(LoginRequest(email, password))

            if (res.isSuccessful) {
                res.body()?.let { response ->
                    tokenManager.saveToken(response.data?.token ?: "")
                    Result.success(response)
                } ?: Result.failure(Exception("Empty Response Body"))
            } else {
                val errorBody = res.errorBody()?.string()
                val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody ?: "")
                Result.failure(Exception(errorResponse.error))
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }
}